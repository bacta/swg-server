package io.bacta.swg.tre;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by crush on 3/20/14.
 */
final class SearchAbsolute extends SearchNode {

    private final String filePath;

    public SearchAbsolute(String filePath, int searchPriority) {
        super(searchPriority);

        this.filePath = filePath.replace("\\", "/");
    }

    @Override
    public boolean exists(final String filePath) {
        final Path path = Paths.get(this.filePath).resolve(filePath);
        return path.toFile().isFile();
    }

    @Override
    public byte[] open(String filePath) {
        byte[] bytes = null;

        try {
            final Path path = Paths.get(this.filePath, filePath);
            final RandomAccessFile file = new RandomAccessFile(path.toString(), "r");

            bytes = new byte[(int) file.length()];
            file.readFully(bytes);

            file.close();
        } catch (FileNotFoundException e) {
            logger.trace("Could not open file[" + filePath + "] because it does not exist.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bytes;
    }
}