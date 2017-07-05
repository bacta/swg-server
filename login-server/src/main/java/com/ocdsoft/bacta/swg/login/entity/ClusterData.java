package com.ocdsoft.bacta.swg.login.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by crush on 7/2/2017.
 */
@Entity
@Table(name = "clusters")
@Getter
@Setter
public final class ClusterData {
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

    public ClusterData() {}
    public ClusterData(final String name, final String address, final short port) {
        this.name = name;
        this.address = address;
        this.port = port;
    }
}
