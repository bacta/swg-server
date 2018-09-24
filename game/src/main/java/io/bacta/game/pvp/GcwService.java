package io.bacta.game.pvp;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import io.bacta.swg.datatable.DataTable;
import io.bacta.swg.datatable.DataTableManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Slf4j
@Service
public class GcwService {
    private static final String RANK_TABLE = "datatables/gcw/gcw_rank.iff";
    private static final String RANK_DECAY_EXCLUSION_TABLE = "datatables/gcw/gcw_rank_decay_exclusion.iff";
    private static final String SCORE_CATEGORY_TABLE = "datatables/gcw/gcw_score_category.iff";
    private static final int MAX_CATEGORY_SCORE_WEIGHT = 1000000;

    private final DataTableManager dataTableManager;

    @Inject
    public GcwService(DataTableManager dataTableManager) {
        this.dataTableManager = dataTableManager;
    }

    private void loadScoreCategoryTable() {
        final DataTable dataTable = this.dataTableManager.getTable(SCORE_CATEGORY_TABLE);

        final int categoryNameColumn = dataTable.findColumnNumber("CategoryName");
        final int categoryScoreWeightColumn = dataTable.findColumnNumber("CategoryScoreWeight");
        final int regionPlanetColumn = dataTable.findColumnNumber("RegionPlanet");
        final int regionDefenderColumn = dataTable.findColumnNumber("GcwRegionDefender");
        final int notifyOnPercentileChangeColumn = dataTable.findColumnNumber("NotifyOnPercentileChange");

        final TIntList groupColumns = new TIntArrayList();

        for (int i = 1; i < 10000000; ++i) {
            final String groupColumnName = String.format("GcwGroup%d", i);
            final int columnIndex = dataTable.findColumnNumber(groupColumnName);

            //If it didn't find the column, then exit the loop.
            if (columnIndex < 0)
                break;

            groupColumns.add(columnIndex);
        }

        final int totalRows = dataTable.getNumRows();
        for (int rowIndex = 0; rowIndex < totalRows; ++rowIndex) {
            final String categoryName = dataTable.getStringValue(categoryNameColumn, rowIndex);

            //Ignore any categories with empty names...
            if (categoryName.isEmpty())
                continue;

            int baseCategoryScoreWeight = dataTable.getIntValue(categoryScoreWeightColumn, rowIndex);
            String regionPlanet = dataTable.getStringValue(regionPlanetColumn,rowIndex);

            if (!regionPlanet.isEmpty()) {
                //We don't have regions yet!!!!
                //Region
            }

        }
    }

    public void getGcwScoreCategoryRegions() {
        //map<string, map<string, <pair<pair<float, float>, float>>>
    }

    public void getGcwScoreCategoryGroups() {
        //map<string, map<string, int>>
    }
}
