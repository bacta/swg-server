package io.bacta.swg.tre;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by crush on 3/19/14.
 */
final class SearchPath extends SearchNode {
    private final Path pathName;

    public final String getPathName() {
        return pathName.toString();
    }

    public SearchPath(int searchPriority, final String filePath) {
        super(searchPriority);

        this.pathName = Paths.get(filePath).toAbsolutePath();
    }

    @Override
    public byte[] open(final String filePath) {
        try {
            final Path path = pathName.resolve(filePath);
            return Files.readAllBytes(path);
        } catch (IOException ex) {
            //ex.printStackTrace();
            //We don't care about the stack trace here. If there is an exception, then we reject this node.

            return null;
        }
    }

    @Override
    public boolean exists(final String filePath) {
        final Path path = pathName.resolve(filePath);
        return Files.exists(path);
    }
}