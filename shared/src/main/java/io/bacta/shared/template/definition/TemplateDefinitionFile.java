package io.bacta.shared.template.definition;

import com.google.common.io.Files;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.bacta.engine.utils.StringUtil;
import io.bacta.shared.foundation.Tag;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by crush on 4/18/2016.
 */
public class TemplateDefinitionFile {
    @Getter
    private TagInfo templateId;
    @Getter
    private String baseName;
    @Getter
    private String baseFilename;
    @Getter
    private String templateName;
    @Getter
    private String templateFilename;
    private String templateNameFilter;
    @Getter
    private String path;
    @Getter
    private String compilerPath;
    @Getter
    private TemplateDefinitionFile baseDefinitionFile;

    @Getter
    private boolean writeForCompilerFlag;

    @Getter
    private int highestVersion = 0;

    private final TIntObjectMap<TemplateData> templateMap = new TIntObjectHashMap<>();
    @Getter
    private final List<String> fileComments = new ArrayList<>();

    private TemplateData currentTemplate;
    @Getter
    private TemplateLocation templateLocation;

    public TemplateDefinitionFile() {
        cleanup();
    }

    public void setTemplateFilename(final String name) {
        this.templateFilename = name;
        this.templateName = templateLocation.getName() +
                StringUtil.convertUnderscoreToUpper(name);
    }

    public void setBaseFilename(final String name) {
        this.baseFilename = name;
        this.baseName = templateLocation.getName() +
                StringUtil.convertUnderscoreToUpper(name);
    }

    public void parse(final File templateFile) throws IOException {

        setTemplateFilename(Files.getNameWithoutExtension(templateFile.getName()));

        final List<String> lines = Files.readLines(templateFile, Charset.defaultCharset());

        for (final String line : lines) {
            final TemplateLineBuffer buffer = new TemplateLineBuffer(line);

            final String token = buffer.getNextWhitespaceToken();

            if (token == null)
                continue;

            if ("version".equalsIgnoreCase(token)) {
                parseVersion(buffer);
            } else if (currentTemplate != null) {
                currentTemplate.parseLine(new TemplateLineBuffer(line));
            } else if (token.charAt(0) == '/' && token.charAt(1) == '/') {
                //Apparently, we only keep file-level comments if they are before anything has been set.
                if (baseName.isEmpty() && templateId == TagInfo.None)
                    fileComments.add(buffer.getRemainingString());
            } else if ("base".equalsIgnoreCase(token)) {
                parseBase(templateFile, buffer);
            } else if ("id".equalsIgnoreCase(token)) {
                parseId(buffer);
            } else if ("templatename".equalsIgnoreCase(token)) {
                parseTemplateName(buffer);
            } else if ("clientpath".equalsIgnoreCase(token)
                    || "serverpath".equalsIgnoreCase(token)
                    || "sharedpath".equalsIgnoreCase(token)) {
                parsePath(buffer, token);
            } else if ("compilerpath".equalsIgnoreCase(token)) {
                parseCompilerPath(buffer);
            } else {
                throw new IllegalStateException(String.format("unable to handle line with token %s", token));
            }
        }
    }

    private void parseCompilerPath(final TemplateLineBuffer buffer) {
        if (!compilerPath.isEmpty())
            throw new IllegalStateException("compiler path is already defined");

        compilerPath = buffer.getNextWhitespaceToken();
    }

    private void parsePath(final TemplateLineBuffer buffer, final String pathType) {
        if (!path.isEmpty())
            throw new IllegalStateException("path is already defined");

        if ("clientpath".equalsIgnoreCase(pathType)) {
            templateLocation = TemplateLocation.CLIENT;
        } else if ("serverpath".equalsIgnoreCase(pathType)) {
            templateLocation = TemplateLocation.SERVER;
        } else {
            templateLocation = TemplateLocation.SHARED;
        }

        final String token = buffer.getNextWhitespaceToken();

        path = token;

        //Reset the template name to add the correct prefix to the template name.
        setTemplateFilename(templateFilename);

        if (!baseFilename.isEmpty())
            setBaseFilename(baseFilename);
    }

    private void parseTemplateName(final TemplateLineBuffer buffer) {
        final String token = buffer.getNextWhitespaceToken();
        templateNameFilter = token;

        //compile some kind of regex that will be used for something...
    }

    private void parseId(final TemplateLineBuffer buffer) {
        if (templateId != TagInfo.None)
            throw new IllegalStateException("template id already defined");

        final String tag = buffer.getNextWhitespaceToken();

        if (tag.length() != 4)
            throw new IllegalStateException("id not 4 characters");

        this.templateId = new TagInfo(
                Tag.convertStringToTag(tag),
                TagInfo.convertStringToTagString(tag));
    }

    private void cleanup() {
        highestVersion = 0;
        templateId = TagInfo.None;
        templateLocation = TemplateLocation.NONE;
        templateName = "";
        templateFilename = "";
        baseName = "";
        baseFilename = "";
        templateNameFilter = "";
        path = "";
        compilerPath = "";
        fileComments.clear();
        baseDefinitionFile = null;
        templateMap.clear();
        //filterCompiledRegex = null;
    }

    private void parseBase(final File templateFile, final TemplateLineBuffer buffer) throws IOException {
        if (!baseName.isEmpty())
            throw new IllegalStateException("base is already defined");

        final String token = buffer.getNextWhitespaceToken();
        setBaseFilename(token);

        final String extension = '.' + Files.getFileExtension(templateFile.getName());

        final File baseFile = new File(templateFile.getParent(), token + extension);

        if (!baseFile.exists() || !baseFile.canRead()) {
            throw new IllegalStateException(
                    String.format("unable to open base template definition: %s.", token));
        }

        if (baseDefinitionFile == null)
            baseDefinitionFile = new TemplateDefinitionFile();
        else
            baseDefinitionFile.cleanup();

        baseDefinitionFile.parse(baseFile);
    }

    private void parseVersion(final TemplateLineBuffer buffer) {
        if (templateName.isEmpty())
            throw new IllegalStateException("no template name defined");

        if (path.isEmpty())
            throw new IllegalStateException("no path defined");

        if (compilerPath.isEmpty())
            throw new IllegalStateException("no compiler path defined");

        final String token = buffer.getNextWhitespaceToken();
        final int version = Integer.parseInt(token);

        if (version < 0 || version > 9999)
            throw new IllegalStateException("version out of range");

        if (version > highestVersion)
            highestVersion = version;

        currentTemplate = new TemplateData(version, this);
        templateMap.put(version, currentTemplate);
    }

    public TemplateData getTemplateData(int version) {
        return templateMap.get(version);
    }
}
