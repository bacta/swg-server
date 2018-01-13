package com.ocdsoft.bacta.swg.login.db;

import com.ocdsoft.bacta.swg.login.object.CharacterInfo;
import com.sleepycat.persist.model.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by crush on 6/8/2017.
 * <p>
 * Represents a character in the berkeley database.
 */
@Entity
@Getter
@Setter
public final class CharacterEntity {
    public static final String KEY_CHARACTER_NAME = "characterName";
    public static final String KEY_CLUSTER_ID = "clusterId";

    @PrimaryKey
    private long id;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private String name;

    @SecondaryKey(
            relate = Relationship.ONE_TO_ONE,
            relatedEntity = ClusterEntity.class,
            onRelatedEntityDelete = DeleteAction.CASCADE)
    private int clusterId;

    //Not sure if this is safe. Investigate.
    private CharacterInfo.Type characterType;
    private int objectTemplateId;
    private boolean disabled;

    public CharacterEntity(CharacterInfo info) {
        setId(info.getCharacterId());
        setName(info.getName());
        setClusterId(info.getClusterId());
        setObjectTemplateId(info.getObjectTemplateId());
        setCharacterType(info.getCharacterType());
        setDisabled(info.isDisabled());
    }
}
