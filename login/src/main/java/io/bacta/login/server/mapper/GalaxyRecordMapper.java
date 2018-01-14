package io.bacta.login.server.mapper;

import io.bacta.galaxy.message.GalaxyServerStatus;
import io.bacta.login.server.data.GalaxyRecord;

public class GalaxyRecordMapper {
    public static void map(GalaxyRecord record, GalaxyServerStatus statusMessage) {

        record.setName(statusMessage.getName());
        record.setTimeZone(statusMessage.getTimeZone());
        record.setMaxCharacters(statusMessage.getMaxCharacters());
        record.setMaxCharactersPerAccount(statusMessage.getMaxCharactersPerAccount());
        record.setOnlinePlayerLimit(statusMessage.getOnlinePlayerLimit());
        record.setOnlineTutorialLimit(statusMessage.getOnlineTutorialLimit());
        record.setOnlineFreeTrialLimit(statusMessage.getOnlineFreeTrialLimit());
        record.setAllowingCharacterCreation(statusMessage.isAllowCharacterCreation());
        record.setAllowingFreeTrialCharacterCreation(statusMessage.isAllowFreeTrialCharacterCreation());
        record.setAcceptingConnections(statusMessage.isAcceptingConnections());
        record.setSecret(statusMessage.isSecret());
        record.setLocked(statusMessage.isLocked());

    }
}
