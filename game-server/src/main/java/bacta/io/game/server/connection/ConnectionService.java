package bacta.io.game.server.connection;


import io.bacta.shared.GameNetworkMessage;

/**
 * Created by crush on 6/29/2017.
 */
public interface ConnectionService {
    /**
     * Sends a message to the connection server for the given connection.
     * @param connectionId The connection who will receive the message from the connection server.
     * @param message The message to be sent.
     */
    void send(int connectionId, GameNetworkMessage message);
}
