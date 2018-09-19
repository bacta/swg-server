package io.bacta.game.region;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import io.bacta.shared.datatable.DataTable;
import io.bacta.shared.datatable.DataTableManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.bacta.game.region.RegionPvp.PvpType.PVE_BATTLEFIELD;
import static io.bacta.game.region.RegionPvp.PvpType.PVP_BATTLEFIELD;

@Slf4j
@Service
public final class RegionService {
    private static final String DYNAMIC_REGION_TEMPLATE = "object/universe/dynamic_region.iff";
    private static final String ENVIRONMENT_FLAGS_DATA_TABLE = "datatables/regions/region_environment_flags.iff";
    private static final String REGION_FILES_DATA_TABLE = "datatables/region/planets.iff";

    private final TObjectIntMap<String> environmentConversionMap = new TObjectIntHashMap<String>();
    /**
     * Holds information about the regions on a planet, and their quad tree.
     */
    private final Map<String, RegionData> planetRegions = new HashMap<>();
    /**
     * Static regions loaded from the datatables.
     */
    private final List<Region> staticRegions = new ArrayList<>();
    /**
     * Dynamic regions loaded from elsewhere?
     */
    private final List<Region> dynamicRegions = new ArrayList<>();

    @Inject
    public RegionService(final DataTableManager dataTableManager) {
        loadRegionDataTables(dataTableManager);
        loadEnvironmentFlagTable(dataTableManager);
    }

    private void loadRegionDataTables(DataTableManager dataTableManager) {
        final DataTable regionFilesDataTable = dataTableManager.getTable(REGION_FILES_DATA_TABLE, true);

        if (regionFilesDataTable == null) {
            LOGGER.warn("Static regions could not be found because the regions data table could not be opened.");
            return;
        }

        final int totalRegionFiles = regionFilesDataTable.getNumRows();

        for (int regionFileIndex = 0; regionFileIndex < totalRegionFiles; ++regionFileIndex) {
            final String planetName = regionFilesDataTable.getStringValue(0, regionFileIndex);
            final String regionFileName = regionFilesDataTable.getStringValue(1, regionFileIndex);
            final float regionMinX = regionFilesDataTable.getFloatValue(2, regionFileIndex);
            final float regionMinY = regionFilesDataTable.getFloatValue(3, regionFileIndex);
            final float regionMaxX = regionFilesDataTable.getFloatValue(4, regionFileIndex);
            final float regionMaxY = regionFilesDataTable.getFloatValue(5, regionFileIndex);

            final DataTable regionDataTable = dataTableManager.getTable(regionFileName, true);

            if (regionDataTable == null) {
                LOGGER.warn("Could not open region file {}", regionFileName);
                continue;
            }

            final RegionData regionData = getOrCreateRegionData(planetName);

            final int totalRegions = regionDataTable.getNumRows();

            for (int regionIndex = 0; regionIndex < totalRegions; ++regionIndex) {
                final String regionName = regionDataTable.getStringValue(0, regionIndex);
                final RegionGeometry geometry = RegionGeometry.from(regionDataTable.getIntValue(1, regionIndex));
                final float minX = regionDataTable.getFloatValue(2, regionIndex);
                final float minY = regionDataTable.getFloatValue(3, regionIndex);
                final float maxX = regionDataTable.getFloatValue(4, regionIndex);
                final float maxY = regionDataTable.getFloatValue(5, regionIndex);
                final RegionPvp.PvpType pvp = RegionPvp.PvpType.from(regionDataTable.getIntValue(6, regionIndex));
                final int geography = regionDataTable.getIntValue(7, regionIndex);
                final int minDifficulty = regionDataTable.getIntValue(8, regionIndex);
                final int maxDifficulty = regionDataTable.getIntValue(9, regionIndex);
                final int spawn = regionDataTable.getIntValue(10, regionIndex);
                final int mission = regionDataTable.getIntValue(11, regionIndex);
                final int buildable = regionDataTable.getIntValue(12, regionIndex);
                final int municipal = regionDataTable.getIntValue(13, regionIndex);
                final int visible = regionDataTable.getIntValue(14, regionIndex);
                final int notify = regionDataTable.getIntValue(15, regionIndex);
                final int environmentFlags = convertFromStringToEnvironmentInfo(regionDataTable.getStringValue(16, regionIndex));

                //Ensure the name is unique.
                if (regionName.isEmpty()) {
                    LOGGER.warn("Row {} of region table {} has no region name.", regionIndex, regionFileName);
                    continue;
                }

                if (regionData.regionsByName.containsKey(regionName)) {
                    LOGGER.warn("Row {} of region table {} tried to add a duplicate region {}.", regionIndex, regionFileName, regionName);
                    continue;
                }

                final Region region;

                switch (geometry) {
                    case RECTANGLE:
                        region = new RegionRectangle();
                        //Add region to quad tree of the current region data.
                        //If the add fails, delete it.
                        break;
                    case CIRCLE: {
                        if (PVE_BATTLEFIELD.equals(pvp) || PVP_BATTLEFIELD.equals(pvp)) {
                            region = new RegionPvp();
                            //add to quad tree. delete on fail.
                        } else {
                            region = new RegionCircle();
                            //add to quad tree. delete on fail.
                        }
                    }
                    break;
                    default:
                        throw new IllegalArgumentException("");
                }

                region.setName(regionName);
                region.setPlanet(planetName);
                region.setPvp(pvp.value);
                region.setGeography(geography);
                region.setMinDifficulty(minDifficulty);
                region.setMaxDifficulty(maxDifficulty);
                region.setSpawn(spawn);
                region.setMission(mission);
                region.setBuildable(buildable);
                region.setMunicipal(municipal);
                region.setVisible(visible != 0);
                region.setNotify(notify != 0);
                region.setEnvironmentFlags(environmentFlags);

                staticRegions.add(region);
                regionData.regionsByName.put(regionName, region);

                dataTableManager.close(regionFileName);
            }

            dataTableManager.close(REGION_FILES_DATA_TABLE);
        }
    }

