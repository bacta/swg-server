package io.bacta.game.object.template.server;

import com.google.common.base.Preconditions;
import io.bacta.shared.foundation.DataResourceList;
import io.bacta.shared.foundation.Tag;
import io.bacta.shared.iff.Iff;
import io.bacta.shared.template.ObjectTemplate;
import io.bacta.shared.utility.FloatParam;
import io.bacta.shared.utility.StringParam;
import io.bacta.template.definition.TemplateDefinition;
import lombok.extern.slf4j.Slf4j;

/**
 * Generated by the TemplateDefinitionWriter.
 * MANUAL MODIFICATIONS MAY BE OVERWRITTEN.
 */
@Slf4j
@TemplateDefinition
public class ServerVehicleObjectTemplate extends ServerTangibleObjectTemplate {
	public static final int TAG_SERVERVEHICLEOBJECTTEMPLATE = Tag.convertStringToTag("VEHO");

	private static void registerTemplateConstructors(final DataResourceList<ObjectTemplate> objectTemplateList) {
		objectTemplateList.registerTemplate(ServerVehicleObjectTemplate.TAG_SERVERVEHICLEOBJECTTEMPLATE, ServerVehicleObjectTemplate::new);
	}

	private int templateVersion;

	private final StringParam fuelType = new StringParam(); //type of fuel used
	private final FloatParam currentFuel = new FloatParam(); //current amount of fuel the vehicle has
	private final FloatParam maxFuel = new FloatParam(); //max amount of fuel the vehicle can hold
	private final FloatParam consumpsion = new FloatParam(); //units/sec/speed(?) fuel used

	public ServerVehicleObjectTemplate(final String filename, final DataResourceList<ObjectTemplate> objectTemplateList) {
		super(filename, objectTemplateList);
	}

	@Override
	public int getId() { return TAG_SERVERVEHICLEOBJECTTEMPLATE; }

	public String getFuelType() {
		ServerVehicleObjectTemplate base = null;

		if (baseData instanceof ServerVehicleObjectTemplate)
			base = (ServerVehicleObjectTemplate)baseData;

		if (!fuelType.isLoaded()) {
			if (base == null) {
				return "";
			} else {
				return base.getFuelType();
			}
		}

		String value = this.fuelType.getValue();
		return value;
	}

	public float getCurrentFuel() {
		ServerVehicleObjectTemplate base = null;

		if (baseData instanceof ServerVehicleObjectTemplate)
			base = (ServerVehicleObjectTemplate)baseData;

		if (!currentFuel.isLoaded()) {
			if (base == null) {
				return 0.0f;
			} else {
				return base.getCurrentFuel();
			}
		}

		float value = this.currentFuel.getValue();
		final byte delta = this.currentFuel.getDeltaType();

		if (delta == '+' || delta == '-' || delta == '_' || delta == '=') {
			float baseValue = 0;

				if (baseData != null) {
					if (base != null)
						baseValue = base.getCurrentFuel();
				}

			if (delta == '+')
				value = baseValue + value;
			if (delta == '-')
				value = baseValue - value;
			if (delta == '=')
				value = baseValue + (float)(baseValue * (value / 100.0f));
			if (delta == '_')
				value = baseValue - (float)(baseValue * (value / 100.0f));
		}
		return value;
	}

	public float getCurrentFuelMin() {
		ServerVehicleObjectTemplate base = null;

		if (baseData instanceof ServerVehicleObjectTemplate)
			base = (ServerVehicleObjectTemplate)baseData;

		if (!currentFuel.isLoaded()) {
			if (base == null) {
				return 0.0f;
			} else {
				return base.getCurrentFuelMin();
			}
		}

		float value = this.currentFuel.getMinValue();
		final byte delta = this.currentFuel.getDeltaType();

		if (delta == '+' || delta == '-' || delta == '_' || delta == '=') {
			float baseValue = 0;

				if (baseData != null) {
					if (base != null)
						baseValue = base.getCurrentFuelMin();
				}

			if (delta == '+')
				value = baseValue + value;
			if (delta == '-')
				value = baseValue - value;
			if (delta == '=')
				value = baseValue + (float)(baseValue * (value / 100.0f));
			if (delta == '_')
				value = baseValue - (float)(baseValue * (value / 100.0f));
		}
		return value;
	}

