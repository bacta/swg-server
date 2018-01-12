package com.ocdsoft.bacta.swg.login.db;

import com.ocdsoft.bacta.swg.login.object.ClusterListEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by crush on 6/8/2017.
 */
public class BerkeleyDbClusterRepositoryTests {
    private Environment environment;
    private BerkeleyDbClusterRepository clusterRepository;

    @Before
    public void setup() {
        try {
            Path environmentPath = Paths.get(System.getProperty("user.dir"), "db", "login-server");
            File environmentFile = environmentPath.toFile();

            EnvironmentConfig environmentConfig = new EnvironmentConfig();
            environmentConfig.setAllowCreate(true);

            environment = new Environment(environmentFile, environmentConfig);
            clusterRepository = new BerkeleyDbClusterRepository(environment);

        } catch (DatabaseException ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    @After
    public void teardown() {
        try {
            if (clusterRepository != null)
                clusterRepository.close();

            if (environment != null)
                environment.close();
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void shouldCreateClusterEntity() {
        clusterRepository.create(new ClusterListEntry(1, "clustertest10"));
        clusterRepository.create(new ClusterListEntry(2, "clustertest11"));
        clusterRepository.create(new ClusterListEntry(3, "clustertest12"));
        clusterRepository.create(new ClusterListEntry(4, "clustertest13"));
        clusterRepository.create(new ClusterListEntry(5, "clustertest14"));

        ClusterListEntry cle1 = clusterRepository.get(1);
        ClusterListEntry cle2 = clusterRepository.get(2);
        ClusterListEntry cle3 = clusterRepository.get(3);
        ClusterListEntry cle4 = clusterRepository.get(4);
        ClusterListEntry cle5 = clusterRepository.get(5);

        cle1 = clusterRepository.get("clustertest10");
        cle2 = clusterRepository.get("clustertest11");
        cle3 = clusterRepository.get("clustertest12");
        cle4 = clusterRepository.get("clustertest13");
        cle5 = clusterRepository.get("clustertest14");

        List<ClusterListEntry> entries = clusterRepository.get();
    }
}