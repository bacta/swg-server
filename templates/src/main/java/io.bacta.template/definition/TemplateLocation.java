package io.bacta.template.definition;

/**
 * Created by crush on 4/19/2016.
 */
public enum TemplateLocation {
    NONE,
    CLIENT,
    SERVER,
    SHARED;

    private final String[] locationNames = new String[]{
            "", "Client", "Server", "Shared"
    };

    public String getName() {
        return locationNames[ordinal()];
    }
}
