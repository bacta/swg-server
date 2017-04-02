package com.ocdsoft.bacta.swg.login.object;

import com.ocdsoft.bacta.engine.buffer.ByteBufferWritable;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by Kyle on 8/15/2014.
 */
public enum PopulationStatus implements ByteBufferWritable {

    PS_LOWEST(0x0),
    PS_very_light(0x0),
    PS_light(0x1),
    PS_medium(0x2),
    PS_heavy(0x3),
    PS_very_heavy(0x4),
    PS_extremely_heavy(0x5),
    PS_full(0x6),
    PS_HIGHEST(0x6);

    @Getter
    int value;

    PopulationStatus(int value) {
        this.value = value;
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.putInt(value);
    }
}
