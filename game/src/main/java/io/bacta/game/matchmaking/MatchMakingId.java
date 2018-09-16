package io.bacta.game.matchmaking;

import io.bacta.engine.buffer.ByteBufferWritable;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Created by crush on 8/13/2014.
 */
@NoArgsConstructor
public class MatchMakingId implements ByteBufferWritable {
    public static final int LOOKING_FOR_GROUP = 0x0;
    public static final int HELPER = 0x1;
    public static final int ROLE_PLAY = 0x2;
    public static final int FACTION = 0x3;
    public static final int SPECIES = 0x4;
    public static final int TITLE = 0x5;
    public static final int FRIEND = 0x6;
    public static final int AWAY_FROM_KEY_BOARD = 0x7;
    public static final int LINK_DEAD = 0x8;
    public static final int DISPLAYING_FACTION_RANK = 0x9;
    public static final int DISPLAY_LOCATION_IN_SEARCH_RESULTS = 0xA;
    public static final int OUT_OF_CHARACTER = 0xB;
    public static final int SEARCHABLE_BY_CTS_SOURCE_GALAXY = 0xC;
    public static final int LOOKING_FOR_WORK = 0xD;
    public static final int MAX_LOADED_BITS = 0x7E;
    public static final int ANONYMOUS = 0x7F;

    private BitSet bitSet = new BitSet(128);

    // TODO: implement
    public MatchMakingId(ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.putInt(4);
        buffer.put(Arrays.copyOf(bitSet.toByteArray(), 16));
    }

    public void flip(int index) {
        bitSet.flip(index);
    }

    public void set(int index) {
        bitSet.set(index);
    }

    public void unset(int index) {
        bitSet.clear(index);
    }

    public boolean isBitSet(int index) {
        return bitSet.get(index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatchMakingId that = (MatchMakingId) o;

        if (bitSet != null ? !bitSet.equals(that.bitSet) : that.bitSet != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return bitSet != null ? bitSet.hashCode() : 0;
    }
}
