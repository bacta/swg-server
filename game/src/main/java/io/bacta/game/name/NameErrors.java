package io.bacta.game.name;

import io.bacta.shared.localization.StringId;

public final class NameErrors {
    private static final String localizationFile = "ui";

    public static final StringId APPROVED = new StringId(localizationFile, "name_approved");
    public static final StringId APPROVED_MODIFIED = new StringId(localizationFile, "name_approved_modified");
    public static final StringId SYNTAX = new StringId(localizationFile, "name_declined_syntax");
    public static final StringId EMPTY = new StringId(localizationFile, "name_declined_empty");
    public static final StringId RACIALLY_INAPPROPRIATE = new StringId(localizationFile, "name_declined_racially_inappropriate");
    public static final StringId FICTIONARLLY_INAPPROPRIATE = new StringId(localizationFile, "name_declined_fictionally_inappropriate");
    public static final StringId PROFANE = new StringId(localizationFile, "name_declined_profane");
    public static final StringId IN_USE = new StringId(localizationFile, "name_declined_in_use");
    public static final StringId RESERVED = new StringId(localizationFile, "name_declined_reserved");
    public static final StringId NO_TEMPLATE = new StringId(localizationFile, "name_declined_no_template");
    public static final StringId NOT_CREATURE_TEMPLATE = new StringId(localizationFile, "name_declined_not_creature_template");
    public static final StringId NO_NAME_GENERATOR = new StringId(localizationFile, "name_declined_no_name_generator");
    public static final StringId CANT_CREATE_AVATAR = new StringId(localizationFile, "name_declined_cant_create_avatar");
    public static final StringId INTERNAL_ERROR = new StringId(localizationFile, "name_declined_internal_error");
    public static final StringId RETRY = new StringId(localizationFile, "name_declined_retry");
    public static final StringId TOO_FAST = new StringId(localizationFile, "name_declined_too_fast");
    public static final StringId NOT_AUTHORIZED_FOR_SPECIES = new StringId(localizationFile, "name_declined_not_authorized_for_species");
    public static final StringId DEVELOPER = new StringId(localizationFile, "name_declined_developer");
    public static final StringId NUMBER = new StringId(localizationFile, "name_declined_number");
}
