package io.bacta.engine.network.udp.netty;

class UnableToStartReceiverException extends RuntimeException {
    UnableToStartReceiverException(String message) {
        super(message);
    }
}
