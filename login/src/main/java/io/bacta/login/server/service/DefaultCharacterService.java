package io.bacta.login.server.service;

import io.bacta.login.message.CharacterType;
import io.bacta.login.message.EnumerateCharacterId;
import io.bacta.login.server.data.CharacterRecord;
import io.bacta.login.server.repository.CharacterRepository;
import io.bacta.soe.network.connection.SoeConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public final class DefaultCharacterService implements CharacterService {
    private final CharacterRepository characterRepository;

    @Inject
    public DefaultCharacterService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }


    @Override
    public void deleteCharacter(int clusterId, long networkId, int bactaId) {

    }

    @Override
    public void deleteAllCharacters(int bactaId) {

    }

    @Override
    public void createCharacter(int clusterId, int bactaId, String characterName, long characterId, int templateId, boolean jedi) {

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
    public void sendEnumerateCharacters(SoeConnection connection, int bactaId) {
        final Set<EnumerateCharacterId.CharacterData> characterDataSet = new HashSet<>();
        final List<CharacterRecord> accountCharacters = characterRepository.findByBactaId(bactaId);

        for (final CharacterRecord character : accountCharacters) {
            final EnumerateCharacterId.CharacterData data = new EnumerateCharacterId.CharacterData(
                    character.getName(),
                    character.getObjectTemplateId(),
                    character.getNetworkId(),
                    character.getGalaxyId(),
                    CharacterType.from(character.getCharacterType()));

            characterDataSet.add(data);
        }

        final EnumerateCharacterId message = new EnumerateCharacterId(characterDataSet);
        connection.sendMessage(message);
    }
}
