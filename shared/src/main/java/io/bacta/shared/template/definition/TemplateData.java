package io.bacta.shared.template.definition;

import com.google.common.base.Preconditions;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by crush on 4/18/2016.
 */
public class TemplateData {
    final TemplateDefinitionFile fileParent;
    final TemplateData templateParent;

    @Getter
    private TagInfo version;
    @Getter
    private TagInfo structId;

    final List<Parameter> parameters = new ArrayList<>();
    final Map<String, Parameter> parameterMap = new HashMap<>();
    final List<TemplateData> structList = new ArrayList<>();
    final Map<String, TemplateData> structMap = new HashMap<>();
    final Map<String, List<EnumData>> enumMap = new HashMap<>();

    private List<EnumData> currentEnumList;
    private TemplateData currentStruct;
    private ParseState parseState;
    private int bracketCount;
    private String name;
    private String baseName;

    private boolean hasList;
    private boolean hasDynamicVarParam;
    private boolean hasTemplateParam;

    public TemplateData(final int version, final TemplateDefinitionFile fileParent) {
        this.fileParent = fileParent;
        this.templateParent = null;
        this.baseName = fileParent.getBaseName();
        this.version = new TagInfo(version);
        this.structId = TagInfo.None;
        this.parseState = ParseState.PARAM;
    }

    public TemplateData(final TemplateData templateParent, final String name) {
        this.fileParent = null;
        this.templateParent = templateParent;
        this.version = templateParent.version;
        this.baseName = "";
        this.name = name;
        this.structId = TagInfo.None;
        this.parseState = ParseState.PARAM;
    }

