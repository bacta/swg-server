package com.ocdsoft.bacta.swg.login.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Created by crush on 7/2/2017.
 */
@Getter
@RequiredArgsConstructor
public final class ConnectionServerEntry {
    private final int id;
    private final String clientServiceAddress;
    private final short clientServicePortPrivate;
    private final short clientServicePortPublic;
    private final short pingPort;
    @Setter
    private int numClients;
}
