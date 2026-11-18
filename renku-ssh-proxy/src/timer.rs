//! The per-connection session timer: refresh on input, warn before the cull, close at it.

use crate::activity::{Activity, REFRESH_WINDOW};
use crate::data_services::{SessionClient, SessionDeadline, SessionError};
use crate::warning::{TimerAction, WARN_LEAD, decide, warning_line};
use std::future::Future;
use std::sync::Arc;
use std::sync::Mutex;
use std::sync::atomic::{AtomicBool, Ordering};
use std::time::Duration;
use tokio::sync::Notify;
use tokio::time::Instant;

/// The reason sent to the client when the cull takes the connection.
pub(crate) const DISCONNECT_REASON: &str = "Session paused due to inactivity";

/// Where the warning and the disconnect go. A port so the loop is testable without russh.
pub(crate) trait SessionAlerts: Send + Sync {
    fn warn(&self, text: &str) -> impl Future<Output = ()> + Send;
    fn disconnect(&self, reason: &str) -> impl Future<Output = ()> + Send;
}

/// Drops the warnings until the proxy wires the shell channels to them.
pub(crate) struct NoAlerts;

impl SessionAlerts for NoAlerts {
    async fn warn(&self, _text: &str) {}

    async fn disconnect(&self, _reason: &str) {}
}

/// Starts the timer for one connection.
pub(crate) fn spawn_timer<C: SessionClient + 'static, A: SessionAlerts + 'static>(
    client: Arc<C>,
    session: String,
    activity: Arc<Mutex<Activity>>,
    notify: Arc<Notify>,
    alive: Arc<AtomicBool>,
    alerts: Arc<A>,
) {
    tokio::spawn(run_timer(client, session, activity, notify, alive, alerts));
}

