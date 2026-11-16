//! The per-connection session clock: refresh on input, read once input stops.

use crate::activity::{Activity, REFRESH_WINDOW};
use crate::data_services::{ClockClient, ClockError};
use std::sync::Arc;
use std::sync::Mutex;
use std::sync::atomic::{AtomicBool, Ordering};
use tokio::sync::Notify;
use tokio::time::Instant;

/// Starts the clock loop for one connection.
pub(crate) fn spawn_clock_task<C: ClockClient + 'static>(
    client: Arc<C>,
    session: String,
    activity: Arc<Mutex<Activity>>,
    notify: Arc<Notify>,
    alive: Arc<AtomicBool>,
) {
    tokio::spawn(run_clock(client, session, activity, notify, alive));
}

/// One touch per `REFRESH_WINDOW` while input flows; one read per window after it stops.
pub(crate) async fn run_clock<C: ClockClient>(
    client: Arc<C>,
    session: String,
    activity: Arc<Mutex<Activity>>,
    notify: Arc<Notify>,
    alive: Arc<AtomicBool>,
) {
    let mut last_touch: Option<Instant> = None;
    loop {
        // Wake on input, and at the latest one window after the last decision.
        let _ = tokio::time::timeout(REFRESH_WINDOW, notify.notified()).await;
        if !alive.load(Ordering::Relaxed) {
            return;
        }
        let active = activity
            .lock()
            .expect("activity mutex poisoned")
            .is_active(REFRESH_WINDOW);
        if active {
            if last_touch.is_some_and(|at| at.elapsed() < REFRESH_WINDOW) {
                continue;
            }
            // Count the attempt, not the success: a failed touch waits for the next window
            // instead of retrying on the next keystroke.
            last_touch = Some(Instant::now());
            match client.touch_session(&session).await {
                Ok(()) => log::debug!("Refreshed the clock for {session}"),
                Err(ClockError::SessionGone) => {
                    log::info!("Session {session} is gone; stopping the clock");
                    return;
                }
                Err(ClockError::Failed(err)) => {
                    log::warn!("Could not refresh the clock for {session}: {err:#}");
                }
            }
        } else {
            match client.session_clock(&session).await {
                Ok(clock) => {
                    log::info!(
                        "Session {session} idle: {:?} until hibernation, {:?} since input",
                        clock.remaining,
                        clock.since_input
                    );
                    if !clock.alive {
                        return;
                    }
                }
                Err(ClockError::SessionGone) => {
                    log::info!("Session {session} is gone; stopping the clock");
                    return;
                }
                Err(ClockError::Failed(err)) => {
                    log::warn!("Could not read the clock for {session}: {err:#}");
                }
            }
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::activity::{Activity, REFRESH_WINDOW};
    use crate::data_services::{ClockClient, ClockError, ClockResult, SessionClock};
    use color_eyre::eyre::eyre;
    use std::collections::VecDeque;
    use std::sync::atomic::{AtomicBool, AtomicUsize, Ordering};
    use std::sync::{Arc, Mutex};
    use std::time::Duration;
    use tokio::sync::Notify;
    use tokio::task::JoinHandle;

    #[derive(Clone, Copy)]
    enum Scripted {
        Ok,
        NotAlive,
        Gone,
        Failed,
    }

    #[derive(Default)]
    struct FakeClient {
        touches: AtomicUsize,
        reads: AtomicUsize,
        touch_script: Mutex<VecDeque<Scripted>>,
        read_script: Mutex<VecDeque<Scripted>>,
    }

    impl FakeClient {
        fn new() -> Arc<Self> {
            Arc::new(Self::default())
        }

        fn touches(&self) -> usize {
            self.touches.load(Ordering::Relaxed)
        }

        fn reads(&self) -> usize {
            self.reads.load(Ordering::Relaxed)
        }
    }

    #[expect(
        clippy::manual_async_fn,
        reason = "`async fn` in a trait cannot state the `Send` bound `tokio::spawn` needs"
    )]
    impl ClockClient for FakeClient {
        fn touch_session(&self, _session: &str) -> impl Future<Output = ClockResult<()>> + Send {
            async move {
                self.touches.fetch_add(1, Ordering::Relaxed);
                match self
                    .touch_script
                    .lock()
                    .unwrap()
                    .pop_front()
                    .unwrap_or(Scripted::Ok)
                {
                    Scripted::Ok => Ok(()),
                    Scripted::NotAlive | Scripted::Gone => Err(ClockError::SessionGone),
                    Scripted::Failed => Err(ClockError::Failed(eyre!("touch failed"))),
                }
            }
        }

        fn session_clock(
            &self,
            _session: &str,
        ) -> impl Future<Output = ClockResult<SessionClock>> + Send {
            async move {
                self.reads.fetch_add(1, Ordering::Relaxed);
                match self
                    .read_script
                    .lock()
                    .unwrap()
                    .pop_front()
                    .unwrap_or(Scripted::Ok)
                {
                    Scripted::Ok => Ok(SessionClock {
                        remaining: Some(Duration::from_secs(600)),
                        since_input: Some(Duration::from_secs(300)),
                        alive: true,
                    }),
                    Scripted::NotAlive => Ok(SessionClock {
                        remaining: Some(Duration::ZERO),
                        since_input: None,
                        alive: false,
                    }),
                    Scripted::Gone => Err(ClockError::SessionGone),
                    Scripted::Failed => Err(ClockError::Failed(eyre!("read failed"))),
                }
            }
        }
    }

    struct Clock {
        fake: Arc<FakeClient>,
        activity: Arc<Mutex<Activity>>,
        notify: Arc<Notify>,
        alive: Arc<AtomicBool>,
        handle: JoinHandle<()>,
    }

    impl Clock {
        async fn start() -> Self {
            let fake = FakeClient::new();
            let activity = Arc::new(Mutex::new(Activity::default()));
            let notify = Arc::new(Notify::new());
            let alive = Arc::new(AtomicBool::new(true));
            let handle = tokio::spawn(run_clock(
                fake.clone(),
                "s1".to_string(),
                activity.clone(),
                notify.clone(),
                alive.clone(),
            ));
            // Let the loop reach its first wait before any test advances the clock.
            tokio::task::yield_now().await;
            tokio::task::yield_now().await;
            Clock {
                fake,
                activity,
                notify,
                alive,
                handle,
            }
        }

        /// Simulates one keystroke: records input and wakes the loop.
        async fn input(&self) {
            self.activity.lock().unwrap().touch(REFRESH_WINDOW);
            self.notify.notify_one();
            tokio::task::yield_now().await;
            tokio::task::yield_now().await;
        }

        async fn wait(&self, duration: Duration) {
            tokio::time::advance(duration).await;
            tokio::task::yield_now().await;
            tokio::task::yield_now().await;
        }

        async fn wait_window(&self) {
            self.wait(REFRESH_WINDOW).await;
        }
    }

    #[tokio::test(start_paused = true)]
    async fn test_input_touches_once_per_window() {
        let clock = Clock::start().await;
        for _ in 0..10 {
            clock.wait(Duration::from_secs(30)).await;
            clock.input().await;
        }
        assert_eq!(clock.fake.touches(), 1, "10 inputs, one touch");
        assert_eq!(clock.fake.reads(), 0, "no reads while input flows");

        for _ in 0..10 {
            clock.wait(Duration::from_secs(30)).await;
            clock.input().await;
        }
        assert_eq!(clock.fake.touches(), 2, "the next window touches again");
    }

    #[tokio::test(start_paused = true)]
    async fn test_output_only_never_touches() {
        let clock = Clock::start().await;
        clock.wait_window().await;
        assert_eq!(clock.fake.touches(), 0, "output never touches");
        assert_eq!(clock.fake.reads(), 1, "silence reads the clock");
    }

    #[tokio::test(start_paused = true)]
    async fn test_reads_once_per_window_after_silence() {
        let clock = Clock::start().await;
        for _ in 0..3 {
            clock.wait_window().await;
        }
        assert_eq!(clock.fake.reads(), 3);
        assert_eq!(clock.fake.touches(), 0);
    }

    #[tokio::test(start_paused = true)]
    async fn test_stops_on_not_alive() {
        let clock = Clock::start().await;
        clock
            .fake
            .read_script
            .lock()
            .unwrap()
            .push_back(Scripted::NotAlive);
        clock.wait_window().await;
        assert_eq!(clock.fake.reads(), 1);
        assert!(clock.handle.is_finished(), "alive: false stops the loop");
    }

    #[tokio::test(start_paused = true)]
    async fn test_stops_on_session_gone() {
        let clock = Clock::start().await;
        clock
            .fake
            .read_script
            .lock()
            .unwrap()
            .push_back(Scripted::Gone);
        clock.wait_window().await;
        assert_eq!(clock.fake.reads(), 1);
        assert!(clock.handle.is_finished(), "a 404 stops the loop");
    }

    #[tokio::test(start_paused = true)]
    async fn test_failure_is_logged_and_retried_next_window() {
        let clock = Clock::start().await;
        clock
            .fake
            .read_script
            .lock()
            .unwrap()
            .push_back(Scripted::Failed);
        clock.wait_window().await;
        assert_eq!(clock.fake.reads(), 1);
        assert!(
            !clock.handle.is_finished(),
            "a failure does not stop the loop"
        );

        clock.wait_window().await;
        assert_eq!(clock.fake.reads(), 2, "the next window retries");
    }

    #[tokio::test(start_paused = true)]
    async fn test_touch_failure_is_retried_next_window() {
        let clock = Clock::start().await;
        clock
            .fake
            .touch_script
            .lock()
            .unwrap()
            .push_back(Scripted::Failed);

        clock.wait(Duration::from_secs(30)).await;
        clock.input().await;
        assert_eq!(clock.fake.touches(), 1);

        clock.wait(Duration::from_secs(30)).await;
        clock.input().await;
        assert_eq!(clock.fake.touches(), 1, "no retry inside the window");

        clock.wait(REFRESH_WINDOW).await;
        clock.input().await;
        assert_eq!(clock.fake.touches(), 2, "the next window retries");
    }

    #[tokio::test(start_paused = true)]
    async fn test_exits_when_connection_drops() {
        let clock = Clock::start().await;
        clock.alive.store(false, Ordering::Relaxed);
        clock.notify.notify_one();
        tokio::task::yield_now().await;
        assert!(clock.handle.is_finished());
        assert_eq!(clock.fake.reads(), 0, "nothing is read after the drop");
    }
}
