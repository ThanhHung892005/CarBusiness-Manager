package com.carmanagement.enums;

public enum LeadSource {
    FACEBOOK("Facebook"),
    WALK_IN("Đến trực tiếp"),
    REFERRAL("Giới thiệu"),
    OTHER("Khác");

    private final String displayName;

    LeadSource(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
