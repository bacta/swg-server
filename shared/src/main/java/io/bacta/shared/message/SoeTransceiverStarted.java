package io.bacta.shared.message;

import lombok.Getter;

import java.net.InetSocketAddress;

@Getter
public class SoeTransceiverStarted {

    private final InetSocketAddress address;

    public SoeTransceiverStarted(final InetSocketAddress address) {
        this.address = address;
    }
}
