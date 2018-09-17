package io.bacta.game.controllers.object;


import io.bacta.game.MessageQueueData;
import io.bacta.game.context.GameRequestContext;
import io.bacta.game.object.ServerObject;

/**
 * Created by crush on 5/29/2016.
 */
public interface MessageQueueController<T extends MessageQueueData> {
    void handleIncoming(GameRequestContext context, ServerObject actor, int flags, float value, T data);
}
