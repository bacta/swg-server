package com.ocdsoft.bacta.swg.login.service;

import com.ocdsoft.bacta.swg.login.entity.AvatarRecord;

import java.util.List;

/**
 * Created by crush on 7/2/2017.
 */
public interface CharacterService {
    void deleteCharacter(int clusterId, long networkId, int bactaId);
    void deleteAllCharacters(int bactaId);

    void createCharacter(int clusterId, int bactaId, String characterName, long characterId, int templateId, boolean jedi);
    void renameCharacter(int clusterId, long characterId, String name);
    void restoreCharacter(int clusterId, String requestor, int bactaId, String characterName, long characterId, int templateId, boolean jedi);
    void enableCharacter(int bactaId, long characterId, String requestor, boolean enabled, int clusterId);
    void toggleDisableCharacter(int clusterId, long characterId, int bactaId, boolean enabled);
    void toggleCompletedTutorial(int bactaId, boolean completed);

    //void requestAvatarListForAccount(int bactaId);
    void sendAvatarList(int bactaId, int bactaIdNumberJediSlot, List<AvatarRecord> avatars);
}
//
//public class CharacterService {
//    private final CharacterRepository characterRepository;
//
//    public CharacterService(CharacterRepository characterRepository) {
//        this.characterRepository = characterRepository;
//    }
//
//    public void sendAvatarList(int bactaId, final List<AvatarRecord> avatars) {
//        final Set<EnumerateCharacterId.CharacterData> chardata = new TreeSet<>();
//
//        for (final AvatarRecord avatar : avatars) {
//            final EnumerateCharacterId.CharacterData temp = new EnumerateCharacterId.CharacterData(
//                    avatar.getName(),
//                    avatar.getObjectTemplateId(),
//                    avatar.getNetworkId(),
//                    avatar.getClusterId(),
//                    CharacterType.from(avatar.getCharacterType()));
//
//            chardata.add(temp);
//        }
//
//        EnumerateCharacterId enumerateCharactersMessage = new EnumerateCharacterId(chardata);
//
//
////        ClientConnection* connection = getValidatedClient(bactaId);
////
////        if (connection != null) {
////            GenericValueTypeMessage<int> msgStationIdHasJediSlot = new GenericValueTypeMessage<int>("StationIdHasJediSlot", bactaIdNumberJediSlot);
////            connection.send(msgStationIdHasJediSlot);
////
////            connection.send(enumerateCharactersMessage);
////         } else {
////            LOGGER.error("Aborting sending avatar list. Could not find connection for client with bactaId({})", bactaId);
////         }
//    }
//}
