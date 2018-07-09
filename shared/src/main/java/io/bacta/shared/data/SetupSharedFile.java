package io.bacta.shared.data;

import io.bacta.engine.conf.BactaConfiguration;
import io.bacta.shared.tre.TreeFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by crush on 3/19/14.
 */
@Component
@Scope("prototype")
@Slf4j
public class SetupSharedFile {

    private final BactaConfiguration configuration;
    private final TreeFile treeFile;

    @Inject
    public SetupSharedFile(final BactaConfiguration configuration, final TreeFile treeFile) {
        this.configuration = configuration;
        this.treeFile = treeFile;

        install();
    }

    public void install() {
        try {
            final String rootPath = configuration.getPath().toString();
            treeFile.setRootPath(rootPath);

            int subscriptionFeatures = configuration.getIntWithDefault("Station", "subscriptionFeatures", 1);
            int maxSearchPriority = configuration.getIntWithDefault("SharedFile", "maxSearchPriority", 26);

            for (int feature = 0; feature <= subscriptionFeatures; feature++) {
                for (int priority = 0; priority < maxSearchPriority; priority++) {

                    try {

                        String propertyName = String.format("searchPath_%02d_%d", feature, priority);
                        Collection<String> filePaths = configuration.getStringCollection("SharedFile", propertyName);

                        if (filePaths != null) {
                            for (String filePath : filePaths) {
                                LOGGER.info("Loading Search path {}", filePath);
                                treeFile.addSearchPath(rootPath + File.separator + filePath, priority);
                            }
                        }

                        propertyName = String.format("searchTree_%02d_%d", feature, priority);
                        filePaths = configuration.getStringCollection("SharedFile", propertyName);

                        if (filePaths != null) {
                            for (String filePath : filePaths) {
                                LOGGER.info("Loading Search Tree {}", filePath);
                                treeFile.addSearchTree(rootPath + File.separator + filePath, priority);
                            }
                        }

                        propertyName = String.format("searchTOC_%02d_%d", feature, priority);
                        filePaths = configuration.getStringCollection("SharedFile", propertyName);

                        if (filePaths != null) {
                            for (String filePath : filePaths) {
                                LOGGER.info("Loading Search TOC {}", filePath);
                                treeFile.addSearchTOC(rootPath + File.separator + filePath, priority);
                            }
                        }
                    } catch (IOException e) {
                        LOGGER.error("Unable to open file", e);
                    }
                }

            }

            treeFile.addSearchAbsolute(configuration.getIntWithDefault("SharedFile", "searchAbsolute", maxSearchPriority + 1));
            treeFile.addSearchCache(configuration.getIntWithDefault("SharedFile", "searchCache", maxSearchPriority + 1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
