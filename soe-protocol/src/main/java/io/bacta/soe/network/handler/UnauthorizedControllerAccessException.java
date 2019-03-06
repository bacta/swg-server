package io.bacta.soe.network.handler;

class UnauthorizedControllerAccessException extends RuntimeException {
    UnauthorizedControllerAccessException(String message) {
        super(message);
    }
}
