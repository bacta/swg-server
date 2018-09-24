package io.bacta.swg.container;

import io.bacta.swg.object.GameObject;
import io.bacta.swg.property.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by crush on 8/26/2014.
 */
public abstract class Container extends Property {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Container.class);

    public static int getClassPropertyId() {
        return 0x55DC5726;
    }

    private final List<GameObject> contents = new ArrayList<>();
    private boolean changed = false;

    public Container(final int propertyId, final GameObject owner) {
        super(propertyId, owner);
    }

    public Iterator<GameObject> iterator() {
        return contents.iterator();
    }

    public boolean hasChanged() {
        return changed;
    }
}
