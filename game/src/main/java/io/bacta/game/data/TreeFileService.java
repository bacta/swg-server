package io.bacta.game.data;

import io.bacta.game.GameServerProperties;
import io.bacta.game.GameServerProperties.TreeFileProperties;
import io.bacta.game.GameServerProperties.TreeFileProperties.SearchNodeProperties;
import io.bacta.swg.tre.TreeFile;
import io.bacta.swg.tre.UnsupportedTreeFileException;
import io.bacta.swg.tre.UnsupportedTreeFileVersionException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * This service is responsible for configuring the TreeFile based on configuration properties as well as handling
 * metrics concerning the Tree File. Each zone server may have its own TreeFile service.
 */
@Slf4j
@Service
public final class TreeFileService {
    private final TreeFileProperties properties;

    @Getter
    private final TreeFile treeFile;

    public TreeFileService(GameServerProperties properties) {
        this.properties = properties.getTreeFile();

        //Is this the right way to get the application root path?
        final String rootPath = System.getProperty("user.dir");

        LOGGER.info("Setting root path for tree file to {}", rootPath);

        this.treeFile = new TreeFile();
        this.treeFile.setRootPath(rootPath);
    }

    @PostConstruct
    private void configureTreeFile()
            throws UnsupportedTreeFileVersionException, UnsupportedTreeFileException, IOException {

        final List<SearchNodeProperties> searchNodeProperties = properties.getSearch();

        if (searchNodeProperties == null || searchNodeProperties.size() == 0) {
            LOGGER.warn(
                    "No search nodes have been configured in the application properties file." +
                    "Absolute search from root directory will be the only means of resolving files.");
        } else {

            LOGGER.debug("Configuring tree file with {} search nodes.", searchNodeProperties.size());

            //Sort the nodes by priority in descending order.
            searchNodeProperties.sort(Comparator.comparingInt(SearchNodeProperties::getPriority).reversed());

            for (final SearchNodeProperties searchNodeProperty : searchNodeProperties) {
                final String type = searchNodeProperty.getType().toLowerCase();
                final String path = searchNodeProperty.getPath();
                final int priority = searchNodeProperty.getPriority();

                switch (type) {
                    case "toc":
                        LOGGER.trace("Adding TOC search node with priority {} and path {}", priority, path);
                        treeFile.addSearchTOC(path, priority);
                        break;
                    case "tree":
                        LOGGER.trace("Adding Tree search node with priority {} and path {}", priority, path);
                        treeFile.addSearchTree(path, priority);
                        break;
                    case "path":
                        LOGGER.trace("Adding Path search node with priority {} and path {}", priority, path);
                        treeFile.addSearchPath(path, priority);
                        break;
                    case "absolute":
                        LOGGER.trace("Adding Absolute search node with priority {}", priority);
                        treeFile.addSearchAbsolute(priority);
                        break;
                    case "cache":
                        LOGGER.trace("Adding Cache search node with priority {}", priority);
                        treeFile.addSearchCache(priority);
                        break;
                    default:
                        throw new UnsupportedOperationException(
                                String.format("Unknown search node type %s specified in tree file properties.", type));
                }
            }

            LOGGER.debug("Tree file successfully configured.");
        }
    }
}
