package io.bacta.game.planet;

import io.bacta.game.object.ServerObjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Loads a world snapshot from the associated .ws IFF file, and then attempts to save it to the
 * database. This can be used in conjunction with startup properties to only run on demand when a fresh
 * snapshot needs to be imported. Any objects with existing object ids will be overwritten.
 */
@Slf4j
@Service
public class WorldSnapshotDatabaseLoader {
    private final ServerObjectService serverObjectService;

    @Inject
    public WorldSnapshotDatabaseLoader(ServerObjectService serverObjectService) {
        this.serverObjectService = serverObjectService;
    }

    public void load() {
        //Find all the world snapshot files in a given directory?
        //Attempt to load each with the world snapshot iff reader
        //Serialize the objects to the database.

        final String[] planets = new String[] {
                "tatooine",
                "naboo"
        };

//        final WorldSnapshotIffReader iffReader = new WorldSnapshotIffReader();
//
//        for (final String planet : planets) {
//            final Iff iff = new Iff(); //TODO: Load external iff file.
//            final WorldSnapshot snapshot = iffReader.load(iff);
//
//            final List<WorldSnapshotNode> nodes = snapshot.getNodeList();
//
//            for (WorldSnapshotNode node : nodes) {
//                final long networkId = node.getNetworkId();
//            }
//        }
    }
}
