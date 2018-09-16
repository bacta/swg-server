package io.bacta.game.service.player.creation;

import io.bacta.shared.datatable.DataTable;
import io.bacta.shared.datatable.DataTableManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by crush on 3/28/14.
 */
@Slf4j
@Service
public final class StartingLocations {
    private static final String DATA_TABLE_NAME = "datatables/creation/starting_locations.iff";

    private final Map<String, StartingLocationInfo> startingLocations = new HashMap<>();
    private final DataTableManager dataTableManager;

    @Inject
    public StartingLocations(final DataTableManager dataTableManager) {
        this.dataTableManager = dataTableManager;
        load();
    }

    /**
     * Gets a {@link StartingLocationInfo} object
     * specified by a string key representing the location.
     *
     * @param location The key that represents the location. i.e. mos_eisley
     * @return {@link StartingLocationInfo} object
     * if key exists. Otherwise, null.
     */
    public StartingLocationInfo getStartingLocationInfo(final String location) {
        return startingLocations.get(location);
    }

    private void load() {
        LOGGER.trace("Loading starting locations.");

        final DataTable dataTable = dataTableManager.getTable(DATA_TABLE_NAME, true);

        if (dataTable != null) {
            for (int row = 0; row < dataTable.getNumRows(); ++row) {
                final StartingLocationInfo locationInfo = new StartingLocationInfo(dataTable, row);
                startingLocations.put(locationInfo.location, locationInfo);
            }

            dataTableManager.close(DATA_TABLE_NAME);
        } else {
            LOGGER.error("Could not open starting locations datatable for processing.");
        }

        LOGGER.debug(String.format("Loaded %d starting locations.", startingLocations.size()));
    }

    @Getter
    public static final class StartingLocationInfo {
        private final String location;
        private final String planet;
        private final float x;
        private final float y;
        private final float z;
        private final String cellId;
        private final String image;
        private final String description;
        private final float radius;
        private final float heading;

        public StartingLocationInfo(final DataTable dataTable, final int row) {
            location = dataTable.getStringValue("location", row);
            planet = dataTable.getStringValue("planet", row);
            x = dataTable.getFloatValue("x", row);
            y = dataTable.getFloatValue("y", row);
            z = dataTable.getFloatValue("z", row);
            cellId = dataTable.getStringValue("cellId", row);
            image = dataTable.getStringValue("image", row);
            description = dataTable.getStringValue("description", row);
            radius = dataTable.getFloatValue("radius", row);
            heading = dataTable.getFloatValue("heading", row);
        }
    }
}
