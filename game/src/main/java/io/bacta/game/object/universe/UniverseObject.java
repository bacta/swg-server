package io.bacta.game.object.universe;


import io.bacta.game.object.ServerObject;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.shared.object.template.SharedObjectTemplate;
import io.bacta.swg.container.SlotIdManager;
import io.bacta.swg.template.ObjectTemplateList;

import javax.inject.Inject;

/**
 * Created by crush on 9/3/2014.
 * <p>
 * A UniverseObject is an object that is global to the entire server
 * cluster.  UniverseObjects represent global data (such as Resource
 * Classes) or objects with no definite location (such as Resource Pools).
 */
public abstract class UniverseObject extends ServerObject {
    private static SharedObjectTemplate defaultSharedObjectTemplate; //gets set by a service at runtime.

    @Inject
    public UniverseObject(final ObjectTemplateList objectTemplateList,
                          final SlotIdManager slotIdManager,
                          final ServerObjectTemplate template) {
        super(objectTemplateList, slotIdManager, template, false);
    }
}
