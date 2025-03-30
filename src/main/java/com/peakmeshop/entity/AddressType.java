package com.peakmeshop.entity;

public enum AddressType {
    HOME("집"),
    WORK("직장"),
    OTHER("기타");

    private final String displayName;

    AddressType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}