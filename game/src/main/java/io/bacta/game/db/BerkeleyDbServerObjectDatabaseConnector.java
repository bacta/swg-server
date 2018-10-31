package io.bacta.game.db;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Sequence;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;
import io.bacta.engine.network.NetworkObjectByteSerializer;
import io.bacta.game.object.ServerObject;
import io.bacta.swg.math.Transform;
import io.bacta.swg.math.Vector;
import io.bacta.swg.util.NetworkId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * An implementation of a network object database connector for BerkeleyDB.
 * <p>
 * Documentation for BerkeleyDB JE: https://docs.oracle.com/cd/E17277_02/html/GettingStartedGuide/index.html
 */
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public final class BerkeleyDbServerObjectDatabaseConnector implements ServerObjectDatabaseConnector {
    private static final String STORE_OBJECTS = "objects";

    private final NetworkObjectByteSerializer serializer;

    private Environment environment;
    private EntityStore store;
    private BerkeleyServerObjectEntity.DataAccessor dataAccessor;

    //TODO: BerkeleyDb specific configuration.

    public BerkeleyDbServerObjectDatabaseConnector(NetworkObjectByteSerializer serializer) {
        this.serializer = serializer;
    }

    public void connect() {
        try {
            final EnvironmentConfig environmentConfig = new EnvironmentConfig();
            final StoreConfig storeConfig = new StoreConfig();
            environmentConfig.setAllowCreate(true);
            storeConfig.setAllowCreate(true);

            final Path path = Paths.get(System.getProperty("user.dir"), "db/objects");
            final File environmentFile = path.toFile();

            //Ensure the directories exist.
            //noinspection ResultOfMethodCallIgnored
            environmentFile.mkdirs();

            this.environment = new Environment(environmentFile, environmentConfig);
            this.store = new EntityStore(this.environment, STORE_OBJECTS, storeConfig);
            this.dataAccessor = new BerkeleyServerObjectEntity.DataAccessor(this.store);
        } catch (DatabaseException ex) {
            LOGGER.error(ex.getMessage(), ex);

            //We should throw a new, project specific exception that can be handled elsewhere.
            throw ex;
        }
    }

    public void close() {
        try {
            if (store != null) {
                store.close();
            }

            if (environment != null) {
                environment.close();
            }
        } catch (DatabaseException ex) {
            LOGGER.error(ex.getMessage(), ex);

            throw ex;
        }
    }

    public void seed() {
        //Seed the database to start with a specific network id!?
    }

    @Override
    public long nextId() {
        final Sequence sequence = this.store.getSequence(BerkeleyServerObjectEntity.PK_SEQUENCE_NAME);
        return sequence.get(null, 1);
    }

    @Override
    public <T extends ServerObject> T get(String key) {
        return get(Long.parseLong(key));
    }

    @Override
    public <T extends ServerObject> T get(long key) {
        //TODO: Transactions, lock mode, reading options, etc...
        final BerkeleyServerObjectEntity entity = dataAccessor.getPrimaryIndex().get(key);
        return serializer.deserialize(entity.getSerializedData());
    }

    @Override
    public <T extends ServerObject> void persist(T object) {
        if (object.getNetworkId() == NetworkId.INVALID) {
            throw new UnsupportedOperationException("May only persist objects that have a network id assigned.");
        }

        //TODO: Transactions, lock mode, writing options...
        final BerkeleyServerObjectEntity entity = new BerkeleyServerObjectEntity();
        final Transform transform = object.getTransformObjectToParent();
        final Vector position = transform.getPositionInParent();
        final byte[] data = serializer.serialize(object);

        entity.setNetworkId(object.getNetworkId());
        entity.setX(position.x);
        entity.setZ(position.z);
        entity.setSceneId(object.getSceneId());
        entity.setContainedBy(object.getContainedBy());
        entity.setDeleted(false);

        entity.setSerializedData(data);

        dataAccessor.getPrimaryIndex().put(entity);
    }
}
