package io.bacta.login.server.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Character information that is stored on the login server. This information allows the SwgClient to
 * display a quick preview of the character when selecting a character, before the player has been transferred
 * to the selected galaxy server.
 */
@Data
@Entity
@Table(name = "characters")
@IdClass(Character.CharacterEntityKey.class)
@NoArgsConstructor
public class Character {
    @Id
    private long networkId;
    @Id
    private int galaxyId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int objectTemplateId;

    @Column(nullable = false)
    private int characterType;

    @Id
    @Column(nullable = false)
    private int bactaId;

    @EqualsAndHashCode
    public static class CharacterEntityKey implements Serializable {
        private long networkId;
        private int galaxyId;
    }
}
