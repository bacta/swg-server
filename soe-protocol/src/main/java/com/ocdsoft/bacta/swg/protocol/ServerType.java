package com.ocdsoft.bacta.swg.protocol;

import com.google.inject.Inject;
import lombok.Getter;

/**
 * Created by Kyle on 2/18/14.
 */
public enum ServerType {

    CHAT ("chat"),
    LOGIN ("cluster"),
    GAME ("game"),
    PING ("ping");

    @Getter
    private String group;

    @Inject
    ServerType() {}

    ServerType(String group) {
        this.group = group;
    }
}
