package io.bacta.login.server.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Represents an account in the Bacta Network.
 */
@Data
@Entity
@Table(name = "accounts", indexes = {@Index(name = "IDX_USERNAME", columnList = "username", unique = true)})
@NoArgsConstructor
public final class BactaAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private ZonedDateTime created = ZonedDateTime.now();

    @Column(nullable = false, unique = true)
    private String username;

    private String encodedPassword;

    public BactaAccount(String username, String encodedPassword) {
        this.username = username;
        this.encodedPassword = encodedPassword;
    }
}
