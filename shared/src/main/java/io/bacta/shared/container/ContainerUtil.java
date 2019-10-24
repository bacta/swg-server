package io.bacta.shared.container;

import io.bacta.shared.object.GameObject;

public final class ContainerUtil {
    public static int getDepth(final Container container) {
        int depth = 0;

        ContainedByProperty parentContainedByProperty = container.getOwner().getContainedByProperty();

        if (parentContainedByProperty == null)
            return depth;

        GameObject parentObject = parentContainedByProperty.getContainedBy();

        if (parentObject == null)
            return depth;

        while (parentObject != null) {
            ++depth;

            parentContainedByProperty = parentObject.getContainedByProperty();

            if (parentContainedByProperty == null)
                return depth;

            parentObject = parentContainedByProperty.getContainedBy();
        }

        return depth;
    }
}
