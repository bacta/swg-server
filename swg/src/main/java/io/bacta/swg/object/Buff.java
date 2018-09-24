package io.bacta.swg.object;


import io.bacta.engine.buffer.ByteBufferWritable;
import io.bacta.swg.util.NetworkId;
import lombok.AllArgsConstructor;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/8/2016.
 */
public class Buff {
    @AllArgsConstructor
    public static class PackedBuff implements ByteBufferWritable {
        public final int endtime;
        public final float value;
        public final int duration;
        public final long caster;
        public final int stackCount;

        public PackedBuff(final ByteBuffer buffer) {
            endtime = buffer.getInt();
            value = buffer.getFloat();
            duration = buffer.getInt();
            caster = buffer.getLong();
            stackCount = buffer.getInt();
        }

        public PackedBuff(final long oldSchoolPackedBuff) {
            this.endtime = (int) (oldSchoolPackedBuff >> 32);
            int tmp = (int) (oldSchoolPackedBuff);
            this.value = (float) tmp;
            this.duration = 0;
            this.caster = NetworkId.INVALID;
            this.stackCount = 1;
        }

        public String packToString() {
            return String.format("%d %f %d %d %d", endtime, value, duration, caster, stackCount);
        }

        public static PackedBuff unpackFromStringTokens(final String[] tokens) {
            //token 0 should be the crc so skip it - it's not part of this struct.
            return new PackedBuff(Integer.parseInt(tokens[1]),
                    Float.parseFloat(tokens[2]),
                    Integer.parseInt(tokens[3]),
                    Long.parseLong(tokens[4]),
                    Integer.parseInt(tokens[5]));
        }

        @Override
        public void writeToBuffer(final ByteBuffer buffer) {
            buffer.putInt(endtime);
            buffer.putFloat(value);
            buffer.putInt(duration);
            buffer.putLong(caster);
            buffer.putInt(stackCount);
        }
    }

    public String name;
    public int nameCrc;
    public int timestamp;
    public float value;
    public int duration;
    public long caster;
    public int stackCount;

    public Buff() {

    }

    public Buff(final String buffName, final PackedBuff buffValues) {

    }

    public Buff(final String buffName, final int timestamp, final float value, final int duration, final long caster, final int stackCount) {

    }

    public Buff(final int buffNameCrc, final PackedBuff buffValues) {

    }

    public Buff(final int buffNameCrc, final int timestamp, final float value, final int duration, final long caster, final int stackCount) {

    }

    public PackedBuff getPackedBuffValue() {
        return null;
    }

    public void set(final int buffNameCrc, final PackedBuff buffValues) {

    }


    public static PackedBuff makePackedBuff(final long oldSchoolPackedBuff) {
        return new PackedBuff(oldSchoolPackedBuff);
    }
}
