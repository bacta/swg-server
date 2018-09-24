package io.bacta.swg.radialmenu;

/**
 * Created by crush on 5/31/2016.
 */
public class DuplicateRadialMenuTypeException extends RuntimeException {
    public DuplicateRadialMenuTypeException(final short menuType) {
        super(String.format("The menu type %d already existed in the radial menu.", menuType));
    }
}
