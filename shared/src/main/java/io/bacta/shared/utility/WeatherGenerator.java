package io.bacta.shared.utility;

/**
 * Created by crush on 6/4/2016.
 */
public class WeatherGenerator {
    private int updateInterval;
    private WeatherData weather;

    private int minTemperature;
    private int maxTemperature;
    private int minWind;
    private int maxWind;

    public WeatherGenerator(final int updateInterval) {
        this.updateInterval = updateInterval;
    }

    private void generateWeather(final long serverTime) {

    }
}
