package com.ocdsoft.bacta.swg.login.entity;

import lombok.Getter;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by crush on 7/2/2017.
 */
@Getter
@Entity
@Table(name = "accounts")
public final class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public Account(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.created = new Date();
    }
}
