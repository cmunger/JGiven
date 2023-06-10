package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.StepStatus;
import java.time.Duration;
import java.util.Optional;

class MetadataMapper {
    private static final String ICON_CHECK_MARK = "icon:check-square[role=green]";
    private static final String ICON_EXCLAMATION_MARK = "icon:exclamation-circle[role=red]";
    private static final String ICON_BANNED = "icon:ban[role=silver]";
    private static final String ICON_STEP_FORWARD = "icon:step-forward[role=silver]";
    private static final int NANOSECONDS_PER_MILLISECOND = 1000000;

    private MetadataMapper() {
        // static helper class not intended to be instantiated
    }

    static String toAsciiDocTagStart(ExecutionStatus executionStatus) {
        return "// tag::" + toAsciiDocTagName(executionStatus) + "[]";
    }

    static String toAsciiDocTagEnd(ExecutionStatus executionStatus) {
        return "// end::" + toAsciiDocTagName(executionStatus) + "[]";
    }

    static String toAsciiDocTagName(final ExecutionStatus executionStatus) {
        switch (executionStatus) {
            case SCENARIO_PENDING:
                // fall through
            case SOME_STEPS_PENDING:
                return "scenario-pending";
            case SUCCESS:
                return "scenario-successful";
            case FAILED:
                return "scenario-failed";
            default:
                return "scenario-" + executionStatus.toString().toLowerCase();
        }
    }

    static String toHumanReadableStatus(final ExecutionStatus executionStatus) {
        switch (executionStatus) {
            case SCENARIO_PENDING:
                // fall through
            case SOME_STEPS_PENDING:
                return ICON_BANNED;
            case SUCCESS:
                return ICON_CHECK_MARK;
            case FAILED:
                return ICON_EXCLAMATION_MARK;
            default:
                return executionStatus.toString();
        }
    }

    static String toHumanReadableStatus(final StepStatus stepStatus) {
        switch (stepStatus) {
            case PASSED:
                return ICON_CHECK_MARK;
            case FAILED:
                return ICON_EXCLAMATION_MARK;
            case SKIPPED:
                return ICON_STEP_FORWARD;
            case PENDING:
                return ICON_BANNED;
            default:
                return stepStatus.toString();
        }
    }

    static Optional<String> toHumanReadableDuration(final long nanos) {
        if (nanos >= NANOSECONDS_PER_MILLISECOND) {
            final Duration duration = Duration.ofNanos(nanos);
            final String millisFragment = duration.getNano() / NANOSECONDS_PER_MILLISECOND + "ms";

            final long seconds = duration.getSeconds();
            final String secondsFragment = seconds > 0 ? seconds + "s " : "";

            return Optional.of(secondsFragment + millisFragment);
        } else {
            return Optional.empty();
        }
    }
}
