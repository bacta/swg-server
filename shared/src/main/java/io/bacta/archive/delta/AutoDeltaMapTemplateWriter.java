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
public class AutoDeltaMapTemplateWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoDeltaMapTemplateWriter.class);
    private static final String archivePath = "pre-cu/src/main/java/com/ocdsoft/bacta/swg/precu/object/archive/delta/map";
    private static final String resourcesPath = new File(AutoDeltaMapTemplateWriter.class.getResource("/").getFile()).getAbsolutePath();

    private final VelocityEngine velocityEngine;
    private final Template template;

    public AutoDeltaMapTemplateWriter() {
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, LOGGER);
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
        velocityEngine.init();

        template = velocityEngine.getTemplate("/template/AutoDeltaMap.vm");
    }

    public void writeTemplates() {
        try {
            Files.readAllLines(Paths.get(resourcesPath, "AutoDeltaMap.txt"))
                    .stream()
                    .skip(1) //Skip the header
                    .filter(line -> !line.isEmpty()) //Get rid of any empty lines.
                    .map(this::createContextFromLine) //Convert each line into a context.
                    .forEach(this::writeTemplate); //Write the template for the context.

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void writeTemplate(final AutoDeltaMapContext context) {
        try {
            final VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("nullable", Character.isUpperCase(context.valueType.charAt(0)) ? "" : null);
            velocityContext.put("trove", context.mapType.charAt(0) == 'T');
            velocityContext.put("mapType", context.mapType);
            velocityContext.put("mapImpl", context.mapImpl);
            velocityContext.put("iteratorType", context.iteratorType);
            velocityContext.put("iteratorAccessor", context.iteratorAccessor);
            velocityContext.put("iteratorGeneric", context.mapType.charAt(0) == 'T' ? context.generic : String.format("<Map.Entry%s>", context.generic));
            velocityContext.put("generic", context.generic.isEmpty() ? null : context.generic);
            velocityContext.put("genericClass", context.genericClass);
            velocityContext.put("genericCommand", context.genericCommand.isEmpty() ? null : context.genericCommand);
            velocityContext.put("keyName", context.keyName);
            velocityContext.put("keyType", context.keyType);
            velocityContext.put("keySerializer", context.keySerializer);
            velocityContext.put("keyDeserializer", context.keyDeserializer);
            velocityContext.put("keyCreator", context.keyCreator.isEmpty() ? null : context.keyCreator);
            velocityContext.put("keyToBoolean", "boolean".equals(context.keyType) ? " != 0" : "");
            velocityContext.put("keyFromBoolean", "boolean".equals(context.keyType) ? " ? (byte)1 : (byte)0" : "");
            velocityContext.put("valueName", context.valueName);
            velocityContext.put("valueType", context.valueType);
            velocityContext.put("valueSerializer", context.valueSerializer);
            velocityContext.put("valueDeserializer", context.valueDeserializer);
            velocityContext.put("valueCreator", context.valueCreator.isEmpty() ? null : context.valueCreator);
            velocityContext.put("valueToBoolean", "boolean".equals(context.valueType) ? " != 0" : "");
            velocityContext.put("valueFromBoolean", "boolean".equals(context.valueType) ? " ? (byte)1 : (byte)0" : "");
            velocityContext.put("containerForEachStart", context.containerForEachStart);
            velocityContext.put("containerForEachEnd", context.containerForEachEnd);

            final String fileName = String.format("AutoDelta%s%sMap.java", context.keyName, context.valueName);
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

    private AutoDeltaMapContext createContextFromLine(final String line) {
        final String[] fields = line
                .substring(1, line.length() - 1) //Remove the first and last quote.
                .split("\",\""); //Split on each sequence of ","

        return new AutoDeltaMapContext(
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
                fields[11],
                fields[12],
                fields[13],
                fields[14],
                fields[15],
                fields[16],
                fields[17],
                fields[18]);
    }

    @AllArgsConstructor
    private static class AutoDeltaMapContext {
        public final String mapType;
        public final String mapImpl;
        public final String iteratorType;
        public final String iteratorAccessor;
        public final String generic;
        public final String genericClass;
        public final String genericCommand;
        public final String keyName;
        public final String keyType;
        public final String keySerializer;
        public final String keyDeserializer;
        public final String keyCreator;
        public final String valueName;
        public final String valueType;
        public final String valueSerializer;
        public final String valueDeserializer;
        public final String valueCreator;
        public final String containerForEachStart;
        public final String containerForEachEnd;
    }

    public static void main(final String[] args) {
        final AutoDeltaMapTemplateWriter writer = new AutoDeltaMapTemplateWriter();
        writer.writeTemplates();
    }
}
