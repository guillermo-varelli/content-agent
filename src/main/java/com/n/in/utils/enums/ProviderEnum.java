package com.n.in.utils.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProviderEnum {

    GEMINI(1, "GEMINI"),
    GROQ(2, "GROQ"),
    UNPLASH(3, "UNPLASH");

    private final int id;
    private final String name;

    public static ProviderEnum fromId(int id) {
        for (ProviderEnum t : values()) {
            if (t.id == id) {
                return t;
            }
        }
        throw new IllegalArgumentException("Invalid StatusType id: " + id);
    }
}
