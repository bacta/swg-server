package io.bacta.shared.tre.math;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by crush on 5/15/2016.
 */
@Getter
@AllArgsConstructor
public final class Triangle2d {
    private final Vector2d cornerA;
    private final Vector2d cornerB;
    private final Vector2d cornerC;

    public Triangle2d() {
        this.cornerA = Vector2d.ZERO;
        this.cornerB = Vector2d.ZERO;
        this.cornerC = Vector2d.ZERO;
    }

    public Vector2d getCorner(final int i) {
        final int index = i % 3;

        switch (index) {
            default:
            case 0:
                return cornerA;
            case 1:
                return cornerB;
            case 2:
                return cornerC;
        }
    }
}