    public ParseStatus parseLine(final TemplateLineBuffer buffer) {
        ParamState paramState = ParamState.LIST;

        if (buffer == null || buffer.isAtEnd())
            return ParseStatus.ERROR;

        if (parseState == ParseState.ENUM)
            return parseEnum(buffer);
        else if (parseState == ParseState.STRUCT)
            return parseStruct(buffer);

        //Parsing a parameter.
        final Parameter parameter = new Parameter();
        parameter.type = ParamType.NONE;
        parameter.listType = ListType.NONE;
        parameter.listSize = 1;
        parameter.minIntLimit = Integer.MIN_VALUE;
        parameter.maxIntLimit = Integer.MAX_VALUE;
        parameter.minFloatLimit = Float.MIN_VALUE;
        parameter.maxFloatLimit = Float.MAX_VALUE;

        if (buffer.charAt(0) == '/' && buffer.charAt(1) == '/') {
            parameter.type = ParamType.COMMENT;
            paramState = ParamState.DESCRIPTION;
        }

        while (paramState != ParamState.DESCRIPTION) {
            final String token = buffer.getNextWhitespaceToken();

            if (paramState == ParamState.LIST) {
                if ("enum".equalsIgnoreCase(token)) {
                    if (parseState == ParseState.PARAM) {
                        final String enumName = buffer.getNextWhitespaceToken();
                        Preconditions.checkNotNull(enumName, "no enum name");
                        Preconditions.checkArgument(Character.isAlphabetic(enumName.charAt(0)), "bad enum name");
                        final List<EnumData> enumList = getEnumList(enumName, true);
                        Preconditions.checkArgument(enumList == null, "enum already defined");

                        currentEnumList = new ArrayList<>();
                        enumMap.put(enumName, currentEnumList);
                        parseState = ParseState.ENUM;

                        return parseEnum(buffer);
                    }

                    Preconditions.checkState(false, "Unexpected keyword 'enum'");
                    return ParseStatus.ERROR;
                } else if ("struct".equalsIgnoreCase(token)) {
                    if (parseState == ParseState.PARAM) {
                        final String structName = buffer.getNextWhitespaceToken();
                        Preconditions.checkNotNull(structName, "no struct name");
                        Preconditions.checkArgument(Character.isAlphabetic(structName.charAt(0)), "bad struct name");
                        final TemplateData existingStruct = structMap.get(structName);
                        Preconditions.checkArgument(existingStruct == null, "struct already defined");
                        currentStruct = new TemplateData(this, structName);
                        currentStruct.name = structName;

                        structMap.put(structName, currentStruct);
                        structList.add(currentStruct);
                        parseState = ParseState.STRUCT;

                        return parseStruct(buffer);
                    }

                    Preconditions.checkState(false, "Unexpected keyword 'struct'");
                    return ParseStatus.ERROR;
                } else if ("id".equalsIgnoreCase(token)
                        && templateParent != null) { //structId.tag == NO_TAG

                    final String idToken = buffer.getNextToken();

                    if (idToken.length() != 4) {
                        //struct id not 4 characters.
                        return ParseStatus.ERROR;
                    }

                    structId = new TagInfo(idToken);

                    return ParseStatus.SUCCESS;
                }
            }

            if (templateParent != null && structId == TagInfo.None) {
                //struct id not defined.
                return ParseStatus.ERROR;
            }

            switch (paramState) {
                case LIST:
                    if ("list".equalsIgnoreCase(token)) {
                        final int startingPosition = buffer.position();
                        final String listType = buffer.getNextWhitespaceToken();

                        if (Character.isDigit(listType.charAt(0))) {
                            parameter.listType = ListType.INT_ARRAY;
                            parameter.listSize = Integer.parseInt(listType);
                        } else if (listType.startsWith("enumList")) {
                            parameter.listType = ListType.ENUM_ARRAY;
                            parameter.enumListName = listType.substring(8);
                            final List<EnumData> enumList = getEnumList(parameter.enumListName, false);

                            if (enumList == null) {
                                //enum name not defined.
                                return ParseStatus.ERROR;
                            }

                            parameter.listSize = enumList.size();
                        } else {
                            parameter.listType = ListType.LIST;
                            hasList = true;
                            buffer.position(startingPosition);
                        }

                        paramState = ParamState.TYPE;
                        break;
                    }
                case TYPE:
                    if ("int".equalsIgnoreCase(token)) {
                        parameter.type = ParamType.INTEGER;
                        parameter.minIntLimit = Integer.MIN_VALUE;
                        parameter.maxIntLimit = Integer.MAX_VALUE;
                        paramState = ParamState.LIMITS;
                    } else if ("float".equalsIgnoreCase(token)) {
                        parameter.type = ParamType.FLOAT;
                        parameter.minFloatLimit = Float.MIN_VALUE;
                        parameter.maxFloatLimit = Float.MAX_VALUE;
                        paramState = ParamState.LIMITS;
                    } else if ("bool".equalsIgnoreCase(token)) {
                        parameter.type = ParamType.BOOL;
                        paramState = ParamState.LIMITS;
                    } else if ("string".equalsIgnoreCase(token)) {
                        parameter.type = ParamType.STRING;
                        paramState = ParamState.NAME;
                    } else if ("filename".equalsIgnoreCase(token)) {
                        parameter.type = ParamType.FILENAME;
                        paramState = ParamState.NAME;
                    } else if ("stringid".equalsIgnoreCase(token)) {
                        parameter.type = ParamType.STRING_ID;
                        paramState = ParamState.NAME;
                    } else if ("vector".equalsIgnoreCase(token)) {
                        parameter.type = ParamType.VECTOR;
                        paramState = ParamState.NAME;
                    } else if ("objvar".equalsIgnoreCase(token)) {
                        if (parameter.listType == ListType.LIST) {
                            //objvar may not be part of a list
                            return ParseStatus.ERROR;
                        }

                        if (parameter.type == ParamType.DYNAMIC_VAR) {
                            //server-only data type
                            return ParseStatus.ERROR;
                        }

                        parameter.type = ParamType.DYNAMIC_VAR;
                        hasDynamicVarParam = true;
                        paramState = ParamState.NAME;
                    } else if (token.startsWith("template")) {
                        parameter.type = ParamType.TEMPLATE;
                        parameter.extendedName = token.substring(8);
                        hasTemplateParam = true;
                        paramState = ParamState.NAME;
                    } else if (token.startsWith("enum")) {
                        parameter.type = ParamType.ENUM;
                        parameter.extendedName = token.substring(4);
                        if (getEnumList(parameter.extendedName, false) == null) {
                            //enum type parameter.extendedName not defined.
                            return ParseStatus.ERROR;
                        }

                        paramState = ParamState.NAME;
                    } else if (token.startsWith("struct")) {
                        parameter.type = ParamType.STRUCT;
                        parameter.extendedName = token.substring(6);
                        if (getStruct(parameter.extendedName) == null) {
                            //struct parameter.extendedName not defined.
                            return ParseStatus.ERROR;
                        }

                        paramState = ParamState.NAME;
                    } else if ("triggerVolume".equalsIgnoreCase(token)) {
                        parameter.type = ParamType.TRIGGER_VOLUME;
                        parameter.minFloatLimit = 0.0f;
                        paramState = ParamState.NAME;
                    } else {
                        //Expected "list" or parameter type.
                        return ParseStatus.ERROR;
                    }
                    break;
                case LIMITS:
                    if (Character.isDigit(token.charAt(0))
                            || token.charAt(0) == '-'
                            || (token.charAt(0) == '.' && token.charAt(1) == '.')) {

                        final TemplateLineBuffer tempBuffer = new TemplateLineBuffer(buffer);

                        if (Character.isDigit(tempBuffer.charAt(0)) || tempBuffer.charAt(0) == '-') {
                            final String tempToken = tempBuffer.getNextToken();

                            if (ParamType.INTEGER.equals(parameter.type)) {
                                parameter.minIntLimit = Integer.parseInt(tempToken, 10);
                            } else {
                                parameter.minFloatLimit = Float.parseFloat(tempToken);
                            }
                        }

                        if (tempBuffer.charAt(0) == '.' && tempBuffer.charAt(1) == '.') {
                            tempBuffer.skip(2);
                            final String tempToken = tempBuffer.getNextToken();

                            if (Character.isDigit(tempToken.charAt(0)) || tempToken.charAt(0) == '-') {
                                if (ParamType.INTEGER.equals(parameter.type)) {
                                    parameter.maxIntLimit = Integer.parseInt(tempToken, 10);
                                } else {
                                    parameter.maxFloatLimit = Float.parseFloat(tempToken);
                                }
                            }
                        }

                        paramState = ParamState.NAME;
                        break;
                    }
                case NAME:
                    if (token.charAt(0) == '_') {
                        //parameter names may not begin with '_'
                        return ParseStatus.ERROR;
                    }
                    parameter.name = token;
                    paramState = ParamState.DESCRIPTION;
                    break;
                case DESCRIPTION:
                default:
                    break;
            }
        }

        if (buffer.remaining() > 0) {
            parameter.description = buffer.getRemainingString();
        }

        parameters.add(parameter);
        parameterMap.put(parameter.name, parameter);

        return ParseStatus.SUCCESS;
    }

