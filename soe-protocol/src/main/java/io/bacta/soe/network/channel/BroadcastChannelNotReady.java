package io.bacta.soe.network.channel;

class BroadcastChannelNotReady extends RuntimeException {
    BroadcastChannelNotReady() {
        super("Broadcast channel not ready");
    }
}
