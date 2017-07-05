package bacta.io.soe.util;


import io.bacta.game.MessageId;
import io.bacta.shared.GameNetworkMessage;

/**
 * Created by kyle on 5/28/2016.
 */
public class MessageHashUtil {
    public static int getHash(Class<? extends GameNetworkMessage> handledMessageClass) {
        final int hash;

        final MessageId messageId = handledMessageClass.getAnnotation(MessageId.class);

        if (messageId != null) {
            hash = messageId.value();
        } else {
            hash = SOECRC32.hashCode(handledMessageClass.getSimpleName());
        }

        return hash;
    }
}
