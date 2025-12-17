package com.org.hosply360.constant.Enums;

public enum AppointmentStatus {

    PENDING("Pending"),
    SCHEDULED("Scheduled"),
    CHECKEDIN("Checked In"),
    INPROGRESS("In Progress"),
    COMPLETED("Completed"),
    PAID("Paid"),
    NOSHOW("No Show"),
    RESCHEDULED("Rescheduled"),
    CANCELLED("Cancelled");

    private final String displayName;

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return displayName;
    }
}
