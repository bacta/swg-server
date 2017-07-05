package com.ocdsoft.bacta.swg.login.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by crush on 7/2/2017.
 */
@Getter
@AllArgsConstructor
public final class AvatarRecord {
    private final String name;
    private final int objectTemplateId;
    private final long networkId;
    private final int clusterId;
    private final int characterType;
}
