package io.bacta.tools.template;

import io.bacta.swg.template.definition.TemplateDefinitionCompiler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public final class TemplateDefinitionCompilerApplication {
    public static void main(final String[] args) {
        try {
            LOGGER.info("Generating template definitions.");

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
}
