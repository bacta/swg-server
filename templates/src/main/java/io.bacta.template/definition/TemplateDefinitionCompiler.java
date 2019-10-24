package io.bacta.template.definition;

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
            final Path sourcePath = Paths.get(args[0]);
            final String sourcePackage = args[1];
            final Path sourceDestination = Paths.get(args[2]);
            Files.createDirectories(sourceDestination);


            final TemplateDefinitionCompiler compiler = new TemplateDefinitionCompiler();
            compiler.compile(sourcePath.toFile(), sourceDestination.toFile(), sourcePackage);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void compile(final File sourceDirectory, final File destinationDirectory, final String templatePackage) throws IOException {
        final TemplateDefinitionWriter writer = new TemplateDefinitionWriter(templatePackage);
        final String destinationPath = destinationDirectory.getAbsolutePath();

        Files.walk(Paths.get(sourceDirectory.getAbsolutePath()))
                .filter(p -> p.getFileName().toString().endsWith(".tdf"))
                .forEach(path -> {
                    try {
                        final File file = new File(path.toUri());

                        final TemplateDefinitionFile tdf = new TemplateDefinitionFile();
                        tdf.parse(file);

                        System.out.println("Generating file " + destinationPath + "\\" + tdf.getTemplateName() + ".java");
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