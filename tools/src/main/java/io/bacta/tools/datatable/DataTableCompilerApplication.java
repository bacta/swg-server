package io.bacta.tools.datatable;

import io.bacta.swg.datatable.DataTableManager;
import io.bacta.swg.datatable.DataTableWriter;
import io.bacta.swg.datatable.NamedDataTable;
import io.bacta.swg.datatable.TabNamedDataTableReader;
import io.bacta.swg.tre.TreeFile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Can compile a directory of SWG DataTable source files recursively, and output the files as IFF in a target directory.
 */
@Slf4j
public final class DataTableCompilerApplication {
    public static void main(final String[] args) throws IOException {
        final Path sourceDirectory = Paths.get(args[0]);
        final Path destinationDirectory = Paths.get(args[1]);

        LOGGER.info("Compiling DataTables in source directory {}.", sourceDirectory.toAbsolutePath());

        //If the destination doesn't exist, create it if possible.
        Files.createDirectories(destinationDirectory);

        final TreeFile treeFile = new TreeFile();
        //Now we need shared loader to load this.

        final DataTableManager dataTableManager = new DataTableManager(treeFile);
        final TabNamedDataTableReader tabNamedDataTableReader = new TabNamedDataTableReader(dataTableManager);

        final List<ExceptionDetail> exceptions = new ArrayList<>();
        final AtomicInteger successfullyWritten = new AtomicInteger();

        Files.walk(sourceDirectory)
                .filter(path -> path.getFileName().toString().endsWith(".tab"))
                .forEach(path -> {
                    try {
                        //LOGGER.info("Writing DataTable to IFF from spreadsheet file {}.", path.toAbsolutePath());
                        final NamedDataTable namedDataTable = tabNamedDataTableReader.read(path);

                        //We want to preserve the part of the directory that is in our source directory.
                        final Path relativePath = sourceDirectory.relativize(path.getParent());
                        final Path outputDirectory = destinationDirectory.resolve(relativePath);

                        Files.createDirectories(outputDirectory);

                        final Path outputPath = outputDirectory.resolve(namedDataTable.getName() + ".iff");

                        final DataTableWriter dataTableWriter = new DataTableWriter();
                        dataTableWriter.write(namedDataTable, outputPath);

                        successfullyWritten.incrementAndGet();

                    } catch (Exception e) {
                        final String stackTrace = ExceptionUtils.getStackTrace(e);
                        exceptions.add(new ExceptionDetail(e.getMessage(), stackTrace));
                    }
                });

        LOGGER.info("Successfully compiled {} datatables. Recorded {} exceptions.",
                successfullyWritten.get(),
                exceptions.size());
    }

    @Getter
    @RequiredArgsConstructor
    private static class ExceptionDetail {
        private final String message;
        private final String stackTrace;
    }
}
