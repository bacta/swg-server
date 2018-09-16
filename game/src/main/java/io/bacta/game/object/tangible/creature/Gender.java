package io.bacta.game.object.tangible.creature;


/**
 * Created by kburkhardt on 3/28/14.
 */
public enum Gender {
    MALE, FEMALE;

    public static Gender parseGender(final String templateString) {
        final String gender = templateString.substring(templateString.lastIndexOf("_") + 1, templateString.indexOf(".iff"));
        return Gender.valueOf(gender.toUpperCase());
    }

    /**
     * Takes a string of the format <code>human_male</code>, extracts the gender portion, and returns the
     * Gender type.
     * @param genderSpecies The genderSpecies string in the form of <code>human_male</code>.
     * @return The gender.
     */
    public static Gender fromSpeciesGender(final String genderSpecies) {
        final int underscoreIndex = genderSpecies.lastIndexOf('_');

        if (underscoreIndex == -1)
            throw new IllegalArgumentException("Invalid genderSpecies string provided. Must contain an underscore.");

        final String gender = genderSpecies.substring(underscoreIndex + 1);

        return Gender.valueOf(gender.toUpperCase());
    }
}
