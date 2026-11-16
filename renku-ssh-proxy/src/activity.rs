use std::time::Duration;

use tokio::time::Instant;

/// Presence window: input within this window means the connection is active.
/// Section 3 of the build shares it as the refresh debounce (at most one touch per window).
pub(crate) const REFRESH_WINDOW: Duration = Duration::from_secs(5 * 60);

/// Client input on one connection. Output, EOF/close and keepalives never touch it.
#[derive(Debug, Default)]
pub(crate) struct Activity {
    last_input: Option<Instant>,
}

impl Activity {
    /// Records input; true when this input reopened the window (it was inactive before).
    pub(crate) fn touch(&mut self, window: Duration) -> bool {
        let was_active = self.is_active(window);
        self.last_input = Some(Instant::now());
        !was_active
    }

    /// Whether there was input within `window`. No input ever means inactive.
    pub(crate) fn is_active(&self, window: Duration) -> bool {
        self.last_input.is_some_and(|at| at.elapsed() < window)
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::time::Duration;

    #[tokio::test(start_paused = true)]
    async fn test_window_boundary() {
        let mut activity = Activity::default();
        assert!(!activity.is_active(REFRESH_WINDOW), "no input is inactive");
        activity.touch(REFRESH_WINDOW);
        assert!(activity.is_active(REFRESH_WINDOW));
        tokio::time::advance(REFRESH_WINDOW - Duration::from_secs(1)).await;
        assert!(activity.is_active(REFRESH_WINDOW));
        tokio::time::advance(Duration::from_secs(1)).await;
        assert!(
            !activity.is_active(REFRESH_WINDOW),
            "exactly one window old is inactive"
        );
    }

    #[tokio::test(start_paused = true)]
    async fn test_touch_reports_the_edge_only() {
        let mut activity = Activity::default();
        assert!(activity.touch(REFRESH_WINDOW), "first input is an edge");
        assert!(
            !activity.touch(REFRESH_WINDOW),
            "input inside the window is not"
        );
        tokio::time::advance(REFRESH_WINDOW).await;
        assert!(
            activity.touch(REFRESH_WINDOW),
            "input after the window is an edge again"
        );
    }
}
