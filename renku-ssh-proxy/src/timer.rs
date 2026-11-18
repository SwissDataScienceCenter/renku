//! The per-connection session timer: refresh on input, warn before the cull, close at it.

use crate::activity::{Activity, REFRESH_WINDOW};
use crate::data_services::{SessionClient, SessionError};
use std::sync::Arc;
use std::sync::Mutex;
use std::sync::atomic::{AtomicBool, Ordering};
use tokio::sync::Notify;
use tokio::time::Instant;

/// Starts the timer for one connection.
pub(crate) fn spawn_timer<C: SessionClient + 'static>(
    client: Arc<C>,
    session: String,
    activity: Arc<Mutex<Activity>>,
    notify: Arc<Notify>,
    alive: Arc<AtomicBool>,
) {
    tokio::spawn(run_timer(client, session, activity, notify, alive));
}

/// One touch per `REFRESH_WINDOW` while input flows; one read per window after it stops.
pub(crate) async fn run_timer<C: SessionClient>(
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
                Ok(()) => log::debug!("Touched the session for {session}"),
                Err(SessionError::SessionGone) => {
                    log::info!("Session {session} is gone; stopping the timer");
                    return;
                }
                Err(SessionError::Failed(err)) => {
                    log::warn!("Could not touch the session for {session}: {err:#}");
                }
            }
        } else {
            match client.session_deadline(&session).await {
                Ok(deadline) => {
                    log::info!(
                        "Session {session} idle: {:?} until hibernation, {:?} since input",
                        deadline.remaining,
                        deadline.since_input
                    );
                    if !deadline.alive {
                        return;
                    }
                }
                Err(SessionError::SessionGone) => {
                    log::info!("Session {session} is gone; stopping the timer");
                    return;
                }
                Err(SessionError::Failed(err)) => {
                    log::warn!("Could not read the session deadline for {session}: {err:#}");
                }
            }
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::activity::{Activity, REFRESH_WINDOW};
    use crate::data_services::{SessionClient, SessionDeadline, SessionError, SessionResult};
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
    impl SessionClient for FakeClient {
        fn touch_session(&self, _session: &str) -> impl Future<Output = SessionResult<()>> + Send {
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
                    Scripted::NotAlive | Scripted::Gone => Err(SessionError::SessionGone),
                    Scripted::Failed => Err(SessionError::Failed(eyre!("touch failed"))),
                }
            }
        }

        fn session_deadline(
            &self,
            _session: &str,
        ) -> impl Future<Output = SessionResult<SessionDeadline>> + Send {
            async move {
                self.reads.fetch_add(1, Ordering::Relaxed);
                match self
                    .read_script
                    .lock()
                    .unwrap()
                    .pop_front()
                    .unwrap_or(Scripted::Ok)
                {
                    Scripted::Ok => Ok(SessionDeadline {
                        remaining: Some(Duration::from_secs(600)),
                        since_input: Some(Duration::from_secs(300)),
                        alive: true,
                    }),
                    Scripted::NotAlive => Ok(SessionDeadline {
                        remaining: Some(Duration::ZERO),
                        since_input: None,
                        alive: false,
                    }),
                    Scripted::Gone => Err(SessionError::SessionGone),
                    Scripted::Failed => Err(SessionError::Failed(eyre!("read failed"))),
                }
            }
        }
    }

    struct Timer {
        fake: Arc<FakeClient>,
        activity: Arc<Mutex<Activity>>,
        notify: Arc<Notify>,
        alive: Arc<AtomicBool>,
        handle: JoinHandle<()>,
    }

    impl Timer {
        async fn start() -> Self {
            let fake = FakeClient::new();
            let activity = Arc::new(Mutex::new(Activity::default()));
            let notify = Arc::new(Notify::new());
            let alive = Arc::new(AtomicBool::new(true));
            let handle = tokio::spawn(run_timer(
                fake.clone(),
                "s1".to_string(),
                activity.clone(),
                notify.clone(),
                alive.clone(),
            ));
            // Let the loop reach its first wait before any test advances time.
            tokio::task::yield_now().await;
            tokio::task::yield_now().await;
            Timer {
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
        let timer = Timer::start().await;
        for _ in 0..10 {
            timer.wait(Duration::from_secs(30)).await;
            timer.input().await;
        }
        assert_eq!(timer.fake.touches(), 1, "10 inputs, one touch");
        assert_eq!(timer.fake.reads(), 0, "no reads while input flows");

        for _ in 0..10 {
            timer.wait(Duration::from_secs(30)).await;
            timer.input().await;
        }
        assert_eq!(timer.fake.touches(), 2, "the next window touches again");
    }

    #[tokio::test(start_paused = true)]
    async fn test_output_only_never_touches() {
        let timer = Timer::start().await;
        timer.wait_window().await;
        assert_eq!(timer.fake.touches(), 0, "output never touches");
        assert_eq!(timer.fake.reads(), 1, "silence reads the deadline");
    }

    #[tokio::test(start_paused = true)]
    async fn test_reads_once_per_window_after_silence() {
        let timer = Timer::start().await;
        for _ in 0..3 {
            timer.wait_window().await;
        }
        assert_eq!(timer.fake.reads(), 3);
        assert_eq!(timer.fake.touches(), 0);
    }

    #[tokio::test(start_paused = true)]
    async fn test_stops_on_not_alive() {
        let timer = Timer::start().await;
        timer
            .fake
            .read_script
            .lock()
            .unwrap()
            .push_back(Scripted::NotAlive);
        timer.wait_window().await;
        assert_eq!(timer.fake.reads(), 1);
        assert!(timer.handle.is_finished(), "alive: false stops the loop");
    }

    #[tokio::test(start_paused = true)]
    async fn test_stops_on_session_gone() {
        let timer = Timer::start().await;
        timer
            .fake
            .read_script
            .lock()
            .unwrap()
            .push_back(Scripted::Gone);
        timer.wait_window().await;
        assert_eq!(timer.fake.reads(), 1);
        assert!(timer.handle.is_finished(), "a 404 stops the loop");
    }

    #[tokio::test(start_paused = true)]
    async fn test_failure_is_logged_and_retried_next_window() {
        let timer = Timer::start().await;
        timer
            .fake
            .read_script
            .lock()
            .unwrap()
            .push_back(Scripted::Failed);
        timer.wait_window().await;
        assert_eq!(timer.fake.reads(), 1);
        assert!(
            !timer.handle.is_finished(),
            "a failure does not stop the loop"
        );

        timer.wait_window().await;
        assert_eq!(timer.fake.reads(), 2, "the next window retries");
    }

    #[tokio::test(start_paused = true)]
    async fn test_touch_failure_is_retried_next_window() {
        let timer = Timer::start().await;
        timer
            .fake
            .touch_script
            .lock()
            .unwrap()
            .push_back(Scripted::Failed);

        timer.wait(Duration::from_secs(30)).await;
        timer.input().await;
        assert_eq!(timer.fake.touches(), 1);

        timer.wait(Duration::from_secs(30)).await;
        timer.input().await;
        assert_eq!(timer.fake.touches(), 1, "no retry inside the window");

        timer.wait(REFRESH_WINDOW).await;
        timer.input().await;
        assert_eq!(timer.fake.touches(), 2, "the next window retries");
    }

    #[tokio::test(start_paused = true)]
    async fn test_exits_when_connection_drops() {
        let timer = Timer::start().await;
        timer.alive.store(false, Ordering::Relaxed);
        timer.notify.notify_one();
        tokio::task::yield_now().await;
        assert!(timer.handle.is_finished());
        assert_eq!(timer.fake.reads(), 0, "nothing is read after the drop");
    }
}
