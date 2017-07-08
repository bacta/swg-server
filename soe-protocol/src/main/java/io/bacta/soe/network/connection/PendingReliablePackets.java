package io.bacta.soe.network.connection;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by kyle on 7/7/2017.
 */
public class PendingReliablePackets {

    private final int maxInstandingPackets;
    private final Map<Long, ByteBuffer> pendingMap;

    PendingReliablePackets(int maxInstandingPackets) {
        this.maxInstandingPackets = maxInstandingPackets;
        this.pendingMap = Collections.synchronizedSortedMap(new TreeMap<>());
    }

    public ByteBuffer getNext(long currentReliableId) {
       return pendingMap.get(currentReliableId);
    }

    public void add(long reliableId, ByteBuffer buffer) {
        if(pendingMap.containsKey(reliableId)) {
//            mStatDuplicatePacketsReceived++;
//            mUdpConnection->mConnectionStats.duplicatePacketsReceived++;
//            mUdpConnection->mUdpManager->mManagerStats.duplicatePacketsReceived++;
        } else {
            pendingMap.put(reliableId, buffer);
        }
    }
}