/// One touch per `REFRESH_WINDOW` while input flows; one read per window after it stops,
/// and one more at the deadline and at the warning, so those two are not window-granular.
pub(crate) async fn run_timer<C: SessionClient, A: SessionAlerts>(
    client: Arc<C>,
    session: String,
    activity: Arc<Mutex<Activity>>,
    notify: Arc<Notify>,
    alive: Arc<AtomicBool>,
    alerts: Arc<A>,
) {
    let mut read: Option<(Instant, SessionDeadline)> = None;
    let mut warned_deadline: Option<Instant> = None;
    let mut last_touch: Option<Instant> = None;
    loop {
        let _ = tokio::time::timeout_at(next_wake(read.as_ref()), notify.notified()).await;
        if !alive.load(Ordering::Relaxed) {
            return;
        }
        let now = Instant::now();
        let active = activity
            .lock()
            .expect("activity mutex poisoned")
            .is_active(REFRESH_WINDOW);

        if active {
            // Any input cancels the warning and moves the deadline: forget what we knew.
            warned_deadline = None;
            read = None;
            if last_touch.is_none_or(|at| now.duration_since(at) >= REFRESH_WINDOW) {
                // Count the attempt, not the success: a failed touch waits for the next window
                // instead of retrying on the next keystroke.
                last_touch = Some(now);
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
            }
            continue;
        }

        let stale = read
            .as_ref()
            .is_none_or(|(at, _)| now.duration_since(*at) >= REFRESH_WINDOW);
        if stale || remaining(read.as_ref(), now).is_some_and(|left| left <= WARN_LEAD) {
            match client.session_deadline(&session).await {
                Ok(deadline) => {
                    log::info!(
                        "Session {session} idle: {:?} until hibernation, {:?} since input",
                        deadline.remaining,
                        deadline.since_input
                    );
                    let alive_now = deadline.alive;
                    read = Some((Instant::now(), deadline));
                    if !alive_now {
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

        let deadline = deadline(read.as_ref());
        let warned = warned_deadline.is_some() && warned_deadline == deadline;
        match decide(
            remaining(read.as_ref(), now),
            since_input(read.as_ref(), now),
            active,
            warned,
        ) {
            TimerAction::Cancel => {
                warned_deadline = None;
                read = None;
            }
            TimerAction::Nothing => {}
            TimerAction::Warn(left) => {
                alerts.warn(&warning_line(left)).await;
                warned_deadline = deadline;
            }
            TimerAction::RecheckAndClose => {
                match client.session_deadline(&session).await {
                    Ok(deadline) => {
                        if !alive.load(Ordering::Relaxed) {
                            return;
                        }
                        let due = deadline.remaining == Some(Duration::ZERO);
                        let silent = deadline.since_input.is_none_or(|age| age >= WARN_LEAD);
                        let inactive = !activity
                            .lock()
                            .expect("activity mutex poisoned")
                            .is_active(REFRESH_WINDOW);
                        if due && silent && inactive {
                            alerts.disconnect(DISCONNECT_REASON).await;
                            return;
                        }
                        // A zero that stays open would spin the loop; wait a window instead.
                        read = if due {
                            None
                        } else {
                            Some((Instant::now(), deadline))
                        };
                    }
                    Err(SessionError::SessionGone) => {
                        log::info!("Session {session} is gone; stopping the timer");
                        return;
                    }
                    Err(SessionError::Failed(err)) => {
                        log::warn!("Could not re-read the session deadline for {session}: {err:#}");
                    }
                }
            }
        }
    }
}

/// What a read said, aged by the elapsed monotonic time since it was made.
fn remaining(read: Option<&(Instant, SessionDeadline)>, now: Instant) -> Option<Duration> {
    let (at, deadline) = read?;
    deadline
        .remaining
        .map(|left| left.saturating_sub(now.duration_since(*at)))
}

fn since_input(read: Option<&(Instant, SessionDeadline)>, now: Instant) -> Option<Duration> {
    let (at, deadline) = read?;
    deadline
        .since_input
        .map(|age| age + now.duration_since(*at))
}

fn deadline(read: Option<&(Instant, SessionDeadline)>) -> Option<Instant> {
    let (at, deadline) = read?;
    deadline.remaining.map(|left| *at + left)
}

/// The next read or decision point: a window away at most, earlier for the warning and the
/// deadline so they land on time instead of on the nearest window boundary.
fn next_wake(read: Option<&(Instant, SessionDeadline)>) -> Instant {
    let now = Instant::now();
    let mut wake = now + REFRESH_WINDOW;
    if let Some((at, deadline)) = read
        && let Some(left) = deadline.remaining
    {
        let warn_at = *at + left.saturating_sub(WARN_LEAD);
        for candidate in [*at + left, warn_at] {
            if candidate > now && candidate < wake {
                wake = candidate;
            }
        }
    }
    wake
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

    const MIN: Duration = Duration::from_secs(60);

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
        /// Exact deadlines the next reads report, in order; drained before `read_script`.
        read_deadlines: Mutex<VecDeque<SessionDeadline>>,
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

        fn script_read(&self, remaining: Option<Duration>, since_input: Option<Duration>) {
            self.read_deadlines
                .lock()
                .unwrap()
                .push_back(SessionDeadline {
                    remaining,
                    since_input,
                    alive: true,
                });
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
                if let Some(deadline) = self.read_deadlines.lock().unwrap().pop_front() {
                    return Ok(deadline);
                }
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

    #[derive(Default)]
    struct FakeAlerts {
        warns: Mutex<Vec<String>>,
        disconnects: Mutex<Vec<String>>,
    }

    impl FakeAlerts {
        fn warns(&self) -> Vec<String> {
            self.warns.lock().unwrap().clone()
        }

        fn disconnects(&self) -> Vec<String> {
            self.disconnects.lock().unwrap().clone()
        }
    }

    #[expect(
        clippy::manual_async_fn,
        reason = "`async fn` in a trait cannot state the `Send` bound the port requires"
    )]
    impl SessionAlerts for FakeAlerts {
        fn warn(&self, text: &str) -> impl Future<Output = ()> + Send {
            async move {
                self.warns.lock().unwrap().push(text.to_string());
            }
        }

        fn disconnect(&self, reason: &str) -> impl Future<Output = ()> + Send {
            async move {
                self.disconnects.lock().unwrap().push(reason.to_string());
            }
        }
    }

    struct Timer {
        fake: Arc<FakeClient>,
        activity: Arc<Mutex<Activity>>,
        notify: Arc<Notify>,
        alive: Arc<AtomicBool>,
        alerts: Arc<FakeAlerts>,
        handle: JoinHandle<()>,
    }

    impl Timer {
        async fn start() -> Self {
            let fake = FakeClient::new();
            let activity = Arc::new(Mutex::new(Activity::default()));
            let notify = Arc::new(Notify::new());
            let alive = Arc::new(AtomicBool::new(true));
            let alerts = Arc::new(FakeAlerts::default());
            let handle = tokio::spawn(run_timer(
                fake.clone(),
                "s1".to_string(),
                activity.clone(),
                notify.clone(),
                alive.clone(),
                alerts.clone(),
            ));
            // Let the loop reach its first wait before any test advances time.
            tokio::task::yield_now().await;
            tokio::task::yield_now().await;
            Timer {
                fake,
                activity,
                notify,
                alive,
                alerts,
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
            // Step one second at a time: a timer registered by a woken loop has to be due
            // at a moment the paused time still passes through.
            let mut left = duration;
            while left > Duration::ZERO {
                let step = left.min(Duration::from_secs(1));
                tokio::time::advance(step).await;
                tokio::task::yield_now().await;
                left -= step;
            }
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

    #[tokio::test(start_paused = true)]
    async fn test_warns_once_ten_minutes_before_the_deadline() {
        let timer = Timer::start().await;
        timer.fake.script_read(Some(20 * MIN), Some(20 * MIN));
        timer.fake.script_read(Some(10 * MIN), Some(30 * MIN));
        timer.fake.script_read(Some(5 * MIN), Some(35 * MIN));
        timer.fake.script_read(Some(Duration::ZERO), Some(40 * MIN));
        timer.fake.script_read(Some(Duration::ZERO), Some(40 * MIN));

        timer.wait(10 * MIN).await;
        let warns = timer.alerts.warns();
        assert_eq!(warns.len(), 1, "{warns:?} reads={}", timer.fake.reads());
        assert!(warns[0].contains("10 minutes"), "{}", warns[0]);
        assert!(
            timer.alerts.disconnects().is_empty(),
            "no disconnect before the deadline"
        );

        timer.wait(5 * MIN).await;
        assert_eq!(
            timer.alerts.warns().len(),
            1,
            "the next tick does not warn again"
        );

        timer.wait(5 * MIN).await;
        assert_eq!(
            timer.alerts.disconnects(),
            vec![DISCONNECT_REASON.to_string()]
        );
        assert!(timer.handle.is_finished(), "the loop ends after the close");
    }

    #[tokio::test(start_paused = true)]
    async fn test_warning_waits_for_the_lead_not_the_window() {
        let timer = Timer::start().await;
        timer.fake.script_read(Some(22 * MIN), Some(20 * MIN));
        timer.fake.script_read(Some(17 * MIN), Some(25 * MIN));
        timer.fake.script_read(Some(12 * MIN), Some(30 * MIN));
        timer.fake.script_read(Some(WARN_LEAD), Some(32 * MIN));

        timer.wait(16 * MIN).await;
        assert!(
            timer.alerts.warns().is_empty(),
            "a window boundary does not warn early"
        );

        timer.wait(2 * MIN).await;
        let warns = timer.alerts.warns();
        assert_eq!(warns.len(), 1, "{warns:?}");
        assert!(warns[0].contains("10 minutes"), "{}", warns[0]);
    }

    #[tokio::test(start_paused = true)]
    async fn test_no_warning_while_another_connection_touched() {
        let timer = Timer::start().await;
        timer.fake.script_read(Some(5 * MIN), Some(2 * MIN));
        timer.fake.script_read(Some(5 * MIN), Some(7 * MIN));
        timer.fake.script_read(Some(5 * MIN), Some(12 * MIN));

        timer.wait(10 * MIN).await;
        assert!(
            timer.alerts.warns().is_empty(),
            "a recent touch elsewhere suppresses the warning"
        );
        assert!(timer.alerts.disconnects().is_empty());

        timer.wait(5 * MIN).await;
        let warns = timer.alerts.warns();
        assert_eq!(warns.len(), 1, "it warns once both gates hold: {warns:?}");
        assert!(warns[0].contains("5 minutes"), "{}", warns[0]);
    }

    #[tokio::test(start_paused = true)]
    async fn test_recheck_moves_the_deadline_and_keeps_the_connection() {
        let timer = Timer::start().await;
        timer.fake.script_read(Some(Duration::ZERO), Some(40 * MIN));
        timer.fake.script_read(Some(5 * MIN), Some(40 * MIN));
        timer.fake.script_read(Some(5 * MIN), Some(40 * MIN));

        timer.wait_window().await;
        assert!(
            timer.alerts.disconnects().is_empty(),
            "a moved deadline keeps the connection"
        );

        timer.wait_window().await;
        assert!(timer.alerts.disconnects().is_empty());
        let warns = timer.alerts.warns();
        assert_eq!(warns.len(), 1, "the new deadline warns once: {warns:?}");
        assert!(warns[0].contains("5 minutes"), "{}", warns[0]);
        assert!(!timer.handle.is_finished());
    }

    #[tokio::test(start_paused = true)]
    async fn test_recent_touch_at_the_deadline_prevents_the_disconnect() {
        let timer = Timer::start().await;
        timer.fake.script_read(Some(Duration::ZERO), Some(40 * MIN));
        timer.fake.script_read(Some(10 * MIN), Some(2 * MIN));
        timer.fake.script_read(Some(10 * MIN), Some(7 * MIN));

        timer.wait(10 * MIN).await;
        assert!(
            timer.alerts.disconnects().is_empty(),
            "a session-wide touch keeps every connection"
        );
        assert!(timer.alerts.warns().is_empty(), "and no warning repeats");
        assert!(!timer.handle.is_finished());
    }

    #[tokio::test(start_paused = true)]
    async fn test_failed_recheck_keeps_the_connection() {
        let timer = Timer::start().await;
        timer.fake.script_read(Some(Duration::ZERO), Some(40 * MIN));
        timer
            .fake
            .read_script
            .lock()
            .unwrap()
            .push_back(Scripted::Failed);

        timer.wait_window().await;
        assert!(
            timer.alerts.disconnects().is_empty(),
            "a failed re-read leaves the connection open"
        );
        assert!(!timer.handle.is_finished());

        timer.wait_window().await;
        assert_eq!(timer.fake.reads(), 3, "the next window reads again");
        assert!(!timer.handle.is_finished());
    }

    #[tokio::test(start_paused = true)]
    async fn test_input_cancels_the_warning_and_the_close() {
        let timer = Timer::start().await;
        timer.fake.script_read(Some(10 * MIN), Some(10 * MIN));
        timer.fake.script_read(Some(20 * MIN), Some(5 * MIN));

        timer.wait_window().await;
        assert_eq!(timer.alerts.warns().len(), 1, "the warning fires at T-10");

        timer.wait(2 * MIN).await;
        timer.input().await;
        assert_eq!(timer.fake.touches(), 1, "input touches the session");

        timer.wait(13 * MIN).await;
        assert!(
            timer.alerts.disconnects().is_empty(),
            "input cancels the close"
        );
        assert_eq!(
            timer.alerts.warns().len(),
            1,
            "and the warning does not repeat"
        );
        assert!(!timer.handle.is_finished());
    }

    #[tokio::test(start_paused = true)]
    async fn test_no_deadline_never_warns_or_closes() {
        let timer = Timer::start().await;
        for _ in 0..3 {
            timer.fake.script_read(None, None);
        }
        for _ in 0..3 {
            timer.wait_window().await;
        }
        assert!(timer.alerts.warns().is_empty());
        assert!(timer.alerts.disconnects().is_empty());
        assert!(!timer.handle.is_finished());
        assert_eq!(timer.fake.reads(), 3, "one read per window");
    }

    #[tokio::test(start_paused = true)]
    async fn test_disconnect_happens_once() {
        let timer = Timer::start().await;
        timer.fake.script_read(Some(Duration::ZERO), Some(40 * MIN));
        timer.fake.script_read(Some(Duration::ZERO), Some(40 * MIN));

        timer.wait_window().await;
        assert_eq!(
            timer.alerts.disconnects(),
            vec![DISCONNECT_REASON.to_string()]
        );
        assert!(timer.handle.is_finished());

        timer.wait_window().await;
        assert_eq!(
            timer.alerts.disconnects().len(),
            1,
            "one disconnect per connection"
        );
    }
}
