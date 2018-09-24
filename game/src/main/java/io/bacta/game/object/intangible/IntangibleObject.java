package io.bacta.game.object.intangible;

import io.bacta.archive.delta.AutoDeltaInt;
import io.bacta.game.object.ServerObject;
import io.bacta.game.object.template.server.ServerIntangibleObjectTemplate;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.swg.container.SlotIdManager;
import io.bacta.swg.template.ObjectTemplateList;

import javax.inject.Inject;

public class IntangibleObject extends ServerObject {
    private final AutoDeltaInt count;

    @Inject
    public IntangibleObject(final ObjectTemplateList objectTemplateList,
                            final SlotIdManager slotIdManager,
                            final ServerObjectTemplate template) {
        super(objectTemplateList, slotIdManager, template, false);

        assert template instanceof ServerIntangibleObjectTemplate;

        final ServerIntangibleObjectTemplate objectTemplate = (ServerIntangibleObjectTemplate) template;

        count = new AutoDeltaInt(objectTemplate.getCount());

        addMembersToPackages();
    }

    private void addMembersToPackages() {
        sharedPackage.addVariable(count);
    }

    @Override
    protected void sendObjectSpecificBaselinesToClient(final SoeConnection client) {
        //IsFlattenedTheaterMessage<pair<long, bool>>
    }

    public enum TheaterLocationType {
        NONE,
        GET_GOOD_LOCATION,
        FLATTEN
    }

    ;
}
