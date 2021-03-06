package io.bacta.game.object.template.server;

import com.google.common.base.Preconditions;
import io.bacta.shared.foundation.DataResourceList;
import io.bacta.shared.foundation.Tag;
import io.bacta.shared.iff.Iff;
import io.bacta.shared.template.ObjectTemplate;
import io.bacta.shared.utility.BoolParam;
import io.bacta.shared.utility.IntegerParam;
import io.bacta.template.definition.TemplateDefinition;
import lombok.extern.slf4j.Slf4j;

/**
 * Generated by the TemplateDefinitionWriter.
 * MANUAL MODIFICATIONS MAY BE OVERWRITTEN.
 */
@Slf4j
@TemplateDefinition
public class ServerBuildingObjectTemplate extends ServerTangibleObjectTemplate {
	public static final int TAG_SERVERBUILDINGOBJECTTEMPLATE = Tag.convertStringToTag("BUIO");

	private static void registerTemplateConstructors(final DataResourceList<ObjectTemplate> objectTemplateList) {
		objectTemplateList.registerTemplate(ServerBuildingObjectTemplate.TAG_SERVERBUILDINGOBJECTTEMPLATE, ServerBuildingObjectTemplate::new);
	}

	private int templateVersion;

	private final IntegerParam maintenanceCost = new IntegerParam(); //The weekly cost (in credits) of maintaining this Building.
	private final BoolParam isPublic = new BoolParam(); //Whether by default the building is flagged public.

	public ServerBuildingObjectTemplate(final String filename, final DataResourceList<ObjectTemplate> objectTemplateList) {
		super(filename, objectTemplateList);
	}

	@Override
	public int getId() { return TAG_SERVERBUILDINGOBJECTTEMPLATE; }

	public int getMaintenanceCost() {
		ServerBuildingObjectTemplate base = null;

		if (baseData instanceof ServerBuildingObjectTemplate)
			base = (ServerBuildingObjectTemplate)baseData;

		if (!maintenanceCost.isLoaded()) {
			if (base == null) {
				return 0;
			} else {
				return base.getMaintenanceCost();
			}
		}

		int value = this.maintenanceCost.getValue();
		final byte delta = this.maintenanceCost.getDeltaType();

		if (delta == '+' || delta == '-' || delta == '_' || delta == '=') {
			int baseValue = 0;

				if (baseData != null) {
					if (base != null)
						baseValue = base.getMaintenanceCost();
				}

			if (delta == '+')
				value = baseValue + value;
			if (delta == '-')
				value = baseValue - value;
			if (delta == '=')
				value = baseValue + (int)(baseValue * (value / 100.0f));
			if (delta == '_')
				value = baseValue - (int)(baseValue * (value / 100.0f));
		}
		return value;
	}

	public int getMaintenanceCostMin() {
		ServerBuildingObjectTemplate base = null;

		if (baseData instanceof ServerBuildingObjectTemplate)
			base = (ServerBuildingObjectTemplate)baseData;

		if (!maintenanceCost.isLoaded()) {
			if (base == null) {
				return 0;
			} else {
				return base.getMaintenanceCostMin();
			}
		}

		int value = this.maintenanceCost.getMinValue();
		final byte delta = this.maintenanceCost.getDeltaType();

		if (delta == '+' || delta == '-' || delta == '_' || delta == '=') {
			int baseValue = 0;

				if (baseData != null) {
					if (base != null)
						baseValue = base.getMaintenanceCostMin();
				}

			if (delta == '+')
				value = baseValue + value;
			if (delta == '-')
				value = baseValue - value;
			if (delta == '=')
				value = baseValue + (int)(baseValue * (value / 100.0f));
			if (delta == '_')
				value = baseValue - (int)(baseValue * (value / 100.0f));
		}
		return value;
	}

	public int getMaintenanceCostMax() {
		ServerBuildingObjectTemplate base = null;

		if (baseData instanceof ServerBuildingObjectTemplate)
			base = (ServerBuildingObjectTemplate)baseData;

		if (!maintenanceCost.isLoaded()) {
			if (base == null) {
				return 0;
			} else {
				return base.getMaintenanceCostMax();
			}
		}

		int value = this.maintenanceCost.getMaxValue();
		final byte delta = this.maintenanceCost.getDeltaType();

		if (delta == '+' || delta == '-' || delta == '_' || delta == '=') {
			int baseValue = 0;

				if (baseData != null) {
					if (base != null)
						baseValue = base.getMaintenanceCostMax();
				}

			if (delta == '+')
				value = baseValue + value;
			if (delta == '-')
				value = baseValue - value;
			if (delta == '=')
				value = baseValue + (int)(baseValue * (value / 100.0f));
			if (delta == '_')
				value = baseValue - (int)(baseValue * (value / 100.0f));
		}
		return value;
	}

	public boolean getIsPublic() {
		ServerBuildingObjectTemplate base = null;

		if (baseData instanceof ServerBuildingObjectTemplate)
			base = (ServerBuildingObjectTemplate)baseData;

		if (!isPublic.isLoaded()) {
			if (base == null) {
				return false;
			} else {
				return base.getIsPublic();
			}
		}

		boolean value = this.isPublic.getValue();
		return value;
	}

	@Override
	protected void load(final Iff iff) {
		if (iff.getCurrentName() != TAG_SERVERBUILDINGOBJECTTEMPLATE) {
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

			if ("maintenanceCost".equalsIgnoreCase(parameterName)) {
				maintenanceCost.loadFromIff(objectTemplateList, iff);
			} else if ("isPublic".equalsIgnoreCase(parameterName)) {
				isPublic.loadFromIff(objectTemplateList, iff);
			} else  {
				LOGGER.trace("Unexpected parameter {}", parameterName);
			}

			iff.exitChunk();
		}
		iff.exitForm();

		super.load(iff);
		iff.exitForm();
	}

}

