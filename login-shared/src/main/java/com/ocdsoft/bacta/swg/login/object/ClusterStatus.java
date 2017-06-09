package com.ocdsoft.bacta.swg.login.object;

import lombok.Data;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by kyle on 4/2/2017.
 */

@Data
public class ClusterStatus {
    private final Set<ClusterData> clusterServerEntrySet = new TreeSet<>();
    private int maxCharactersPerAccount;
}
