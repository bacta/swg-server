package io.bacta.swg.utility;

import io.bacta.swg.math.Vector;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by crush on 6/4/2016.
 */
@Getter
@AllArgsConstructor
public final class WeatherData {
    private final Vector wind;
    private final int weatherIntensity;
    private final int temperature;

    public String getDebugString() {
        return String.format("Wind (%f, %f, %f), Temperature: %d, Weather: %d",
                wind.x,
                wind.y,
                wind.z,
                temperature,
                weatherIntensity);
    }
}
