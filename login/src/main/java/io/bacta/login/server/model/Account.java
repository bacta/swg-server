package io.bacta.login.server.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "accounts", indexes = {@Index(name = "IDX_USERNAME", columnList = "username", unique = true)})
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private ZonedDateTime created = ZonedDateTime.now(ZoneOffset.UTC);

    @Column(nullable = false, unique = true)
    private String username;

    private String encodedPassword;

    protected Account() {}

    public Account(final String username,
                   final String encodedPassword) {
        this.username = username;
        this.encodedPassword = encodedPassword;
    }
}
