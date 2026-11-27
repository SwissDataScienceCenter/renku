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

/// The deadline state machine. A fresh `remaining` is the only source of truth: any touch on
/// the session moves the deadline, so it answers the "is someone active elsewhere" question
/// without a second, separately rounded source.
pub(crate) fn decide(remaining: Option<Duration>, active: bool, warned: bool) -> TimerAction {
    if active {
        return TimerAction::Cancel;
    }
    let Some(remaining) = remaining else {
        return TimerAction::Nothing;
    };
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
        assert_eq!(decide(Some(MIN), true, true), TimerAction::Cancel);
    }

    #[test]
    fn test_no_deadline_never_acts() {
        assert_eq!(decide(None, false, false), TimerAction::Nothing);
    }

    #[test]
    fn test_a_touch_elsewhere_moves_the_lead_out_of_reach() {
        assert_eq!(decide(Some(19 * MIN), false, false), TimerAction::Nothing);
    }

    #[test]
    fn test_warns_only_inside_the_lead() {
        assert_eq!(decide(Some(11 * MIN), false, false), TimerAction::Nothing);
        assert_eq!(
            decide(Some(10 * MIN), false, false),
            TimerAction::Warn(10 * MIN)
        );
    }

    #[test]
    fn test_warns_once() {
        assert_eq!(decide(Some(9 * MIN), false, true), TimerAction::Nothing);
    }

    #[test]
    fn test_zero_deadline_rechecks() {
        assert_eq!(
            decide(Some(Duration::ZERO), false, true),
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
