package io.bacta.game.db;

import com.sleepycat.persist.*;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import lombok.Data;
import lombok.Getter;

@Data
@Entity
public final class BerkeleyServerObjectEntity {
    protected static final String PK_SEQUENCE_NAME = "Sequence_NetworkId";

    @PrimaryKey(sequence = PK_SEQUENCE_NAME)
    private long networkId;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private float x;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private float z;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private String sceneId;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private long containedBy;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private boolean deleted;

    private byte[] serializedData;

    @Getter
    public static final class DataAccessor {
        private final PrimaryIndex<Long, BerkeleyServerObjectEntity> primaryIndex;
        private final SecondaryIndex<Float, Long, BerkeleyServerObjectEntity> xIndex;
        private final SecondaryIndex<Float, Long, BerkeleyServerObjectEntity> zIndex;
        private final SecondaryIndex<String, Long, BerkeleyServerObjectEntity> sceneIdIndex;
        private final SecondaryIndex<Long, Long, BerkeleyServerObjectEntity> containedByIndex;
        private final SecondaryIndex<Boolean, Long, BerkeleyServerObjectEntity> deletedIndex;

        public DataAccessor(EntityStore store) {
            this.primaryIndex = store.getPrimaryIndex(Long.class, BerkeleyServerObjectEntity.class);
            this.xIndex = store.getSecondaryIndex(this.primaryIndex, Float.class, "x");
            this.zIndex = store.getSecondaryIndex(this.primaryIndex, Float.class, "z");
            this.sceneIdIndex = store.getSecondaryIndex(this.primaryIndex, String.class, "sceneId");
            this.containedByIndex = store.getSecondaryIndex(this.primaryIndex, Long.class, "containedBy"); //TODO: Make this a FK?
            this.deletedIndex = store.getSecondaryIndex(this.primaryIndex, Boolean.class, "deleted");
        }

        /**
         * Creates a cursor for iterating database objects based on indexed criteria. Caller is responsible for closing the cursor.
         *
         * @param x           The x coordinate of the object.
         * @param z           The z coordinate of the object.
         * @param sceneId     The scene id of the object.
         * @param containedBy The id of the parent object.
         * @param deleted     If the object has been marked for deletion.
         * @return A cursor instance for iterating database objects. Caller must close the cursor when finished.
         */
        public ForwardCursor<BerkeleyServerObjectEntity> createCursor(float x, float z, String sceneId, long containedBy, boolean deleted) {
            final EntityJoin<Long, BerkeleyServerObjectEntity> join = new EntityJoin<>(primaryIndex);
            join.addCondition(xIndex, x);
            join.addCondition(zIndex, z);
            join.addCondition(sceneIdIndex, sceneId);
            join.addCondition(containedByIndex, containedBy);
            join.addCondition(deletedIndex, deleted);

            return join.entities();
        }
    }
}
