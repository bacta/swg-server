package io.bacta.shared.tre.template.definition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by crush on 4/18/2016.
 */
public class TemplateDefinitionCompiler {

    public static void main(final String[] args) {
        try {

            System.out.println("Generating Template Definitions");
            //Shared
            final Path sharedSourcePath = Paths.get(args[0]);
            final String sharedTemplatePackage = args[1];
            final Path sharedTemplateDestination = Paths.get(args[2]);
            Files.createDirectories(sharedTemplateDestination);

            final Path serverSourcePath = Paths.get(args[3]);
            final String serverTemplatePackage = args[4];
            final Path serverTemplateDestination = Paths.get(args[5]);
            Files.createDirectories(serverTemplateDestination);

            final TemplateDefinitionCompiler compiler = new TemplateDefinitionCompiler();
            compiler.compile(sharedSourcePath.toFile(), sharedTemplateDestination.toFile(), sharedTemplatePackage);
            compiler.compile(serverSourcePath.toFile(), serverTemplateDestination.toFile(), serverTemplatePackage);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void compile(final File sourceDirectory, final File destinationDirectory, final String templatePackage) throws IOException {
        final File[] files = sourceDirectory.listFiles((dir, name) -> {
            return name.endsWith(".tdf");
        });

        final TemplateDefinitionWriter writer = new TemplateDefinitionWriter(templatePackage);
        final String destinationPath = destinationDirectory.getAbsolutePath();

        Files.walk(Paths.get(sourceDirectory.getAbsolutePath()))
                .filter(p -> p.getFileName().toString().endsWith(".tdf"))
                .forEach(path -> {
                    try {
                        final File file = new File(path.toUri());

                        final TemplateDefinitionFile tdf = new TemplateDefinitionFile();
                        tdf.parse(file);

                        System.out.println("Generating file " + destinationPath + tdf.getTemplateName() + ".java");
                        final File outputFile = new File(destinationPath, tdf.getTemplateName() + ".java");

                        try (final OutputStream outputStream = new FileOutputStream(outputFile)) {
                            writer.write(outputStream, tdf, tdf.getHighestVersion());
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
    }
}