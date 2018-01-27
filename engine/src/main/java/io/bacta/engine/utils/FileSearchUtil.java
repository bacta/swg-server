package io.bacta.engine.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileSearchUtil {

    /**
     * Searches from current directory for a file matching the provided regex
     * and will only descend down the specified number of subfolders.  This will
     * search all the files and will throw and exception if there are multiple
     * matching files.
     * @return
     */
    public static Path findSoftwareRoot() throws IOException {

        Path workingPath = Paths.get(System.getProperty("user.dir"));
        return getSoftwareRoot(workingPath.getParent(), 3);
    }

    private static Path getSoftwareRoot(Path path, int depth) throws IOException {

        List<String> filesToFind = new ArrayList<>();
        filesToFind.add("galaxy.bat");
        filesToFind.add("login.bat");
        filesToFind.add("windows-kill.exe");
        filesToFind.add("lib");

        LOGGER.trace("Searching path: {}", path);

        List<Path> paths = new ArrayList<>();

        Files.walk(path, depth, FileVisitOption.FOLLOW_LINKS).forEach(currentPath ->  {

            if(paths.isEmpty()) {
                String fileName = currentPath.toFile().getName();
                LOGGER.trace("Checking file: {} {}", currentPath, fileName);
                if (filesToFind.contains(fileName)) {
                    filesToFind.remove(fileName);
                }

                if (filesToFind.isEmpty()) {
                    paths.add(currentPath.getParent());
                }
            }
        });

        if(!paths.isEmpty()) {
            return paths.get(0);
        }

        throw new IOException("Unable to find software root.  If you are debugging, you may need to package files before running this");
    }
}
