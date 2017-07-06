/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.login.server.service;

import io.bacta.login.server.entity.AvatarEntity;

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
    void sendAvatarList(int bactaId, int bactaIdNumberJediSlot, List<AvatarEntity> avatars);
}
//
//public class CharacterService {
//    private final CharacterRepository characterRepository;
//
//    public CharacterService(CharacterRepository characterRepository) {
//        this.characterRepository = characterRepository;
//    }
//
//    public void sendAvatarList(int bactaId, final List<AvatarEntity> avatars) {
//        final Set<EnumerateCharacterId.CharacterData> chardata = new TreeSet<>();
//
//        for (final AvatarEntity avatar : avatars) {
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
