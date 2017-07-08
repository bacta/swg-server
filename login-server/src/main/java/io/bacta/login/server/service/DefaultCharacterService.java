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

import io.bacta.login.message.CharacterType;
import io.bacta.login.message.EnumerateCharacterId;
import io.bacta.login.server.entity.CharacterEntity;
import io.bacta.login.server.repository.CharacterRepository;
import io.bacta.soe.network.connection.SoeUdpConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Services character information and requests on the LoginServer.
 */
@Slf4j
@Service
public class DefaultCharacterService implements CharacterService {
    private final CharacterRepository characterRepository;

    public DefaultCharacterService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    @Override
    public void deleteCharacter(int clusterId, long networkId, int bactaId) {

    }

    @Override
    public void deleteAllCharacters(int bactaId) {
        characterRepository.deleteByBactaId(bactaId);
    }

    @Override
    public void createCharacter(int clusterId, int bactaId, String characterName, long characterId, int templateId, boolean jedi) {
        final CharacterEntity entity = new CharacterEntity();
        entity.setNetworkId(characterId);
        entity.setClusterId(clusterId);
        entity.setBactaId(bactaId);
        entity.setName(characterName);
        entity.setObjectTemplateId(templateId);
        entity.setCharacterType(jedi ? CharacterType.JEDI.getValue() : CharacterType.NORMAL.getValue());

        characterRepository.save(entity);
    }

    @Override
    public void renameCharacter(int clusterId, long characterId, String name) {

    }

    @Override
    public void restoreCharacter(int clusterId, String requestor, int bactaId, String characterName, long characterId, int templateId, boolean jedi) {

    }

    @Override
    public void enableCharacter(int bactaId, long characterId, String requestor, boolean enabled, int clusterId) {

    }

    @Override
    public void toggleDisableCharacter(int clusterId, long characterId, int bactaId, boolean enabled) {

    }

    @Override
    public void toggleCompletedTutorial(int bactaId, boolean completed) {

    }

    @Override
    public void sendEnumerateCharacters(SoeUdpConnection connection, int bactaId) {

        //Add some characters for testing purposes.
        if (characterRepository.count() == 0) {
            LOGGER.info("Creating 4 characters for testing.");

            final CharacterEntity entity1 = new CharacterEntity();
            entity1.setNetworkId(1);
            entity1.setClusterId(1);
            entity1.setObjectTemplateId(0xD4A72A70);
            entity1.setBactaId(bactaId);
            entity1.setName("Female Human 1");
            entity1.setCharacterType(CharacterType.NORMAL.getValue());

            final CharacterEntity entity2 = new CharacterEntity();
            entity2.setNetworkId(2);
            entity2.setClusterId(1);
            entity2.setObjectTemplateId(0xD4A72A70);
            entity2.setBactaId(bactaId);
            entity2.setName("Female Human 2");
            entity2.setCharacterType(CharacterType.JEDI.getValue());

            final CharacterEntity entity3 = new CharacterEntity();
            entity3.setNetworkId(5);
            entity3.setClusterId(1);
            entity3.setObjectTemplateId(0x6F6EB65D);
            entity3.setBactaId(bactaId);
            entity3.setName("Female Twilek 1");
            entity3.setCharacterType(CharacterType.SPECTRAL.getValue());

//            characterRepository.save(entity1);
//            characterRepository.save(entity2);
//            characterRepository.save(entity3);
        }


        final Set<EnumerateCharacterId.CharacterData> characterDataSet = new HashSet<>();
        final List<CharacterEntity> characterEntities = characterRepository.findByBactaId(bactaId);

        for (final CharacterEntity entity : characterEntities) {
            final EnumerateCharacterId.CharacterData data = new EnumerateCharacterId.CharacterData(
                    entity.getName(),
                    entity.getObjectTemplateId(),
                    entity.getNetworkId(),
                    entity.getClusterId(),
                    CharacterType.from(entity.getCharacterType()));

            characterDataSet.add(data);
        }

        final EnumerateCharacterId message = new EnumerateCharacterId(characterDataSet);
        connection.sendMessage(message);
    }

}