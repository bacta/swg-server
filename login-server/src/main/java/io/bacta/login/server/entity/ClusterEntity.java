/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.login.server.entity;

import io.bacta.login.server.object.ClusterListEntry;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.persistence.*;

/**
 * Created by crush on 7/2/2017.
 */
@Entity
@Table(name = "clusters")
@Getter
@Setter
public final class ClusterEntity {
    /**
     * The unique id for the cluster.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * The unique name for the cluster.
     */
    @Column(unique = true, nullable = false)
    private String name;
    /**
     * The ip address for the cluster server.
     */
    @Column(nullable = false)
    private String address;
    /**
     * The port for the cluster server.
     */
    @Column(nullable = false)
    private short port;
    /**
     * Is this cluster available to public clients.
     */
    private boolean secret;
    /**
     * Can clients connect to this cluster.
     */
    private boolean locked;
    /**
     * Is this cluster not recommended for new clients to connect.
     */
    private boolean notRecommended;
    /**
     * How many characters may be created on this cluster per account.
     */
    private int maxCharactersPerAccount;
    /**
     * The total number of players that may be connected to this cluster at one time.
     */
    private int onlinePlayerLimit;
    /**
     * The total number of free trial players that may be connected to this cluster at one time.
     */
    private int onlineFreeTrialLimit;
    /**
     * Can a free trial account create a character on this cluster.
     */
    private boolean freeTrialCanCreateChar;
    /**
     * The total number of players that can be in the tutorial at one time.
     */
    private int onlineTutorialLimit;

    public ClusterEntity() {}

    public ClusterEntity(final String name, final String address, final short port) {
        this.name = name;
        this.address = address;
        this.port = port;
    }

    /**
     * Maps the properties of a ClusterEntity to an existing ClusterListEntry.
     * @param entity The entity being mapped to the entry.
     * @param entry The entry that is being updated.
     */
    public static void map(@NonNull ClusterEntity entity, @NonNull final ClusterListEntry entry) {
        entry.setAddress(entity.address);
        entry.setPort(entity.port);
        entry.setSecret(entity.secret);
        entry.setLocked(entity.locked);
        entry.setNotRecommendedDatabase(entity.notRecommended);
        entry.setMaxCharactersPerAccount(entity.maxCharactersPerAccount);
        entry.setOnlinePlayerLimit(entity.onlinePlayerLimit);
        entry.setOnlineTutorialLimit(entity.onlineTutorialLimit);
        entry.setOnlineFreeTrialLimit(entity.onlineFreeTrialLimit);
        entry.setFreeTrialCanCreateChar(entity.freeTrialCanCreateChar);
    }
}
