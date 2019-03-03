package io.bacta.shared.radialmenu;

/**
 * Created by crush on 5/31/2016.
 */
public class RadialMenuTypeNotFoundException extends RuntimeException {
    public RadialMenuTypeNotFoundException(final String radialMenuTypeName) {
        super(String.format("Could not find radial menu type with name %s.", radialMenuTypeName));
    }
}
