package io.bacta.swg.iff;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Compiles .mif files which stands for Make IFF file format. These are plain text representations of an IFF file.
 */
@Slf4j
public class MakeIffCompiler {
    private Path currentFilePath;

    private int braceDepth = 0;

    public void compile(final Path filePath) throws IOException {
        LOGGER.debug("Compiling MIFF file {}", filePath.toAbsolutePath());

        //Reset the compiler state.
        this.reset();

        this.currentFilePath = filePath;

        try (final BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filePath.toFile()))){

        }
    }

    private void reset() {
        braceDepth = 0;
    }
}
