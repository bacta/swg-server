package io.bacta.archive.delta;

import lombok.AllArgsConstructor;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by crush on 5/7/2016.
 */
public class AutoDeltaSetTemplateWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoDeltaSetTemplateWriter.class);
    private static final String archivePath = "pre-cu/src/main/java/com/ocdsoft/bacta/swg/precu/object/archive/delta/set";
    private static final String resourcesPath = new File(AutoDeltaSetTemplateWriter.class.getResource("/").getFile()).getAbsolutePath();

    private final VelocityEngine velocityEngine;
    private final Template template;

    public AutoDeltaSetTemplateWriter() {
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, LOGGER);
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
        velocityEngine.init();

        template = velocityEngine.getTemplate("/template/AutoDeltaSet.vm");
    }

    public void writeTemplates() {
        try {
            Files.readAllLines(Paths.get(resourcesPath, "AutoDeltaSet.txt"))
                    .stream()
                    .skip(1) //Skip the header
                    .filter(line -> !line.isEmpty()) //Get rid of any empty lines.
                    .map(this::createContextFromLine) //Convert each line into a context.
                    .forEach(this::writeTemplate); //Write the template for the context.

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void writeTemplate(final AutoDeltaSetContext context) {
        try {
            final VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("nullable", Character.isUpperCase(context.valueType.charAt(0)) ? "" : null);
            velocityContext.put("trove", context.setType.charAt(0) == 'T');
            velocityContext.put("setType", context.setType);
            velocityContext.put("setImpl", context.setImpl);
            velocityContext.put("generic", context.generic);
            velocityContext.put("genericClass", context.genericClass);
            velocityContext.put("valueName", context.valueName);
            velocityContext.put("valueType", context.valueType);
            velocityContext.put("valueDefault", context.valueDefault);
            velocityContext.put("valueSerializer", context.valueSerializer);
            velocityContext.put("valueDeserializer", context.valueDeserializer);
            velocityContext.put("valueCreator", context.valueCreator.isEmpty() ? null : context.valueCreator);
            velocityContext.put("valueToBoolean", "boolean".equals(context.valueType) ? " != 0" : "");
            velocityContext.put("valueFromBoolean", "boolean".equals(context.valueType) ? " ? (byte)1 : (byte)0" : "");
            velocityContext.put("containerForEachStart", context.containerForEachStart);
            velocityContext.put("containerForEachEnd", context.containerForEachEnd);

            final String fileName = String.format("AutoDelta%sSet.java", context.valueName);
            final Path filePath = Paths.get(System.getProperty("user.dir"), archivePath, fileName);

            final BufferedWriter writer = Files.newBufferedWriter(filePath);

            if (!velocityEngine.evaluate(velocityContext, writer, template.getName(), ""))
                throw new Exception("Failed to convert the template into class.");

            template.merge(velocityContext, writer);

            writer.flush();
            writer.close();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private void erase(final int val) {

    }

    private AutoDeltaSetContext createContextFromLine(final String line) {
        final String[] fields = line
                .substring(1, line.length() - 1) //Remove the first and last quote.
                .split("\",\""); //Split on each sequence of ","

        return new AutoDeltaSetContext(
                fields[0],
                fields[1],
                fields[2],
                fields[3],
                fields[4],
                fields[5],
                fields[6],
                fields[7],
                fields[8],
                fields[9],
                fields[10],
                fields[11]);
    }

    @AllArgsConstructor
    private static class AutoDeltaSetContext {
        public final String setType;
        public final String setImpl;
        public final String generic;
        public final String genericClass;
        public final String valueName;
        public final String valueType;
        public final String valueDefault;
        public final String valueSerializer;
        public final String valueDeserializer;
        public final String valueCreator;
        public final String containerForEachStart;
        public final String containerForEachEnd;
    }

    public static void main(final String[] args) {
        final AutoDeltaSetTemplateWriter writer = new AutoDeltaSetTemplateWriter();
        writer.writeTemplates();
    }
}
