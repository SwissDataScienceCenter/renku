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

/// Whether a client data chunk is a terminal answering a query rather than a keystroke.
///
/// A resize or an attach makes the client ask the terminal what it is, where the cursor is and
/// what colours it uses; the answers arrive as ordinary channel data. Counting them keeps a
/// session alive forever on a tiling window manager, so they are not presence. Keys and pasted
/// text share the same ESC prefix and do count.
pub(crate) fn is_terminal_reply(data: &[u8]) -> bool {
    let Some(rest) = data.strip_prefix(b"\x1b[") else {
        return data.starts_with(b"\x1b]");
    };
    match rest {
        // Primary and secondary device attributes: ESC [ ? ... c, ESC [ > ... c
        [b'?', .., b'c'] | [b'>', .., b'c'] => true,
        // Cursor position report: ESC [ row ; col R. A modified F3 sends the same shape, which is
        // rare enough to accept losing.
        [.., b'R'] => rest[..rest.len() - 1]
            .iter()
            .all(|b| b.is_ascii_digit() || *b == b';'),
        _ => false,
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

    #[test]
    fn test_terminal_replies_are_not_input() {
        assert!(
            is_terminal_reply(b"\x1b[?62;22c"),
            "primary device attributes"
        );
        assert!(
            is_terminal_reply(b"\x1b[>1;10;0c"),
            "secondary device attributes"
        );
        assert!(
            is_terminal_reply(b"\x1b]10;rgb:ffff/ffff/ffff\x07"),
            "OSC colour answer"
        );
        assert!(is_terminal_reply(b"\x1b[24;80R"), "cursor position report");
    }

    #[test]
    fn test_keys_and_payloads_stay_input() {
        assert!(!is_terminal_reply(b"\x1b[A"), "up arrow");
        assert!(!is_terminal_reply(b"\x1bOP"), "F1");
        assert!(!is_terminal_reply(b"\x1b[200~"), "paste start");
        assert!(!is_terminal_reply(b"\x1b[201~"), "paste end");
        assert!(!is_terminal_reply(b"ls -la\r"), "typed text");
        assert!(!is_terminal_reply(b"\r"), "enter");
    }
}
