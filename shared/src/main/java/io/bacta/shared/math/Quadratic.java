package io.bacta.shared.math;

import lombok.AllArgsConstructor;

/**
 * Created by crush on 5/15/2016.
 * <p>
 * Helps solve quadratic equations.
 */
@AllArgsConstructor
public final class Quadratic {
    public final float a;
    public final float b;
    public final float c;

    public QuadraticResult solveFor(final float value) {
        if (a == 0.f && b == 0.f) {
            if (c == value)
                return new QuadraticResult(-Float.MAX_VALUE, Float.MAX_VALUE, true);
            else
                return new QuadraticResult(0.f, 0.f, false);
        }

        if (a == 0.f) {
            final float val = (value - c) / b;
            return new QuadraticResult(val, val, true);
        }

        final float tempC = c - value;

        final float det = b * b - 4 * a * c;

        if (det < 0)
            return new QuadraticResult(0.f, 0.f, false);

        final float s = (float) Math.sqrt(det);
        final float i = 1.f / (2.f * a);

        float o1 = (-b + s) * i;
        float o2 = (-b - s) * i;

        return new QuadraticResult(
                o1 < o2 ? o1 : o2,
                o1 > o2 ? o1 : o2,
                true);
    }

    @AllArgsConstructor
    public static final class QuadraticResult {
        public final float out1;
        public final float out2;
        public final boolean success;
    }
}
