package com.ocdsoft.bacta.swg.login.object;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Kyle on 4/3/14.
 */
@Data
public final class SoeAccount {

    @Setter(AccessLevel.NONE)
    private int id;

    private String username;

    private String password;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private List<CharacterInfo> characterList = new ArrayList<>();

    private String authToken = "";
    private long authExpiration;
    private InetAddress authInetAddress;
    private long lastCharacterCreationTime;

    @Getter(AccessLevel.NONE)
    private final String type = "account";

    public SoeAccount() {}

    public SoeAccount(final int id) {
        this.id = id;
    }

    public List<CharacterInfo> getCharacterList() {
        return characterList.stream()
                .filter(info -> !info.isDisabled())
                .collect(Collectors.toList());
    }

    public List<CharacterInfo> getDeletedCharacterList() {

        return characterList.stream()
                .filter(CharacterInfo::isDisabled)
                .collect(Collectors.toList());    }

    public void addCharacter(final CharacterInfo info) {
        characterList.add(info);
    }

    public CharacterInfo getCharacter(final long characterId) {

        for(CharacterInfo characterInfo : getCharacterList()) {
            if (characterInfo.getCharacterId() == characterId) {
                return characterInfo;
            }
        }

        return null;
    }

    public boolean hasCharacter(final long characterId) {
        return getCharacter(characterId) != null;
    }
}
