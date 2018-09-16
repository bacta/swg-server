package io.bacta.game.name;

import io.bacta.shared.localization.StringId;

public final class NameErrors {
    private static final String localizationFile = "ui";

    public static StringId APPROVED = new StringId(localizationFile, "name_approved");
    public static StringId APPROVED_MODIFIED = new StringId(localizationFile, "name_approved_modified");
    public static StringId SYNTAX = new StringId(localizationFile, "name_declined_syntax");
    public static StringId EMPTY = new StringId(localizationFile, "name_declined_empty");
    public static StringId RACIALLY_INAPPROPRIATE = new StringId(localizationFile, "name_declined_racially_inappropriate");
    public static StringId FICTIONARLLY_INAPPROPRIATE = new StringId(localizationFile, "name_declined_fictionally_inappropriate");
    public static StringId PROFANE = new StringId(localizationFile, "name_declined_profane");
    public static StringId IN_USE = new StringId(localizationFile, "name_declined_in_use");
    public static StringId RESERVED = new StringId(localizationFile, "name_declined_reserved");
    public static StringId NO_TEMPLATE = new StringId(localizationFile, "name_declined_no_template");
    public static StringId NOT_CREATURE_TEMPLATE = new StringId(localizationFile, "name_declined_not_creature_template");
    public static StringId NO_NAME_GENERATOR = new StringId(localizationFile, "name_declined_no_name_generator");
    public static StringId CANT_CREATE_AVATAR = new StringId(localizationFile, "name_declined_cant_create_avatar");
    public static StringId INTERNAL_ERROR = new StringId(localizationFile, "name_declined_internal_error");
    public static StringId RETRY = new StringId(localizationFile, "name_declined_retry");
    public static StringId TOO_FAST = new StringId(localizationFile, "name_declined_too_fast");
    public static StringId NOT_AUTHORIZED_FOR_SPECIES = new StringId(localizationFile, "name_declined_not_authorized_for_species");
}
