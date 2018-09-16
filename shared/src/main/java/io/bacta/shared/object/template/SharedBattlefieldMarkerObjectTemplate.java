package io.bacta.shared.object.template;

import com.google.common.base.Preconditions;
import io.bacta.shared.foundation.DataResourceList;
import io.bacta.shared.foundation.Tag;
import io.bacta.shared.iff.Iff;
import io.bacta.shared.template.ObjectTemplate;
import io.bacta.shared.template.definition.TemplateDefinition;
import io.bacta.shared.utility.FloatParam;
import io.bacta.shared.utility.IntegerParam;
import lombok.extern.slf4j.Slf4j;

/**
 * Generated by the TemplateDefinitionWriter.
 * MANUAL MODIFICATIONS MAY BE OVERWRITTEN.
 */
@Slf4j
@TemplateDefinition
public class SharedBattlefieldMarkerObjectTemplate extends SharedTangibleObjectTemplate {
    public static final int TAG_SHAREDBATTLEFIELDMARKEROBJECTTEMPLATE = Tag.convertStringToTag("SBMK");

    private static void registerTemplateConstructors(final DataResourceList<ObjectTemplate> objectTemplateList) {
        objectTemplateList.registerTemplate(SharedBattlefieldMarkerObjectTemplate.TAG_SHAREDBATTLEFIELDMARKEROBJECTTEMPLATE, SharedBattlefieldMarkerObjectTemplate::new);
    }

    private int templateVersion;

    private final IntegerParam numberOfPoles = new IntegerParam(); // number of child object poles
    private final FloatParam radius = new FloatParam(); // radius in meters

    public SharedBattlefieldMarkerObjectTemplate(final String filename, final DataResourceList<ObjectTemplate> objectTemplateList) {
        super(filename, objectTemplateList);
    }

    @Override
    public int getId() {
        return TAG_SHAREDBATTLEFIELDMARKEROBJECTTEMPLATE;
    }

    public int getNumberOfPoles() {
        SharedBattlefieldMarkerObjectTemplate base = null;

        if (baseData instanceof SharedBattlefieldMarkerObjectTemplate)
            base = (SharedBattlefieldMarkerObjectTemplate) baseData;

        if (!numberOfPoles.isLoaded()) {
            if (base == null) {
                return 0;
            } else {
                return base.getNumberOfPoles();
            }
        }

        int value = this.numberOfPoles.getValue();
        final byte delta = this.numberOfPoles.getDeltaType();

        if (delta == '+' || delta == '-' || delta == '_' || delta == '=') {
            int baseValue = 0;

            if (baseData != null) {
                if (base != null)
                    baseValue = base.getNumberOfPoles();
            }

            if (delta == '+')
                value = baseValue + value;
            if (delta == '-')
                value = baseValue - value;
            if (delta == '=')
                value = baseValue + (int) (baseValue * (value / 100.0f));
            if (delta == '_')
                value = baseValue - (int) (baseValue * (value / 100.0f));
        }
        return value;
    }

    public int getNumberOfPolesMin() {
        SharedBattlefieldMarkerObjectTemplate base = null;

        if (baseData instanceof SharedBattlefieldMarkerObjectTemplate)
            base = (SharedBattlefieldMarkerObjectTemplate) baseData;

        if (!numberOfPoles.isLoaded()) {
            if (base == null) {
                return 0;
            } else {
                return base.getNumberOfPolesMin();
            }
        }

        int value = this.numberOfPoles.getMinValue();
        final byte delta = this.numberOfPoles.getDeltaType();

        if (delta == '+' || delta == '-' || delta == '_' || delta == '=') {
            int baseValue = 0;

            if (baseData != null) {
                if (base != null)
                    baseValue = base.getNumberOfPolesMin();
            }

            if (delta == '+')
                value = baseValue + value;
            if (delta == '-')
                value = baseValue - value;
            if (delta == '=')
                value = baseValue + (int) (baseValue * (value / 100.0f));
            if (delta == '_')
                value = baseValue - (int) (baseValue * (value / 100.0f));
        }
        return value;
    }

    public int getNumberOfPolesMax() {
        SharedBattlefieldMarkerObjectTemplate base = null;

        if (baseData instanceof SharedBattlefieldMarkerObjectTemplate)
            base = (SharedBattlefieldMarkerObjectTemplate) baseData;

        if (!numberOfPoles.isLoaded()) {
            if (base == null) {
                return 0;
            } else {
                return base.getNumberOfPolesMax();
            }
        }

        int value = this.numberOfPoles.getMaxValue();
        final byte delta = this.numberOfPoles.getDeltaType();

        if (delta == '+' || delta == '-' || delta == '_' || delta == '=') {
            int baseValue = 0;

            if (baseData != null) {
                if (base != null)
                    baseValue = base.getNumberOfPolesMax();
            }

            if (delta == '+')
                value = baseValue + value;
            if (delta == '-')
                value = baseValue - value;
            if (delta == '=')
                value = baseValue + (int) (baseValue * (value / 100.0f));
            if (delta == '_')
                value = baseValue - (int) (baseValue * (value / 100.0f));
        }
        return value;
    }

