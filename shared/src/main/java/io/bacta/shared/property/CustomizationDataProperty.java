package io.bacta.shared.property;

import io.bacta.shared.object.GameObject;

public class CustomizationDataProperty extends Property {
    public static int getClassPropertyId() {
        return 0xDE36CB96;
    }

    public CustomizationDataProperty(final GameObject owner) {
        super(getClassPropertyId(), owner);
    }
}
