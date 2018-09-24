package io.bacta.tre;

import io.bacta.swg.tre.TreeFile;
import io.bacta.swg.tre.UnsupportedTreeFileException;
import io.bacta.swg.tre.UnsupportedTreeFileVersionException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

/**
 * Created by crush on 12/16/2014.
 */
public class TreeFileTest {
    private static final String resourcesPath = new File(Paths.get("target", "test-classes").toUri()).getPath();


    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldExistInTreeFile() throws Exception {

        final TreeFile treeFile = new TreeFile();
        treeFile.addSearchTree(resourcesPath + "/test.tre", 0);
        Assert.assertNotNull(treeFile.open("does/exist.iff"));
    }

    @Test
    public void shouldNotExistInTreeFile() throws Exception {
        final TreeFile treeFile = new TreeFile();
        treeFile.addSearchTree(resourcesPath + "/test.tre", 0);
        Assert.assertNull(treeFile.open("does/not/exist.iff"));
    }

    @Test
    public void shouldThrowUnsupportedTreeFileException() throws Exception {
        final TreeFile treeFile = new TreeFile();
        exception.expect(UnsupportedTreeFileException.class);
        treeFile.addSearchTree(resourcesPath + "/wrong-type.tre", 0);
        treeFile.open("does/not/exist.iff"); //Have to actually try and open a file in the tre archive.
    }

    @Test
    public void shouldThrowUnsupportedTreeFileVersionException() throws Exception {
        TreeFile treeFile = new TreeFile();
        exception.expect(UnsupportedTreeFileVersionException.class);
        treeFile.addSearchTree(resourcesPath + "/wrong-version.tre", 0);
        treeFile.open("does/not/exist.iff");
    }

    @Test
    public void shouldThrowFileNotFoundException() throws Exception {
        final TreeFile treeFile = new TreeFile();
        exception.expect(FileNotFoundException.class);
        treeFile.addSearchTree(resourcesPath + "/non-existent.tre", 0);
    }
}
