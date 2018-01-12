package io.bacta.login.server.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Character information that is stored on the login server. This information allows the SwgClient to
 * display a quick preview of the character when selecting a character, before the player has been transferred
 * to the selected galaxy server.
 */
@Getter
@RequiredArgsConstructor
public final class CharacterRecord {
    private final String name;
    private final int objectTemplateId;
    private final long networkId;
    private final int galaxyId;
    private final int characterType;
}
