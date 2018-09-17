package io.bacta.game.object.tangible.creature;

/**
 * Created by kburkhardt on 3/28/14.
 */
public enum Race {
    AQUALISH,
    BITH,
    BOTHAN,
    HUMAN,
    MONCAL,
    RODIAN,
    TRANDOSHAN,
    TWILEK,
    WOOKIEE,
    ZABRAK;

    public static Race parseRace(String templateString) {
        final String race = templateString.substring(templateString.lastIndexOf("/") + 1, templateString.lastIndexOf("_"));
        return Race.valueOf(race.toUpperCase());
    }

    /**
     * Takes a string of the format <code>human_male</code>, extracts the race portion, and returns the
     * Race type.
     * @param genderSpecies The genderSpecies string in the form of <code>human_male</code>.
     * @return The race.
     */
    public static Race fromSpeciesGender(final String genderSpecies) {
        final int underscoreIndex = genderSpecies.lastIndexOf('_');

        if (underscoreIndex == -1)
            throw new IllegalArgumentException("Invalid genderSpecies string provided. Must contain an underscore.");

        final String race = genderSpecies.substring(0, underscoreIndex);

        return Race.valueOf(race.toUpperCase());
    }
}
