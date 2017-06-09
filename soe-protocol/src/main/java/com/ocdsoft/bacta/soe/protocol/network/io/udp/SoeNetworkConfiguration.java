package com.ocdsoft.bacta.soe.protocol.network.io.udp;

import com.ocdsoft.bacta.soe.protocol.network.connection.EncryptMethod;

/**
 * Created by kburkhardt on 2/7/15.
 */
public interface SoeNetworkConfiguration {

    EncryptMethod getEncryptMethod();
    boolean isMultiSoeMessages();
    boolean isMultiGameMessages();
    int getConnectionsPerAccount();
    boolean isDisableInstrumentation();
    String getBasePackage();
    String getRequiredClientVersion();

    //    logAllNetworkTraffic = false
    //    incomingBufferSize = 4194304
    //    outgoingBufferSize = 4194304
    //    maxConnections = 1000
    //
    //    maxOutstandingBytes = 204800
    //    fragmentSize = 496
    //    pooledPacketMax = 1024
    //    packettHistoryMax = 100
    //    oldestUnacknowledgedTimeout = 90000
    //    reportStatisticsInterval = 60000
    //
    //    pooledPAcketInitial = 1024
    //
    //
    //    reliableOverflowBytes = 2097152
    //    logConnectionConstructionDestruction = false
    //    logConnectionOpenedClosed = false
}
