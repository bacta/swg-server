package io.bacta.swg.object;

import gnu.trove.map.TByteObjectMap;
import gnu.trove.map.hash.TByteObjectHashMap;

/**
 * Created by crush on 8/21/2014.
 */
public class MovementTable {
    public static final class Rates {
        protected byte stationary;
        protected byte slow;
        protected byte fast;
        protected float move;
        protected float accel;
        protected float turn;
        protected float canSeeHeightMod;
    }

    protected final TByteObjectMap<Rates> postures = new TByteObjectHashMap<>();
    protected byte[] locomotionPostures;
}