    private void loadEnvironmentFlagTable(DataTableManager dataTableManager) {
        final DataTable dataTable = dataTableManager.getTable(ENVIRONMENT_FLAGS_DATA_TABLE, true);

        for (int i =0 ; i < dataTable.getNumRows(); ++i) {
            final String value = dataTable.getStringValue(0, i);
            final int environmentInfo = (1 << i);

            environmentConversionMap.put(value, environmentInfo);
        }

        dataTableManager.close(ENVIRONMENT_FLAGS_DATA_TABLE);
    }

    private RegionData getOrCreateRegionData(final String planetName) {
        final RegionData regionData;

        if (planetRegions.containsKey(planetName)) {
            regionData = planetRegions.get(planetName);
        } else {
            regionData = new RegionData();
            planetRegions.put(planetName, regionData);
        }

        //IF no quad tree, then set it here.

        return regionData;
    }

    public Region getRegionByName(final String planetName, final String regionName) {
        final RegionData regionData = planetRegions.get(planetName);

        if (regionData != null && regionData.regionsByName.containsKey(regionName)) {
            return regionData.regionsByName.get(regionName);
        }

        return null;
    }

    private int convertFromStringToEnvironmentInfo(final String environmentString) {
        //parse the string
        final String[] split = environmentString.split(",");
        int outputValue = 0;

        for (final String value : split) {
            if (!environmentConversionMap.containsKey(value)) {
                LOGGER.error("Missing environment conversion info for value {}", value);
                continue;
            }

            final int conversionValue = environmentConversionMap.get(value);
            outputValue |= conversionValue;
        }

        return outputValue;
    }

    private final class RegionData {
        //quadtree tree
        final Map<String, Region> regionsByName = new HashMap<>();
    }
}