    private ParseStatus parseEnum(final TemplateLineBuffer buffer) {
        final String token = buffer.getNextToken();

        if (token == null || token.isEmpty())
            return ParseStatus.SUCCESS;

        if (token.charAt(0) == '{') {
            if (bracketCount == 0) {
                bracketCount = 1;
                return ParseStatus.SUCCESS;
            }

            //Unexpected {
            return ParseStatus.ERROR;
        } else if (token.charAt(0) == '}') {
            if (bracketCount == 1) {
                bracketCount = 0;
                currentEnumList = null;
                parseState = ParseState.PARAM;
                return ParseStatus.SUCCESS;
            }
        } else if (bracketCount == 0) {
            //Open bracket for enumeration not found.
            return ParseStatus.ERROR;
        }

        final EnumData enumData = new EnumData();
        enumData.name = token;

        if (!buffer.isAtEnd() && buffer.charAt(0) == '=') {
            buffer.getNextToken();

            if (buffer.isAtEnd())
                throw new IllegalStateException("expected value for enumeration after =");

            final StringBuilder sb = new StringBuilder();

            int value = parseIntValue(buffer, sb);

            enumData.valueName = sb.toString();
            enumData.value = value;
        } else if (currentEnumList.size() == 0) {
            enumData.value = 0;
        } else {
            enumData.value = currentEnumList.get(currentEnumList.size() - 1).value + 1;
        }

        if (!buffer.isAtEnd() && buffer.charAt(0) == ',')
            buffer.getNextToken();

        if (!buffer.isAtEnd()) {
            enumData.comment = buffer.getRemainingString();
        }

        currentEnumList.add(enumData);
        return ParseStatus.SUCCESS;
    }

