package io.bacta.login.server.rest.mapping;

import io.bacta.login.server.model.ConnectionServerEntry;
import io.bacta.login.server.model.GalaxyStatusUpdate;
import io.bacta.login.server.rest.model.DetailedGalaxyStatus;

import java.util.Set;
import java.util.stream.Collectors;

public final class DetailedGalaxyStatusMapping {
    public static GalaxyStatusUpdate map(DetailedGalaxyStatus status) {
        final Set<ConnectionServerEntry> connectionServers = status.getConnectionServers().stream()
                .map(cs -> {
                    final ConnectionServerEntry entry = new ConnectionServerEntry(
                            cs.getId(),
                            cs.getAddress(),
                            (short) cs.getPort(),
                            (short) cs.getPort(),
                            (short) cs.getPing());

                    entry.setNumClients(cs.getTotalConnected());

                    return entry;
                })
                .collect(Collectors.toSet());

        final GalaxyStatusUpdate update = new GalaxyStatusUpdate(
                status.getName(),
                status.getAddress(),
                status.getPort(),
                status.getTimeZone(),
                connectionServers);

        update.setBranch(status.getBranch());
        update.setNetworkVersion(status.getNetworkVersion());
        update.setChangeList(status.getChangeList());

        update.setMaxCharacters(status.getMaxCharacters());
        update.setMaxCharactersPerAccount(status.getMaxCharactersPerAccount());
        update.setOnlinePlayerLimit(status.getOnlinePlayerLimit());
        update.setOnlineTutorialLimit(status.getOnlineTutorialLimit());
        update.setOnlineFreeTrialLimit(status.getOnlineFreeTrialLimit());
        update.setCharacterCreationDisabled(status.isCharacterCreationDisabled());
        update.setAcceptingConnections(status.isAcceptingConnections());
        update.setSecret(status.isSecret());
        update.setLocked(status.isLocked());

        return update;
    }
}
