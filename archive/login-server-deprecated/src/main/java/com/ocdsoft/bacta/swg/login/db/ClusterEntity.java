package com.ocdsoft.bacta.swg.login.db;

import com.ocdsoft.bacta.swg.login.object.ClusterListEntry;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by crush on 6/8/2017.
 * <p>
 * Represents a cluster in the database.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class ClusterEntity {
    public static final String SEQUENCE_CLUSTER_ID = "ClusterId_Sequence";
    public static final String KEY_CLUSTER_NAME = "name";

    @PrimaryKey(sequence = SEQUENCE_CLUSTER_ID)
    private int id;

    @SecondaryKey(relate = Relationship.ONE_TO_ONE)
    private String name;

    public ClusterEntity(ClusterListEntry entry) {
        setId(entry.getClusterId());
        setName(entry.getClusterName());
    }
}