    private ParseStatus parseStruct(final TemplateLineBuffer buffer) {
        final int startingPosition = buffer.position();

        final String token = buffer.getNextWhitespaceToken();

        if (token == null || token.isEmpty())
            return ParseStatus.SUCCESS;

        if (token.charAt(0) == '{') {
            if (bracketCount == 0)
                bracketCount = 1;
            else {
                //unexpected {
                return ParseStatus.ERROR;
            }
        } else if (token.charAt(0) == '}') {
            if (bracketCount == 1) {
                bracketCount = 0;
                currentStruct = null;
                parseState = ParseState.PARAM;
                return ParseStatus.SUCCESS;
            } else {
                //unexpected }
                return ParseStatus.ERROR;
            }
        } else if (bracketCount == 0) {
            //open bracket for struct not found
            return ParseStatus.ERROR;
        } else {
            buffer.position(startingPosition);
        }

        return currentStruct.parseLine(buffer);
    }

    /**
     * Parses a string and tries to convert it to an integer value.
     *
     * @param buffer    The value being converted.
     * @param intString A StringBuilder that will build the string that represents the integer.
     * @return The integer to which the string was parsed.
     */
    private int parseIntValue(final TemplateLineBuffer buffer, final StringBuilder intString) {
        Preconditions.checkNotNull(intString, "must pass an initialized instance of a StringBuilder");

        if (buffer.isAtEnd())
            throw new IllegalArgumentException("bad value passed.");

        //The final value of the int after parsing is done.
        int value;

        //check for negative number
        boolean negative = false;

        if (buffer.charAt(0) == '-') {
            negative = true;
            buffer.skip(1);

            if (buffer.isAtEnd())
                throw new IllegalStateException("parseIntValue: expected value after '-'");

            intString.append('-');
        }

        //an integer can be a normal value, a hex value starting with 0x, or an enum value
        //we can also have int value or'ed together with |
        if (Character.isAlphabetic(buffer.charAt(0))) {
            final String enumName = buffer.getNextToken();

            value = getEnumValue(enumName);

            if (value == Integer.MAX_VALUE) {
                return 0;
            }

            intString.append(enumName);
        } else if (buffer.remaining() > 1 && buffer.charAt(0) == '0' && buffer.charAt(1) == 'x') {
            buffer.skip(2);
            final String hexValue = buffer.getNextToken();
            //hex value
            value = (int) Long.parseLong(hexValue, 16);
            intString.append("0x").append(hexValue);
        } else if (Character.isDigit(buffer.charAt(0))) {
            final String intValue = buffer.getNextToken();
            value = Integer.parseInt(intValue, 10);
            intString.append(intValue);
        } else {
            //data not an integer
            return 0;
        }

        if (negative)
            value = -value;

        //Check if we want to or another int
        if (!buffer.isAtEnd() && buffer.charAt(0) == '|') {
            buffer.getNextToken();
            intString.append(" | ");
            value |= parseIntValue(buffer, intString);
        }

        return value;
    }

    /**
     * Finds the integral value associated with an enum value.
     *
     * @param enumValue The enumeration value.
     * @return the value, or Integer.MAX_VALUE if not found.
     */
    public int getEnumValue(final String enumValue) {
        for (final List<EnumData> enumList : enumMap.values()) {
            for (final EnumData enumData : enumList) {
                if (enumData.name.equalsIgnoreCase(enumValue))
                    return enumData.value;
            }
        }

        if (templateParent != null)
            return templateParent.getEnumValue(enumValue);

        if (fileParent != null) {
            final TemplateDefinitionFile baseFile = fileParent.getBaseDefinitionFile();

            if (baseFile != null)
                return baseFile.getTemplateData(baseFile.getHighestVersion()).getEnumValue(enumValue);
        }

        return Integer.MAX_VALUE;
    }

    /**
     * Find the integral value associated with an enum value.
     *
     * @param enumType  The enumeration type.
     * @param enumValue The enumeration value.
     * @return The value, or Integer.MAX_VALUE if not found.
     */
    public int getEnumValue(final String enumType, final String enumValue) {
        final List<EnumData> enumList = getEnumList(enumType, false);

        if (enumList == null)
            return Integer.MAX_VALUE;

        for (final EnumData enumData : enumList) {
            if (enumData.name.equalsIgnoreCase(enumValue))
                return enumData.value;
        }

        return Integer.MAX_VALUE;
    }

