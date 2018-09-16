package io.bacta.game.context;

import io.bacta.soe.context.SoeRequestContext;
import io.bacta.soe.network.connection.SoeConnection;

public class GameRequestContext extends SoeRequestContext {
    public GameRequestContext(final SoeConnection connection) {
        super(connection);
    }
}
