package io.bacta.game.controllers.player;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.swg.math.Vector;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

@Getter
@Priority(0x02)
@RequiredArgsConstructor
public final class CharacterSheetResponseMessage extends GameNetworkMessage {
    private final int bornDate;
    private final int played;
    private final Vector bindLocation;
    private final String bindPlanet;
    private final Vector bankLocation;
    private final String bankPlanet;
    private final Vector residenceLocation;
    private final String residencePlanet;
    private final String citizensOf;
    private final String spouseName; //unicode
    private final int lotsUsed;

    public CharacterSheetResponseMessage(ByteBuffer buffer) {
        bornDate = buffer.getInt();
        played = buffer.getInt();
        bindLocation = new Vector(buffer);
        bindPlanet = BufferUtil.getAscii(buffer);
        bankLocation = new Vector(buffer);
        bankPlanet = BufferUtil.getAscii(buffer);
        residenceLocation = new Vector(buffer);
        residencePlanet = BufferUtil.getAscii(buffer);
        citizensOf = BufferUtil.getAscii(buffer);
        spouseName = BufferUtil.getUnicode(buffer);
        lotsUsed = buffer.getInt();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, bornDate);
        BufferUtil.put(buffer, played);
        BufferUtil.put(buffer, bindLocation);
        BufferUtil.putAscii(buffer, bindPlanet);
        BufferUtil.put(buffer, bankLocation);
        BufferUtil.putAscii(buffer, bankPlanet);
        BufferUtil.put(buffer, residenceLocation);
        BufferUtil.putAscii(buffer, residencePlanet);
        BufferUtil.putAscii(buffer, citizensOf);
        BufferUtil.putUnicode(buffer, spouseName);
        BufferUtil.put(buffer, lotsUsed);
    }
}
