package io.bacta.login.server.service;

import io.bacta.soe.context.SoeRequestContext;

public interface CharacterService {
    void deleteCharacter(int clusterId, long networkId, int bactaId);

    void deleteAllCharacters(int bactaId);

    void createCharacter(int clusterId, int bactaId, String characterName, long characterId, int templateId, boolean jedi);

    void renameCharacter(int clusterId, long characterId, String name);

    void restoreCharacter(int clusterId, String requestor, int bactaId, String characterName, long characterId, int templateId, boolean jedi);

    void enableCharacter(int bactaId, long characterId, String requestor, boolean enabled, int clusterId);

//    void toggleDisableCharacter(int clusterId, long characterId, int bactaId, boolean enabled);
//
//    void toggleCompletedTutorial(int bactaId, boolean completed);

    void sendEnumerateCharacters(SoeRequestContext context, int bactaId);
}