	public float getCurrentFuelMax() {
		ServerVehicleObjectTemplate base = null;

		if (baseData instanceof ServerVehicleObjectTemplate)
			base = (ServerVehicleObjectTemplate)baseData;

		if (!currentFuel.isLoaded()) {
			if (base == null) {
				return 0.0f;
			} else {
				return base.getCurrentFuelMax();
			}
		}

		float value = this.currentFuel.getMaxValue();
		final byte delta = this.currentFuel.getDeltaType();

		if (delta == '+' || delta == '-' || delta == '_' || delta == '=') {
			float baseValue = 0;

				if (baseData != null) {
					if (base != null)
						baseValue = base.getCurrentFuelMax();
				}

			if (delta == '+')
				value = baseValue + value;
			if (delta == '-')
				value = baseValue - value;
			if (delta == '=')
				value = baseValue + (float)(baseValue * (value / 100.0f));
			if (delta == '_')
				value = baseValue - (float)(baseValue * (value / 100.0f));
		}
		return value;
	}

	public float getMaxFuel() {
		ServerVehicleObjectTemplate base = null;

		if (baseData instanceof ServerVehicleObjectTemplate)
			base = (ServerVehicleObjectTemplate)baseData;

		if (!maxFuel.isLoaded()) {
			if (base == null) {
				return 0.0f;
			} else {
				return base.getMaxFuel();
			}
		}

		float value = this.maxFuel.getValue();
		final byte delta = this.maxFuel.getDeltaType();

		if (delta == '+' || delta == '-' || delta == '_' || delta == '=') {
			float baseValue = 0;

				if (baseData != null) {
					if (base != null)
						baseValue = base.getMaxFuel();
				}

			if (delta == '+')
				value = baseValue + value;
			if (delta == '-')
				value = baseValue - value;
			if (delta == '=')
				value = baseValue + (float)(baseValue * (value / 100.0f));
			if (delta == '_')
				value = baseValue - (float)(baseValue * (value / 100.0f));
		}
		return value;
	}

	public float getMaxFuelMin() {
		ServerVehicleObjectTemplate base = null;

		if (baseData instanceof ServerVehicleObjectTemplate)
			base = (ServerVehicleObjectTemplate)baseData;

		if (!maxFuel.isLoaded()) {
			if (base == null) {
				return 0.0f;
			} else {
				return base.getMaxFuelMin();
			}
		}

		float value = this.maxFuel.getMinValue();
		final byte delta = this.maxFuel.getDeltaType();

		if (delta == '+' || delta == '-' || delta == '_' || delta == '=') {
			float baseValue = 0;

				if (baseData != null) {
					if (base != null)
						baseValue = base.getMaxFuelMin();
				}

			if (delta == '+')
				value = baseValue + value;
			if (delta == '-')
				value = baseValue - value;
			if (delta == '=')
				value = baseValue + (float)(baseValue * (value / 100.0f));
			if (delta == '_')
				value = baseValue - (float)(baseValue * (value / 100.0f));
		}
		return value;
	}

	public float getMaxFuelMax() {
		ServerVehicleObjectTemplate base = null;

		if (baseData instanceof ServerVehicleObjectTemplate)
			base = (ServerVehicleObjectTemplate)baseData;

		if (!maxFuel.isLoaded()) {
			if (base == null) {
				return 0.0f;
			} else {
				return base.getMaxFuelMax();
			}
		}

		float value = this.maxFuel.getMaxValue();
		final byte delta = this.maxFuel.getDeltaType();

		if (delta == '+' || delta == '-' || delta == '_' || delta == '=') {
			float baseValue = 0;

				if (baseData != null) {
					if (base != null)
						baseValue = base.getMaxFuelMax();
				}

			if (delta == '+')
				value = baseValue + value;
			if (delta == '-')
				value = baseValue - value;
			if (delta == '=')
				value = baseValue + (float)(baseValue * (value / 100.0f));
			if (delta == '_')
				value = baseValue - (float)(baseValue * (value / 100.0f));
		}
		return value;
	}

