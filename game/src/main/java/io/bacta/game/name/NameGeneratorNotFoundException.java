package io.bacta.game.name;

import lombok.Getter;

@Getter
public final class NameGeneratorNotFoundException extends Exception {
    private final String creatureTemplate;

    public NameGeneratorNotFoundException(final String creatureTemplate) {
        this.creatureTemplate = creatureTemplate;
    }
}
