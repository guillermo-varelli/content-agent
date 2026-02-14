package com.n.in.utils.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusEnum {

    PENDING(1, "PENDING"),
    DONE(2, "DONE"),
    REJECTED(3, "REJECTED"),

    ERROR(4, "ERROR"),
    MANUAL_REVIEW(3, "MANUAL REVIEW");


    private final int id;
    private final String description;

    public static StatusEnum fromId(int id) {
        for (StatusEnum t : values()) {
            if (t.id == id) {
                return t;
            }
        }
        throw new IllegalArgumentException("Invalid StatusType id: " + id);
    }
}
