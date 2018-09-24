package io.bacta.game.player.creation;

import com.google.common.collect.ImmutableMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import io.bacta.game.object.tangible.creature.Attribute;
import io.bacta.swg.datatable.DataTable;
import io.bacta.swg.datatable.DataTableManager;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by crush on 3/28/14.
 */
@Service
public class RacialModsService {
    private static final String DATA_TABLE_NAME = "datatables/creation/racial_mods.iff";
    private static final Logger LOGGER = LoggerFactory.getLogger(RacialModsService.class);

    private Map<String, RacialModInfo> racialMods;

    private final DataTableManager dataTableManager;

    @Inject
    public RacialModsService(final DataTableManager dataTableManager) {
        this.dataTableManager = dataTableManager;
        load();
    }

    /**
     * Gets a {@link RacialModInfo} object for the given
     * player template.
     *
     * @param speciesGender speciesGender string like bothan_male.
     * @return {@link RacialModInfo} if key exists. Otherwise, null.
     */
    public RacialModInfo getRacialModInfo(final String speciesGender) {
        return racialMods != null ? racialMods.get(speciesGender) : null;
    }

    private void load() {
        LOGGER.trace("Loading racial mods.");

        final DataTable dataTable = dataTableManager.getTable(DATA_TABLE_NAME, true);

        if (dataTable != null) {
            final Map<String, RacialModInfo> map = new HashMap<>(dataTable.getNumRows() * 2);

            for (int row = 0; row < dataTable.getNumRows(); ++row) {
                final RacialModInfo modInfo = new RacialModInfo(dataTable, row);
                map.put(modInfo.maleTemplate, modInfo);
                map.put(modInfo.femaleTemplate, modInfo);
            }

            racialMods = ImmutableMap.copyOf(map);

            dataTableManager.close(DATA_TABLE_NAME);
        }

        LOGGER.debug(String.format("Loaded %d racial mods.", racialMods != null ? racialMods.size() : 0));
    }

    @Getter
    public static final class RacialModInfo {
        private final String maleTemplate;
        private final String femaleTemplate;
        private final TIntList attributes;

        public RacialModInfo(final DataTable dataTable, final int row) {
            this.maleTemplate = dataTable.getStringValue("male_template", row);
            this.femaleTemplate = dataTable.getStringValue("female_template", row);

            final TIntList list = new TIntArrayList(Attribute.SIZE);
            list.add(dataTable.getIntValue("health", row));
            list.add(dataTable.getIntValue("constitution", row));
            list.add(dataTable.getIntValue("action", row));
            list.add(dataTable.getIntValue("stamina", row));
            list.add(dataTable.getIntValue("mind", row));
            list.add(dataTable.getIntValue("willpower", row));

            attributes = new TUnmodifiableIntList(list);
        }
    }
}
