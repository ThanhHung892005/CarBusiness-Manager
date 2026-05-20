package com.carmanagement.enums;

public enum LeadStatus {
    NEW("Mới"),
    CONTACTED("Đã liên hệ"),
    TEST_DRIVE("Lái thử"),
    NEGOTIATING("Đang đàm phán"),
    CLOSED_WON("Thành công"),
    CLOSED_LOST("Thất bại");

    private final String displayName;

    LeadStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
