package io.bacta.soe.network.message;

import lombok.Getter;

import java.net.InetSocketAddress;

@Getter
public class SwgTerminateMessage {

    private final TerminateReason reason;
    private final boolean silent;
    private final InetSocketAddress remoteAddress;

    public SwgTerminateMessage(final TerminateReason reason, final boolean silent, final InetSocketAddress remoteAddress) {
        this.reason = reason;
        this.silent = silent;
        this.remoteAddress = remoteAddress;
    }
}
