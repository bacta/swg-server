package io.bacta.shared.object.template;

import com.google.common.base.Preconditions;
import io.bacta.shared.foundation.DataResourceList;
import io.bacta.shared.foundation.Tag;
import io.bacta.shared.iff.Iff;
import io.bacta.shared.template.ObjectTemplate;
import io.bacta.template.definition.TemplateDefinition;
import lombok.extern.slf4j.Slf4j;

/**
 * Generated by the TemplateDefinitionWriter.
 * MANUAL MODIFICATIONS MAY BE OVERWRITTEN.
 */
@Slf4j
@TemplateDefinition
public class SharedCellObjectTemplate extends SharedObjectTemplate {
	public static final int TAG_SHAREDCELLOBJECTTEMPLATE = Tag.convertStringToTag("CCLT");

	private static void registerTemplateConstructors(final DataResourceList<ObjectTemplate> objectTemplateList) {
		objectTemplateList.registerTemplate(SharedCellObjectTemplate.TAG_SHAREDCELLOBJECTTEMPLATE, SharedCellObjectTemplate::new);
	}

	private int templateVersion;


	public SharedCellObjectTemplate(final String filename, final DataResourceList<ObjectTemplate> objectTemplateList) {
		super(filename, objectTemplateList);
	}

	@Override
	public int getId() { return TAG_SHAREDCELLOBJECTTEMPLATE; }

	@Override
	protected void load(final Iff iff) {
		if (iff.getCurrentName() != TAG_SHAREDCELLOBJECTTEMPLATE) {
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
		iff.exitForm();

		super.load(iff);
		iff.exitForm();
	}

}