    public float getRadius() {
        SharedBattlefieldMarkerObjectTemplate base = null;

        if (baseData instanceof SharedBattlefieldMarkerObjectTemplate)
            base = (SharedBattlefieldMarkerObjectTemplate) baseData;

        if (!radius.isLoaded()) {
            if (base == null) {
                return 0.0f;
            } else {
                return base.getRadius();
            }
        }

        float value = this.radius.getValue();
        final byte delta = this.radius.getDeltaType();

        if (delta == '+' || delta == '-' || delta == '_' || delta == '=') {
            float baseValue = 0;

            if (baseData != null) {
                if (base != null)
                    baseValue = base.getRadius();
            }

            if (delta == '+')
                value = baseValue + value;
            if (delta == '-')
                value = baseValue - value;
            if (delta == '=')
                value = baseValue + (float) (baseValue * (value / 100.0f));
            if (delta == '_')
                value = baseValue - (float) (baseValue * (value / 100.0f));
        }
        return value;
    }

    public float getRadiusMin() {
        SharedBattlefieldMarkerObjectTemplate base = null;

        if (baseData instanceof SharedBattlefieldMarkerObjectTemplate)
            base = (SharedBattlefieldMarkerObjectTemplate) baseData;

        if (!radius.isLoaded()) {
            if (base == null) {
                return 0.0f;
            } else {
                return base.getRadiusMin();
            }
        }

        float value = this.radius.getMinValue();
        final byte delta = this.radius.getDeltaType();

        if (delta == '+' || delta == '-' || delta == '_' || delta == '=') {
            float baseValue = 0;

            if (baseData != null) {
                if (base != null)
                    baseValue = base.getRadiusMin();
            }

            if (delta == '+')
                value = baseValue + value;
            if (delta == '-')
                value = baseValue - value;
            if (delta == '=')
                value = baseValue + (float) (baseValue * (value / 100.0f));
            if (delta == '_')
                value = baseValue - (float) (baseValue * (value / 100.0f));
        }
        return value;
    }

    public float getRadiusMax() {
        SharedBattlefieldMarkerObjectTemplate base = null;

        if (baseData instanceof SharedBattlefieldMarkerObjectTemplate)
            base = (SharedBattlefieldMarkerObjectTemplate) baseData;

        if (!radius.isLoaded()) {
            if (base == null) {
                return 0.0f;
            } else {
                return base.getRadiusMax();
            }
        }

        float value = this.radius.getMaxValue();
        final byte delta = this.radius.getDeltaType();

        if (delta == '+' || delta == '-' || delta == '_' || delta == '=') {
            float baseValue = 0;

            if (baseData != null) {
                if (base != null)
                    baseValue = base.getRadiusMax();
            }

            if (delta == '+')
                value = baseValue + value;
            if (delta == '-')
                value = baseValue - value;
            if (delta == '=')
                value = baseValue + (float) (baseValue * (value / 100.0f));
            if (delta == '_')
                value = baseValue - (float) (baseValue * (value / 100.0f));
        }
        return value;
    }

    @Override
    protected void load(final Iff iff) {
        if (iff.getCurrentName() != TAG_SHAREDBATTLEFIELDMARKEROBJECTTEMPLATE) {
            super.load(iff);
            return;
        }

        iff.enterForm();
        templateVersion = iff.getCurrentName();

        if (templateVersion == Tag.TAG_DERV) {
            iff.enterForm();
            iff.enterChunk();
            final String baseFilename = iff.readString();
            iff.exitChunk();
            final ObjectTemplate base = objectTemplateList.fetch(baseFilename);
            Preconditions.checkNotNull(base, "was unable to load base template %s", baseFilename);
            if (baseData == base && base != null) {
                base.releaseReference();
            } else {
                if (baseData != null)
                    baseData.releaseReference();
                baseData = base;
            }
            iff.exitForm();
            templateVersion = iff.getCurrentName();
        }

        iff.enterForm();
        iff.enterChunk();
        final int paramCount = iff.readInt();
        iff.exitChunk();
        for (int i = 0; i < paramCount; ++i) {
            iff.enterChunk();
            final String parameterName = iff.readString();

            if ("numberOfPoles".equalsIgnoreCase(parameterName)) {
                numberOfPoles.loadFromIff(objectTemplateList, iff);
            } else if ("radius".equalsIgnoreCase(parameterName)) {
                radius.loadFromIff(objectTemplateList, iff);
            } else {
                LOGGER.trace("Unexpected parameter {}", parameterName);
            }

            iff.exitChunk();
        }
        iff.exitForm();

        super.load(iff);
        iff.exitForm();
    }

}

