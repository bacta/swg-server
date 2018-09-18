package io.bacta.game.player;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.ObjControllerMessage;
import io.bacta.game.controllers.object.ObjControllerBuilder;
import io.bacta.game.message.object.MessageQueueBiographyPayload;
import io.bacta.game.object.ServerObject;
import io.bacta.shared.biography.BiographyPayload;
import io.bacta.soe.network.connection.SoeConnection;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public final class BiographyService {
    private static final String DEFAULT_BACTA_BIO = "Bacta biography.";

    private final TLongObjectMap<CachedBiography> cachedBiographyMap;

    public BiographyService() {
        this.cachedBiographyMap = new TLongObjectHashMap<>();
    }

    public void setBiography(final ServerObject creature, String biography) {
        if (biography.length() > 1024)
            biography = biography.substring(0, 1024);

        LOGGER.error("Not implemented");

        //write to the database.
        //cache.
    }

    public void deleteBiography(final long networkId) {
        LOGGER.error("Not implemented");
    }

    public void requestBiography(final ServerObject target, final ServerObject requestor) {
        final SoeConnection connection = requestor.getConnection();

        if (connection != null) {
            final MessageQueueBiographyPayload payload = new MessageQueueBiographyPayload(
                    new BiographyPayload(target.getNetworkId(), DEFAULT_BACTA_BIO));

            final ObjControllerMessage msg = ObjControllerBuilder.newBuilder()
                    .send().reliable().authClient()
                    .build(target.getNetworkId(), GameControllerMessageType.BIOGRAPHY_RETRIEVED, payload);

            connection.sendMessage(msg);
        }
    }

    @AllArgsConstructor
    private final static class CachedBiography {
        private final long cacheTimeStamp;
        private final String biography;
    }
}
