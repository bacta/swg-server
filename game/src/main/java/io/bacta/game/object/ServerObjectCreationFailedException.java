package io.bacta.game.object;

public class ServerObjectCreationFailedException extends Exception {
    private static final long serialVersionUID = 537801722911446966L;

    public ServerObjectCreationFailedException(final String templateName) {
        super(String.format("Failed to create server object with template %s.", templateName));
    }
}
