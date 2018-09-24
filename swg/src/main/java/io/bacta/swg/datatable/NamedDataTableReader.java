package io.bacta.swg.datatable;

import java.io.IOException;
import java.nio.file.Path;

public interface NamedDataTableReader {
    NamedDataTable read(Path filePath) throws IOException;
}
