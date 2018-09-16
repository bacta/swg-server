package io.bacta.game.object.template.server;


import com.google.common.base.Preconditions;
import io.bacta.shared.foundation.DataResourceList;
import io.bacta.shared.foundation.Tag;
import io.bacta.shared.iff.Iff;
import io.bacta.shared.template.ObjectTemplate;
import io.bacta.shared.template.definition.TemplateDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generated by the TemplateDefinitionWriter.
 * MANUAL MODIFICATIONS MAY BE OVERWRITTEN.
 */
@TemplateDefinition
public class ServerBattlefieldMarkerObjectTemplate extends ServerTangibleObjectTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerBattlefieldMarkerObjectTemplate.class);
    public static final int TAG_SERVERBATTLEFIELDMARKEROBJECTTEMPLATE = Tag.convertStringToTag("BMRK");

    private static void registerTemplateConstructors(final DataResourceList<ObjectTemplate> objectTemplateList) {
        objectTemplateList.registerTemplate(ServerBattlefieldMarkerObjectTemplate.TAG_SERVERBATTLEFIELDMARKEROBJECTTEMPLATE, ServerBattlefieldMarkerObjectTemplate::new);
    }

    private int templateVersion;


    public ServerBattlefieldMarkerObjectTemplate(final String filename, final DataResourceList<ObjectTemplate> objectTemplateList) {
        super(filename, objectTemplateList);
    }

    @Override
    public int getId() {
        return TAG_SERVERBATTLEFIELDMARKEROBJECTTEMPLATE;
    }

    @Override
    protected void load(final Iff iff) {
        if (iff.getCurrentName() != TAG_SERVERBATTLEFIELDMARKEROBJECTTEMPLATE) {
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

