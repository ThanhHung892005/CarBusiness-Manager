package com.carmanagement.enums;

public enum InteractionType {
    CALL("Gọi điện"),
    EMAIL("Email"),
    SMS("Tin nhắn"),
    MEETING("Gặp mặt"),
    TEST_DRIVE("Lái thử"),
    NOTE("Ghi chú");

    private final String displayName;

    InteractionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
