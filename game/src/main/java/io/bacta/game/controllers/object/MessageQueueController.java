package io.bacta.game.controllers.object;


import io.bacta.game.MessageQueueData;
import io.bacta.game.object.ServerObject;
import io.bacta.soe.context.SoeRequestContext;

/**
 * Created by crush on 5/29/2016.
 */
public interface MessageQueueController<T extends MessageQueueData> {
    void handleIncoming(SoeRequestContext context, ServerObject actor, int flags, float value, T data);
}