	public float getConsumpsion() {
		ServerVehicleObjectTemplate base = null;

		if (baseData instanceof ServerVehicleObjectTemplate)
			base = (ServerVehicleObjectTemplate)baseData;

		if (!consumpsion.isLoaded()) {
			if (base == null) {
				return 0.0f;
			} else {
				return base.getConsumpsion();
			}
		}

		float value = this.consumpsion.getValue();
		final byte delta = this.consumpsion.getDeltaType();

		if (delta == '+' || delta == '-' || delta == '_' || delta == '=') {
			float baseValue = 0;

				if (baseData != null) {
					if (base != null)
						baseValue = base.getConsumpsion();
				}

			if (delta == '+')
				value = baseValue + value;
			if (delta == '-')
				value = baseValue - value;
			if (delta == '=')
				value = baseValue + (float)(baseValue * (value / 100.0f));
			if (delta == '_')
				value = baseValue - (float)(baseValue * (value / 100.0f));
		}
		return value;
	}

	public float getConsumpsionMin() {
		ServerVehicleObjectTemplate base = null;

		if (baseData instanceof ServerVehicleObjectTemplate)
			base = (ServerVehicleObjectTemplate)baseData;

		if (!consumpsion.isLoaded()) {
			if (base == null) {
				return 0.0f;
			} else {
				return base.getConsumpsionMin();
			}
		}

		float value = this.consumpsion.getMinValue();
		final byte delta = this.consumpsion.getDeltaType();

		if (delta == '+' || delta == '-' || delta == '_' || delta == '=') {
			float baseValue = 0;

				if (baseData != null) {
					if (base != null)
						baseValue = base.getConsumpsionMin();
				}

			if (delta == '+')
				value = baseValue + value;
			if (delta == '-')
				value = baseValue - value;
			if (delta == '=')
				value = baseValue + (float)(baseValue * (value / 100.0f));
			if (delta == '_')
				value = baseValue - (float)(baseValue * (value / 100.0f));
		}
		return value;
	}

	public float getConsumpsionMax() {
		ServerVehicleObjectTemplate base = null;

		if (baseData instanceof ServerVehicleObjectTemplate)
			base = (ServerVehicleObjectTemplate)baseData;

		if (!consumpsion.isLoaded()) {
			if (base == null) {
				return 0.0f;
			} else {
				return base.getConsumpsionMax();
			}
		}

		float value = this.consumpsion.getMaxValue();
		final byte delta = this.consumpsion.getDeltaType();

		if (delta == '+' || delta == '-' || delta == '_' || delta == '=') {
			float baseValue = 0;

				if (baseData != null) {
					if (base != null)
						baseValue = base.getConsumpsionMax();
				}

			if (delta == '+')
				value = baseValue + value;
			if (delta == '-')
				value = baseValue - value;
			if (delta == '=')
				value = baseValue + (float)(baseValue * (value / 100.0f));
			if (delta == '_')
				value = baseValue - (float)(baseValue * (value / 100.0f));
		}
		return value;
	}

	@Override
	protected void load(final Iff iff) {
		if (iff.getCurrentName() != TAG_SERVERVEHICLEOBJECTTEMPLATE) {
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

			if ("fuelType".equalsIgnoreCase(parameterName)) {
				fuelType.loadFromIff(objectTemplateList, iff);
			} else if ("currentFuel".equalsIgnoreCase(parameterName)) {
				currentFuel.loadFromIff(objectTemplateList, iff);
			} else if ("maxFuel".equalsIgnoreCase(parameterName)) {
				maxFuel.loadFromIff(objectTemplateList, iff);
			} else if ("consumpsion".equalsIgnoreCase(parameterName)) {
				consumpsion.loadFromIff(objectTemplateList, iff);
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

