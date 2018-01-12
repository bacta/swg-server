package io.bacta.soe.network.connection;

import io.bacta.soe.config.SoeNetworkConfiguration;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by kyle on 7/7/2017.
 */
public class PendingReliablePackets {

    private final SoeNetworkConfiguration networkConfiguration;
    private final Map<Long, ByteBuffer> pendingMap;

    PendingReliablePackets(final SoeNetworkConfiguration networkConfiguration) {
        this.networkConfiguration = networkConfiguration;
        this.pendingMap = Collections.synchronizedSortedMap(new TreeMap<>());
    }

    public ByteBuffer getNext(long currentReliableId) {
       return pendingMap.get(currentReliableId);
    }

    //TODO: Finish logic and metrics
    public void add(long reliableId, ByteBuffer buffer) {
        if(pendingMap.containsKey(reliableId)) {
//            mStatDuplicatePacketsReceived++;
//            mUdpConnection->mConnectionStats.duplicatePacketsReceived++;
//            mUdpConnection->mUdpManager->mManagerStats.duplicatePacketsReceived++;
        } else {
            if(pendingMap.size() < networkConfiguration.getMaxInstandingPackets()) {
                pendingMap.put(reliableId, buffer);
            }
        }
    }
}
