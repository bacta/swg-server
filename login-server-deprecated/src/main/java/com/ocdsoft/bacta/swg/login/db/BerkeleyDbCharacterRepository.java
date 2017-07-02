package com.ocdsoft.bacta.swg.login.db;

import com.ocdsoft.bacta.swg.login.object.CharacterInfo;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.persist.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crush on 6/8/2017.
 */
@Slf4j
public final class BerkeleyDbCharacterRepository implements CharacterRepository {
    private static final String STORE_NAME = "characters";

    private final EntityStore characterStore;
    private final PrimaryIndex<Long, CharacterEntity> characterIdAccessor;
    private final SecondaryIndex<String, Long, CharacterEntity> characterNameAccessor;
    private final SecondaryIndex<Integer, Long, CharacterEntity> clusterIdAccessor;

    public BerkeleyDbCharacterRepository(final Environment environment) throws DatabaseException {
        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setAllowCreate(true);

        characterStore = new EntityStore(environment, STORE_NAME, storeConfig);
        characterIdAccessor = characterStore.getPrimaryIndex(Long.class, CharacterEntity.class);
        characterNameAccessor = characterStore.getSecondaryIndex(characterIdAccessor, String.class, CharacterEntity.KEY_CHARACTER_NAME);
        clusterIdAccessor = characterStore.getSecondaryIndex(characterIdAccessor, Integer.class, CharacterEntity.KEY_CLUSTER_ID);
    }

    public void close() throws DatabaseException {
        characterStore.close();
    }

    @Override
    public CharacterInfo put(CharacterInfo character) {
        try {
            final CharacterEntity entity = new CharacterEntity(character);
            characterIdAccessor.put(entity);
            return mapCharacterEntity(entity);
        } catch (DatabaseException ex) {
            LOGGER.error(String.format("Could not put character with id %d in database.",
                    character.getCharacterId()), ex);
            return null;
        }
    }

    @Override
    public List<CharacterInfo> get() {
        try {
            final List<CharacterInfo> entries = new ArrayList<>();

            try (EntityCursor<CharacterEntity> entitiesCursor = characterIdAccessor.entities()) {
                entitiesCursor.forEach((entity) -> entries.add(mapCharacterEntity(entity)));
            }

            return entries;

        } catch (DatabaseException ex) {
            LOGGER.error("Could not get all characters.", ex);
            return null;
        }
    }

    @Override
    public List<CharacterInfo> getByClusterId(int clusterId) {
        try {
            final List<CharacterInfo> entries = new ArrayList<>();

            try (EntityCursor<CharacterEntity> entitiesCursor = clusterIdAccessor.subIndex(clusterId).entities()) {
                entitiesCursor.forEach((entity) -> entries.add(mapCharacterEntity(entity)));
            }

            return entries;

        } catch (DatabaseException ex) {
            LOGGER.error(String.format("Could not get characters for cluster %d.", clusterId), ex);
            return null;
        }
    }

    @Override
    public CharacterInfo get(long characterId) {
        try {
            final CharacterEntity entity = characterIdAccessor.get(characterId);
            return mapCharacterEntity(entity);
        } catch (DatabaseException ex) {
            LOGGER.error(String.format("Could not get character with id %d.", characterId), ex);
            return null;
        }
    }

    @Override
    public void delete() {
        try {
            characterStore.truncateClass(CharacterEntity.class);
        } catch (DatabaseException ex) {
            LOGGER.error("Could not delete all characters.", ex);
        }
    }

    @Override
    public void delete(long characterId) {
        try {
            characterIdAccessor.delete(characterId);
        } catch (DatabaseException ex) {
            LOGGER.error(String.format("Could not delete character with id %d.", characterId), ex);
        }
    }

    @Override
    public void deleteByClusterId(int clusterId) {
        try {
            try (EntityCursor<CharacterEntity> entitiesCursor = clusterIdAccessor.subIndex(clusterId).entities()) {
                while (entitiesCursor.next() != null)
                    entitiesCursor.delete();
            }
        } catch (DatabaseException ex) {
            LOGGER.error(String.format("Could not delete characters with cluster id %d.", clusterId), ex);
        }
    }

    private static CharacterInfo mapCharacterEntity(CharacterEntity entity) {
        return new CharacterInfo(
                entity.getName(),
                entity.getObjectTemplateId(),
                entity.getId(),
                entity.getClusterId(),
                entity.getCharacterType(),
                entity.isDisabled());
    }
}
