package io.bacta.game.planet;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.bacta.game.object.ServerObject;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.swg.datatable.DataTable;
import io.bacta.swg.datatable.DataTableManager;
import io.bacta.swg.math.Rectangle2d;
import io.bacta.swg.math.Transform;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * This service is still in flux. It is imagined to handle loading of the buildouts for a specific planet. Thus,
 * there will likely be multiple buildout services - one per zone.
 */
@Slf4j
public class BuildoutService {
    private static final String BUILDOUT_SCENES_DATATABLE = "datatables/buildout/buildout_scenes.iff";
    private static final String BUILDOUT_AREAS_DATATABLE = "datatables/buildout/areas_%s.iff";


    private static final int BUILDING_OBJECT_ID_OFFSET = 2000;
    private static final long DATA_BIT_STRIP_MASK = 0x80000000ffffffffL;

    //typedef std::map< std::string, std::list< ServerEventAreaInfo > > ServerEventMap;
    private final String sceneName;
    private final List<AreaInfo> areas = new ArrayList<>();
    private final Multimap<String, ServerEventAreaInfo> eventObjects = ArrayListMultimap.create();

    public BuildoutService(final DataTableManager dataTableManager) {
        this.sceneName = "naboo";

        loadBuildoutAreas(dataTableManager);
    }

    public void loadBuildoutAreas(final DataTableManager dataTableManager) {
        final String areasDataTableFileName = String.format(BUILDOUT_AREAS_DATATABLE, sceneName);

        final DataTable areasDataTable = dataTableManager.getTable(areasDataTableFileName, true);

        if (areasDataTable == null) {
            LOGGER.error("Unable to open area buildouts file {} for scene {}.", areasDataTableFileName, sceneName);
            return;
        }

        final int rowCount = areasDataTable.getNumRows();
        final List<BuildoutArea> sceneAreas = new ArrayList<>(rowCount);

        for (int rowIndex = 0; rowIndex < rowCount; ++rowIndex) {
            final BuildoutArea buildoutArea = new BuildoutArea();
            //buildoutArea.areaIndex = i * 100 + rowIndex;
        }

        dataTableManager.close(areasDataTableFileName);
    }

    private class BuildoutArea {
        private int areaIndex;
        private String areaName;
        private Rectangle2d rect;
        private boolean useClipRect;
        private Rectangle2d clipRect;
        private int clipEnvironmentFlags;
        private String compositeName;
        private Rectangle2d compositeRect;
        private boolean isolated;
        private boolean allowMap;
        private boolean internalBuildoutArea;
        private boolean allowRadarTerrain;
        private String requiredEventName;
    }

    private class BuildoutRow {
        protected long id;
        protected long containerId;
        protected Transform transform;
        protected ServerObjectTemplate serverObjectTemplate;
        protected String scripts;
        protected String objvars;
        protected int cellIndex;
        protected String eventRequired;
    }


    private class AreaInfo {
        //BuildoutArea buildoutArea
        protected boolean loaded;
        protected boolean editing;
        protected List<BuildoutRow> buildoutRows;
    }

    private class ServerEventAreaInfo {
        protected BuildoutRow buildoutRow;
        protected ServerObject loadedObject;
    }

}
