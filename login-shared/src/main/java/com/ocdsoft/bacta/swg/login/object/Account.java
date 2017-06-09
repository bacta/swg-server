package com.ocdsoft.bacta.swg.login.object;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by crush on 6/8/2017.
 */
@Getter
@AllArgsConstructor
public final class Account {
    private final int id;
    private final String username;
    private final String password;
    private final int adminLevel;
}
