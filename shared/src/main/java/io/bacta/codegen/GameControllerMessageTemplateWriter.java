package io.bacta.codegen;

import com.google.common.base.CaseFormat;
import io.bacta.archive.delta.AutoDeltaMapTemplateWriter;
import lombok.Getter;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by crush on 5/29/2016.
 * <p>
 * Run this class's main method to generate the GameControllerMessage enum from the file found in resources.
 */
public class GameControllerMessageTemplateWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoDeltaMapTemplateWriter.class);
    private static final String templatePath = "/template/GameControllerMessageType.vm";
    private static final String inputFileName = "GameControllerMessageType.txt";
    private static final String outputFileName = "GameControllerMessageType.java";
    private static final String outputPath = "pre-cu/src/main/java/com/ocdsoft/bacta/swg/server/game/message/object/";
    private static final String resourcesPath = new File(GameControllerMessageTemplateWriter.class.getResource("/").getFile()).getAbsolutePath();

    private static final String hexString = "0123456789ABCDEF";

    private final VelocityEngine velocityEngine;
    private final Template template;

    public GameControllerMessageTemplateWriter() {
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, LOGGER);
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
        velocityEngine.init();

        template = velocityEngine.getTemplate(templatePath);
    }

    public void writeTemplate() {
        try {
            final List<EnumContext> enumContexts = Files.readAllLines(Paths.get(resourcesPath, inputFileName))
                    .stream()
                    .map(this::formatLine)
                    .filter(context -> context != null)
                    .collect(Collectors.toList());

            final Path filePath = Paths.get(System.getProperty("user.dir"), outputPath, outputFileName);

            final VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("enumContexts", enumContexts);
            velocityContext.put("lastValue", currentValue);

            final BufferedWriter writer = Files.newBufferedWriter(filePath);

            if (!velocityEngine.evaluate(velocityContext, writer, template.getName(), ""))
                throw new Exception("Failed to convert the template into class.");

            template.merge(velocityContext, writer);

            writer.flush();
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Starting value for enum.
    private static int currentValue = 0;

    //This is nasty, but it works...
    private EnumContext formatLine(final String line) {
        final EnumContext context = new EnumContext();

        final StringBuilder sb = new StringBuilder(line.length());

        char lastChar = '\0';
        boolean parsingValue = false;
        for (int i = 0, size = line.length(); i < size; ++i) {
            final char c = line.charAt(i);

            //skip whitespace as if it doesn't exist.
            if (c == ' ')
                continue;

            //If we run into any comments, then break.
            if (c == '/' && lastChar == '/')
                break;

            if (c == '=') {
                parsingValue = true;
                context.name = sb.toString();
                sb.setLength(0);
                continue;
            }

            if (!parsingValue) {
                if (Character.isLetter(c) || Character.isDigit(c))
                    sb.append(c);
            } else {
                if (hexString.indexOf(c) != -1 || c == 'x')
                    sb.append(c);
            }

            lastChar = c;
        }

        //If we were parsing a value when we left the loop, then convert that value into an integer value.
        if (parsingValue) {
            final String val = sb.toString();

            if (val.indexOf('x') == 1) {
                context.value = Integer.parseInt(val.substring(2), 16);
            } else {
                context.value = Integer.parseInt(val, 10);
            }
        } else {
            context.name = sb.toString();
            //No value was specified.
            context.value = currentValue;
        }

        if (context.name == null || context.name.isEmpty())
            return null;

        currentValue = context.value + 1;
        context.name = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, context.name);

        return context;
    }

    @Getter
    public static final class EnumContext {
        public String name;
        public int value;
    }

    public static void main(final String[] args) {
        final GameControllerMessageTemplateWriter writer = new GameControllerMessageTemplateWriter();
        writer.writeTemplate();
    }
}
