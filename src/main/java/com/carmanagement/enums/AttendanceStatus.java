package com.carmanagement.enums;

public enum AttendanceStatus {
    PRESENT("Có mặt"),
    LATE("Đi muộn"),
    HALF_DAY("Nửa ngày"),
    ABSENT("Vắng"),
    LEAVE("Nghỉ phép");

    private final String displayName;

    AttendanceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