    /**
     * Tries to find an enum list defintion for this template. If it can't find one
     * it will try to look for the definition in the template's base definition.
     *
     * @param name   The enum list name.
     * @param define Flag that we are defining templates and should not look in base templates.
     * @return The enum list definition, or null if not found.
     */
    public List<EnumData> getEnumList(final String name, boolean define) {
        final List<EnumData> enumList = enumMap.get(name);

        if (enumList != null)
            return enumList;

        if (templateParent != null)
            return templateParent.getEnumList(name, define);

        if (!define && fileParent != null && fileParent.getBaseDefinitionFile() != null) {
            final TemplateData baseData = fileParent.getBaseDefinitionFile().getTemplateData(
                    fileParent.getBaseDefinitionFile().getHighestVersion());

            if (baseData != null)
                return baseData.getEnumList(name, define);
        }


        return null;
    }

    /**
     * Tries to find a struct definition for this template. If it can't find one
     * it will try to look for the definition in the template's base definition.
     *
     * @param name The struct name.
     * @return The struct definition, or null if not found.
     */
    public TemplateData getStruct(final String name) {
        final TemplateData structData = structMap.get(name);

        if (structData != null)
            return structData;

        if (templateParent != null)
            return templateParent.getStruct(name);

        if (fileParent != null && fileParent.getBaseDefinitionFile() != null) {
            final TemplateData baseData = fileParent.getBaseDefinitionFile().getTemplateData(
                    fileParent.getBaseDefinitionFile().getHighestVersion());

            if (baseData != null)
                return baseData.getStruct(name);
        }

        return null;
    }

    public TemplateDefinitionFile getTdf() {
        return fileParent;
    }

    public TemplateDefinitionFile getTdfParent() {
        if (fileParent != null) {
            return fileParent.getBaseDefinitionFile();
        } else {
            return null;
        }
    }

    /**
     * Returns the template name for this data.
     *
     * @return the template name
     */
    public String getName() {
        if (fileParent != null)
            return fileParent.getTemplateName();
        if (templateParent != null)
            return name;
        return "";
    }

    /**
     * Returns the template's base template name.
     *
     * @return the base template name.
     */
    public String getBaseName() {
        if (fileParent != null && !fileParent.getBaseName().isEmpty())
            return fileParent.getBaseName();

        return baseName;
    }

    /**
     * Returns the template's location (client, server, shared, none)
     *
     * @return The location.
     */
    public TemplateLocation getTemplateLocation() {
        if (fileParent != null)
            return fileParent.getTemplateLocation();
        if (templateParent != null)
            return templateParent.getTemplateLocation();
        return TemplateLocation.NONE;
    }


    enum ParamType {
        NONE,
        COMMENT,
        INTEGER,
        FLOAT,
        BOOL,
        STRING,
        STRING_ID,
        VECTOR,
        DYNAMIC_VAR,
        TEMPLATE,
        ENUM,
        STRUCT,
        TRIGGER_VOLUME,
        FILENAME
    }

    enum ListType {
        NONE,
        LIST,
        INT_ARRAY,
        ENUM_ARRAY
    }

    static class EnumData {
        public String name;
        public String valueName;
        public int value;
        public String comment;
    }

    static class Parameter {
        public ParamType type;
        public String name;
        public String description;
        public String extendedName;
        public int minIntLimit;
        public int maxIntLimit;
        public float minFloatLimit;
        public float maxFloatLimit;
        public ListType listType;
        public int listSize;
        public String enumListName;

    }

    /**
     * What we are currently parsing at the moment.
     */
    private enum ParseState {
        PARAM,
        ENUM,
        STRUCT
    }

    /**
     * What we are currently parsing during parsing of a parameter.
     */
    private enum ParamState {
        LIST,
        TYPE,
        NAME,
        LIMITS,
        DESCRIPTION
    }

    private enum ParseStatus {
        SUCCESS,
        ERROR
    }
}
