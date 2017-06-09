package com.ocdsoft.bacta.swg.login.object;

import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import lombok.Data;

import java.nio.ByteBuffer;

/**
 *
 060E51D5   -   human male
 04FEC8FA   -   trandoshan male
 32F6307A   -   twilek male
 9B81AD32   -   bothan male
 22727757   -   zabrak male
 CB8F1F9D   -   rodian male
 79BE87A9   -   moncal male
 2E3CE884   -   wookiee male
 1C95F5BC   -   sullstan male
 D3432345   -   ithorian male
 D4A72A70   -   human female
 64C24976   -   trandoshan female
 6F6EB65D   -   twilek female
 F6AB978F   -   bothan female
 421ABB7C   -   zabrak female
 299DC0DA   -   rodian female
 73D65B5F   -   moncal female
 1AAD09FA   -   wookiee female
 44739CC1   -   sullstan female
 E7DA1366   -   ithorian female */

@Data
public final class CharacterInfo implements Comparable<CharacterInfo> {

    private final long characterId; //NetworkId

    private final String name; //UnicodeString
    private final int objectTemplateId;

    private final int clusterId;
    private final Type characterType;
    private boolean disabled;

    public CharacterInfo(final String name, final int objectTemplateId, final long characterId, final int clusterId, final Type characterType, final boolean disabled) {
        this.name = name;
        this.objectTemplateId = objectTemplateId;
        this.characterId = characterId;
        this.clusterId = clusterId;
        this.characterType = characterType;
        this.disabled = disabled;
    }

    public CharacterInfo(ByteBuffer buffer) {
        name = BufferUtil.getUnicode(buffer);
        objectTemplateId = buffer.getInt();
        characterId = buffer.getLong();
        clusterId = buffer.getInt();
        characterType = Type.values()[buffer.getInt()];
        disabled = BufferUtil.getBoolean(buffer);
    }

    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.putUnicode(buffer, name);
        buffer.putInt(objectTemplateId);
        buffer.putLong(characterId);
        buffer.putInt(clusterId);
        buffer.putInt(characterType.ordinal());
    }

    @Override
    public int compareTo(CharacterInfo o) {
        return name.compareTo(o.getName());
    }

    public enum Type {
        NONE,
        NORMAL,
        JEDI,
        SPECTRAL
    }
}
