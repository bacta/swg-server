package com.ocdsoft.bacta.swg.login.db;

import com.ocdsoft.bacta.swg.login.object.Account;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Created by crush on 6/8/2017.
 */
@Entity
@Getter
@Setter
public final class AccountEntity {
    public static final String SEQUENCE_ACCOUNT_ID = "AccountId_Sequence";
    public static final String KEY_ACCOUNT_USERNAME = "username";

    @PrimaryKey(sequence = SEQUENCE_ACCOUNT_ID)
    private int id;
    @SecondaryKey(relate = Relationship.ONE_TO_ONE)
    private String username;
    private String password;
    private LocalDateTime created;
    private int adminLevel;

    public AccountEntity(Account account) {
        setId(account.getId());
        setUsername(account.getUsername());
        setPassword(account.getPassword());
        setAdminLevel(account.getAdminLevel());
    }
}
