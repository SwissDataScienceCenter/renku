// Temporary: the clock loop consumes these in the next commit.
#![allow(dead_code)]

use std::time::Duration;

/// How long before the cull the warning appears.
pub(crate) const WARN_LEAD: Duration = Duration::from_secs(10 * 60);

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub(crate) enum TimerAction {
    Nothing,
    Cancel,
    Warn(Duration),
    RecheckAndClose,
}

/// The deadline state machine. Order is the rule: this connection's input cancels first,
/// then a missing deadline, then session-wide silence, then the deadline, then the lead.
pub(crate) fn decide(
    remaining: Option<Duration>,
    since_input: Option<Duration>,
    active: bool,
    warned: bool,
) -> TimerAction {
    if active {
        return TimerAction::Cancel;
    }
    let Some(remaining) = remaining else {
        return TimerAction::Nothing;
    };
    if since_input.is_some_and(|age| age < WARN_LEAD) {
        return TimerAction::Nothing;
    }
    if remaining.is_zero() {
        return TimerAction::RecheckAndClose;
    }
    if remaining <= WARN_LEAD && !warned {
        return TimerAction::Warn(remaining);
    }
    TimerAction::Nothing
}

/// The one line written to the shell, mirroring the browser's wording.
pub(crate) fn warning_line(remaining: Duration) -> String {
    let minutes = remaining.as_secs().div_ceil(60).max(1);
    let unit = if minutes == 1 { "minute" } else { "minutes" };
    format!(
        "Your session is inactive and will be paused in {minutes} {unit}. Any input keeps it active."
    )
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::time::Duration;

    const MIN: Duration = Duration::from_secs(60);

    #[test]
    fn test_active_input_cancels() {
        assert_eq!(
            decide(Some(MIN), Some(60 * MIN), true, true),
            TimerAction::Cancel
        );
    }

    #[test]
    fn test_no_deadline_never_acts() {
        assert_eq!(decide(None, None, false, false), TimerAction::Nothing);
    }

    #[test]
    fn test_recent_touch_on_another_connection_suppresses() {
        assert_eq!(
            decide(Some(MIN), Some(2 * MIN), false, false),
            TimerAction::Nothing
        );
    }

    #[test]
    fn test_never_touched_counts_as_silent() {
        assert_eq!(
            decide(Some(9 * MIN), None, false, false),
            TimerAction::Warn(9 * MIN)
        );
    }

    #[test]
    fn test_warns_only_inside_the_lead() {
        assert_eq!(
            decide(Some(11 * MIN), Some(60 * MIN), false, false),
            TimerAction::Nothing
        );
        assert_eq!(
            decide(Some(10 * MIN), Some(60 * MIN), false, false),
            TimerAction::Warn(10 * MIN)
        );
    }

    #[test]
    fn test_warns_once() {
        assert_eq!(
            decide(Some(9 * MIN), Some(60 * MIN), false, true),
            TimerAction::Nothing
        );
    }

    #[test]
    fn test_zero_deadline_rechecks() {
        assert_eq!(
            decide(Some(Duration::ZERO), Some(15 * MIN), false, true),
            TimerAction::RecheckAndClose
        );
    }

    #[test]
    fn test_copy_clamps_and_pluralises() {
        assert_eq!(
            warning_line(Duration::from_secs(30)),
            "Your session is inactive and will be paused in 1 minute. Any input keeps it active."
        );
        assert!(
            warning_line(9 * MIN).contains("9 minutes"),
            "{}",
            warning_line(9 * MIN)
        );
    }
}
