package io.bacta.engine.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileSearchUtil {

    /**
     * Searches from current directory for a file matching the provided regex
     * and will only descend down the specified number of subfolders.  This will
     * search all the files and will throw and exception if there are multiple
     * matching files.
     * @param fileRegex Regex expression to match filename
     * @param depth number of folders levels to recurse into
     * @return
     */
    public static File getSingleMatch(String fileRegex, int depth) throws IOException {
        return getSingleMatch(Paths.get("."), fileRegex, depth);
    }

    public static File getSingleMatch(Path path, String fileRegex, int depth) throws IOException {
        List<Path> discoveredFiles = Files.walk(path, depth, FileVisitOption.FOLLOW_LINKS)
                .filter(thisPath -> thisPath.toFile().getName().matches(fileRegex))
                .collect(Collectors.toList());

        if(discoveredFiles.size() == 1) {
            return discoveredFiles.get(0).toFile();
        }

        if(discoveredFiles.isEmpty()) {
            throw new IOException("Unable to find matching file");
        }

        throw new IOException("Found multiple files: " + discoveredFiles.toString());
    }
}
