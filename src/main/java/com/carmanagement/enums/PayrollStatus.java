package com.carmanagement.enums;

public enum PayrollStatus {
    DRAFT("Nháp"),
    APPROVED("Đã duyệt"),
    PAID("Đã thanh toán");

    private final String displayName;

    PayrollStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
