package io.bacta.shared.container;

import io.bacta.shared.object.GameObject;
import io.bacta.shared.property.Property;

public class ContainedByProperty extends Property {
    public static int getClassPropertyId() {
        return 0x704360EC;
    }

    //private long containedBy;
    private GameObject containedByObject;

    public ContainedByProperty(final GameObject thisObject, final GameObject containedByObject) {
        super(getClassPropertyId(), thisObject);

        this.containedByObject = containedByObject;
    }

    public GameObject getContainedBy() {
        //return NetworkIdManager.getObject(containedBy);
        return containedByObject;
    }

    public long getContainedByNetworkId() {
        return containedByObject.getNetworkId();
    }

    public void setContainedBy(final GameObject object) {
        setContainedBy(object, true);
    }

    public void setContainedBy(final GameObject object, boolean local) {
        if (containedByObject != object) {
            final GameObject oldObject = containedByObject;
            containedByObject = object;

            //getOwner().containedByModified(oldObject, object, local);
        }
    }
}
