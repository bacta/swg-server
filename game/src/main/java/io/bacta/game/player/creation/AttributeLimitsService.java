package io.bacta.game.player.creation;

import com.google.common.collect.ImmutableMap;
import io.bacta.shared.datatable.DataTable;
import io.bacta.shared.datatable.DataTableManager;
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
public final class AttributeLimitsService {
    private static final String dataTableName = "datatables/creation/attribute_limits.iff";
    private static final Logger logger = LoggerFactory.getLogger(AttributeLimitsService.class);

    private Map<String, AttributeLimitInfo> attributeLimits;

    private final DataTableManager dataTableManager;

    @Inject
    public AttributeLimitsService(final DataTableManager dataTableManager) {
        this.dataTableManager = dataTableManager;
        load();
    }

    /**
     * Gets an {@link AttributeLimitInfo} object for
     * the specified player template. <i>The player template should only be the file name, and not the fullpath.</i>
     * <p/>
     * i.e. bothan_female.
     *
     * @param speciesGender A string like human_male.
     * @return Returns an {@link AttributeLimitInfo} object
     * if a record corresponding to the key is found. Otherwise, null.
     */
    public AttributeLimitInfo getAttributeLimits(final String speciesGender) {
        return attributeLimits != null ? attributeLimits.get(speciesGender) : null;
    }

    private void load() {
        logger.trace("Loading attribute limits.");

        final DataTable dataTable = dataTableManager.getTable(dataTableName);

        if (dataTable != null) {
            final Map<String, AttributeLimitInfo> map = new HashMap<>(dataTable.getNumRows() * 2);

            for (int row = 0; row < dataTable.getNumRows(); ++row) {
                final AttributeLimitInfo limitInfo = new AttributeLimitInfo(dataTable, row);
                map.put(limitInfo.maleTemplate, limitInfo);
                map.put(limitInfo.femaleTemplate, limitInfo);
            }

            attributeLimits = ImmutableMap.copyOf(map);

            dataTableManager.close(dataTableName);
        }

        logger.debug(String.format("Loaded %d profession mods.", attributeLimits != null ? attributeLimits.size() : 0));
    }


    @Getter
    public static final class AttributeLimitInfo {
        private final String maleTemplate;
        private final String femaleTemplate;
        private final int minHealth;
        private final int maxHealth;
        private final int minStrength;
        private final int maxStrength;
        private final int minConstitution;
        private final int maxConstitution;
        private final int minAction;
        private final int maxAction;
        private final int minQuickness;
        private final int maxQuickness;
        private final int minStamina;
        private final int maxStamina;
        private final int minMind;
        private final int maxMind;
        private final int minFocus;
        private final int maxFocus;
        private final int minWillpower;
        private final int maxWillpower;
        private final int total;

        public AttributeLimitInfo(final DataTable dataTable, final int row) {
            this.maleTemplate = dataTable.getStringValue("male_template", row);
            this.femaleTemplate = dataTable.getStringValue("female_template", row);
            this.minHealth = dataTable.getIntValue("min_health", row);
            this.maxHealth = dataTable.getIntValue("max_health", row);
            this.minStrength = dataTable.getIntValue("min_strength", row);
            this.maxStrength = dataTable.getIntValue("max_strength", row);
            this.minConstitution = dataTable.getIntValue("min_constitution", row);
            this.maxConstitution = dataTable.getIntValue("max_constitution", row);
            this.minAction = dataTable.getIntValue("min_action", row);
            this.maxAction = dataTable.getIntValue("max_action", row);
            this.minQuickness = dataTable.getIntValue("min_quickness", row);
            this.maxQuickness = dataTable.getIntValue("max_quickness", row);
            this.minStamina = dataTable.getIntValue("min_stamina", row);
            this.maxStamina = dataTable.getIntValue("max_stamina", row);
            this.minMind = dataTable.getIntValue("min_mind", row);
            this.maxMind = dataTable.getIntValue("max_mind", row);
            this.minFocus = dataTable.getIntValue("min_focus", row);
            this.maxFocus = dataTable.getIntValue("max_focus", row);
            this.minWillpower = dataTable.getIntValue("min_willpower", row);
            this.maxWillpower = dataTable.getIntValue("max_willpower", row);
            this.total = dataTable.getIntValue("total", row);
        }
    }
}
