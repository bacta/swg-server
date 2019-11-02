package io.bacta.soe.network.message;

import lombok.Getter;

@Getter
public class SwgTerminateMessage {

    private final TerminateReason reason;
    private final boolean silent;
    private final int connectionId;

    public SwgTerminateMessage(final TerminateReason reason, final boolean silent, final int connectionId) {
        this.reason = reason;
        this.silent = silent;
        this.connectionId = connectionId;
    }
}
