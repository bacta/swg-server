package io.bacta.swg.tre;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

/**
 * Created by crush on 3/19/14.
 * <p>
 * A Tree Archive (*.tre) is an archive format seen in Star Wars Galaxies, and potentially other games. It is unknown
 * if this is an SOE proprietary format, or a common format with little, to no documentation available to the public
 * domain.
 */
@Slf4j
@SuppressWarnings("deprecation")
public class TreeFile {
    public static final int ID_TREE = 0x54524545; //'TREE'
    public static final int ID_0005 = 0x30303035; //'0005'
    public static final int ID_0006 = 0x30303036; //'0006'

    private final Collection<SearchNode> nodes = new ArrayList<>();

    private String rootPath;

    public final String getRootPath() {
        return rootPath;
    }

    public final void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @SuppressWarnings("unchecked")
    public void addSearchPath(final String filePath, int priority) {
        nodes.add(new SearchPath(priority, filePath));
        Collections.sort((ArrayList) nodes);
    }

    @SuppressWarnings("unchecked")
    public void addSearchTree(String filePath, int priority) throws
            IOException,
            UnsupportedTreeFileException,
            UnsupportedTreeFileVersionException {

        SearchTree searchTree = new SearchTree(filePath, priority);
        searchTree.preprocess();
        nodes.add(searchTree);
        Collections.sort((ArrayList) nodes);
    }

    @SuppressWarnings("unchecked")
    public void addSearchTOC(String filePath, int priority) {
        try {
            nodes.add(new SearchTOC(filePath, priority));
            Collections.sort((ArrayList) nodes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void addSearchAbsolute(int priority) {
        nodes.add(new SearchAbsolute(this.getRootPath(), priority));
        Collections.sort((ArrayList) nodes);
    }

    @SuppressWarnings("unchecked")
    public void addSearchCache(int priority) {
        nodes.add(new SearchCache(priority));
        Collections.sort((ArrayList) nodes);
    }

    public boolean exists(final String filePath) {
        for (SearchNode node : nodes) {
            if (node.exists(filePath))
                return true;
        }

        return false;
    }

    /**
     * Attempts to open the file at the desired path if it exists.
     * <code>
     * TreeFile treeFile = ...;
     * byte[] bytes = treeFile.open("/object/base/shared_base_object.iff");
     * </code>
     *
     * @param filePath The path of the file that should be opened.
     * @return Returns a byte array representing the file, or null if the file didn't exist.
     */
    public byte[] open(final String filePath) {

        byte[] bytes = null;

        for (SearchNode searchNode : nodes) {
            bytes = searchNode.open(filePath);

            if (bytes != null)
                break;
        }

        return bytes;
    }

    /**
     * Gets a listing of all the files in the specified directory. This function will recurse through subdirectories.
     *
     * @param directory The directory to begin searching for files.
     * @return A set of all the paths in the directory.
     */
    public Set<String> listFiles(final String directory) {
        final Set<String> set = new TreeSet<>();

        for (final SearchNode node : nodes) {
            if (node instanceof SearchTree) {
                final SearchTree searchTree = (SearchTree) node;
                final Set<String> files = searchTree.listFiles();

                for (String file : files) {
                    if (file.startsWith(directory)) {
                        set.add(file);
                    }
                }
            }
        }

        return set;
    }
}