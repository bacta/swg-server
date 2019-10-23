package io.bacta.game.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

/**
      91 00 02 28 C2 AB 68 C3 BF 02 17 66 18 C3 BF 01
    1C C3 BF 01 1B C3 BF 01 05 C3 BF 01 1A C3 BF 01
    19 C2 8F 0D C3 9C 09 C3 BF 01 30 C3 BF 01 12 C3
    BF 01 13 C3 BF 02 20 C3 BA 10 C3 BF 01 21 C3 BF
    01 0F C3 BF 01 14 23 11 C3 8C 0E C3 BF 01 03 C3
    BF 02 0B C3 BF 01 0C C3 BF 01 06 C3 BF 01 2F 0A
    08 C3 BF 01 15 C3 BF 01 16 C3 BF 02 04 75 07 C3
    BF 01 0A C3 BF 01 2C 45 25 03 2D C3 BF 01 24 C3
    BF 01 01 1A 22 C3 BF 01 2E 3D 40 07 1E C3 BF 01
    C3 BF 03 0B 00 00 00 52 00 61 00 6E 00 64 00 6F
    00 6D 00 20 00 4E 00 61 00 6D 00 65 00 28 00 6F
    62 6A 65 63 74 2F 63 72 65 61 74 75 72 65 2F 70
    6C 61 79 65 72 2F 74 77 69 6C 65 6B 5F 66 65 6D
    61 6C 65 2E 69 66 66 0A 00 6D 6F 73 5F 65 69 73
    6C 65 79 00 00 00 00 0E 00 63 6F 6D 62 61 74 5F
    62 72 61 77 6C 65 72 00 E3 36 7A 3F 00 00 00 00
    01 12 00 66 6F 72 63 65 5F 73 65 6E 73 69 74 69
    76 65 5F 31 61 22 00 63 6C 61 73 73 5F 66 6F 72
    63 65 73 65 6E 73 69 74 69 76 65 5F 70 68 61 73
    65 31 5F 6E 6F 76 69 63 65 

  SOECRC32.hashCode(ClientCreateCharacter.class.getSimpleName()); // 0xb97f3074
  */
@Getter
@Priority(0xe)
@RequiredArgsConstructor
public final class ClientCreateCharacter extends GameNetworkMessage {
    private final String appearanceData;
    private final String characterName;
    private final String templateName;
    private final String startingLocation;
    private final String hairTemplateName;
    private final String hairAppearanceData;
    private final String profession;
    private final boolean jedi;
    private final float scaleFactor;
    private final String biography;
    private final boolean useNewbieTutorial;
    private final String skillTemplate;
    private final String workingSkill;

    public ClientCreateCharacter(final ByteBuffer buffer) {
        appearanceData = BufferUtil.getAscii(buffer);
        characterName = BufferUtil.getUnicode(buffer);
        templateName = BufferUtil.getAscii(buffer);
        startingLocation = BufferUtil.getAscii(buffer);
        hairTemplateName = BufferUtil.getAscii(buffer);
        hairAppearanceData = BufferUtil.getAscii(buffer);
        profession = BufferUtil.getAscii(buffer);
        jedi = BufferUtil.getBoolean(buffer);
        scaleFactor = buffer.getFloat();
        biography = BufferUtil.getUnicode(buffer);
        useNewbieTutorial = BufferUtil.getBoolean(buffer);
        skillTemplate = BufferUtil.getAscii(buffer);
        workingSkill = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, appearanceData);
        BufferUtil.putUnicode(buffer, characterName);
        BufferUtil.putAscii(buffer, templateName);
        BufferUtil.putAscii(buffer, startingLocation);
        BufferUtil.putAscii(buffer, hairTemplateName);
        BufferUtil.putAscii(buffer, hairAppearanceData);
        BufferUtil.putAscii(buffer, profession);
        BufferUtil.put(buffer, jedi);
        BufferUtil.put(buffer, scaleFactor);
        BufferUtil.putUnicode(buffer, biography);
        BufferUtil.put(buffer, useNewbieTutorial);
        BufferUtil.putAscii(buffer, skillTemplate);
        BufferUtil.putAscii(buffer, workingSkill);
    }
}
