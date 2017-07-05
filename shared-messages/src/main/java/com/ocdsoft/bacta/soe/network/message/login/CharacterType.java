package com.ocdsoft.bacta.soe.network.message.login;

import lombok.Getter;

/**
 * Created by crush on 7/2/2017.
 */
@Getter
public enum CharacterType {
    NORMAL(1),
    JEDI(2),
    SPECTRAL(3);

    private final int value;

    CharacterType(final int value) {
        this.value = value;
    }

    public static CharacterType from(final int value) {
        switch (value) {
            default:
            case 1:
                return NORMAL;
            case 2:
                return JEDI;
            case 3:
                return SPECTRAL;
        }
    }
}
