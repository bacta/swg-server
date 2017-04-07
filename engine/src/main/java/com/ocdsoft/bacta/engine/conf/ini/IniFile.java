package com.ocdsoft.bacta.engine.conf.ini;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by crush on 3/21/14.
 * <p/>
 * Replacement for ini4j since it can't handle includes for some retarded dumb reason.
 */
public class IniFile implements IniReader {
    private final static Pattern includePattern = Pattern.compile("^\\.include\\s+(?:\"|')?([^\"']+)(?:\"|')?");
    private final static Pattern sectionPattern = Pattern.compile("^\\s*\\[([^\\]\\[]+)\\]\\s*$");

    private final String baseDirectory;
    private final Map<String, Section> sections = new HashMap<>();
    private Section currentSection = null;

    public IniFile(final String fullPath) {
        File file = new File(fullPath);
        baseDirectory = file.getParent();

        load(file.getName());
    }

    public IniFile(final BufferedReader reader) {
        baseDirectory = System.getProperty("user.dir");
        load(reader);
    }

    @Override
    public String getString(String sectionName, String propertyName) {
        return getStringLast(sectionName, propertyName);
    }

    @Override
    public String getString(String sectionName, String propertyName, int index) {
        return getStringWithDefault(sectionName, propertyName, null, index);
    }

    @Override
    public String getStringLast(String sectionName, String propertyName) {
        return getStringLastWithDefault(sectionName, propertyName, null);
    }

    @Override
    public String getStringWithDefault(String sectionName, String propertyName, String defaultValue) {
        return getStringLastWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public String getStringWithDefault(String sectionName, String propertyName, String defaultValue, int index) {
        Section section = sections.get(sectionName);

        if (section == null)
            return defaultValue;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null || index < 0 || index > list.size())
            return defaultValue;

        return list.get(index);
    }

    @Override
    public String getStringLastWithDefault(String sectionName, String propertyName, String defaultValue) {
        Section section = sections.get(sectionName);

        if (section == null)
            return defaultValue;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null)
            return defaultValue;

        return list.getLast();
    }

    @Override
    public Collection<String> getStringCollection(String sectionName, String propertyName) {
        Section section = sections.get(sectionName);

        if (section == null)
            return null;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null) {
            return null;
        }

        return Collections.unmodifiableCollection(list);
    }

    @Override
    public boolean getBoolean(String sectionName, String propertyName) {
        return getBooleanLast(sectionName, propertyName);
    }

    @Override
    public boolean getBoolean(String sectionName, String propertyName, int index) {
        return getBooleanWithDefault(sectionName, propertyName, false, index);
    }

    @Override
    public boolean getBooleanLast(String sectionName, String propertyName) {
        return getBooleanLastWithDefault(sectionName, propertyName, false);
    }

    @Override
    public boolean getBooleanWithDefault(String sectionName, String propertyName, boolean defaultValue) {
        return getBooleanLastWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public boolean getBooleanWithDefault(String sectionName, String propertyName, boolean defaultValue, int index) {
        Section section = sections.get(sectionName);

        if (section == null)
            return defaultValue;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null || index < 0 || index > list.size())
            return defaultValue;

        return convertStringToBoolean(list.get(index));
    }

    @Override
    public boolean getBooleanLastWithDefault(String sectionName, String propertyName, boolean defaultValue) {
        Section section = sections.get(sectionName);

        if (section == null)
            return defaultValue;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null)
            return defaultValue;

        return convertStringToBoolean(list.getLast());
    }

    @Override
    public Collection<Boolean> getBooleanCollection(String sectionName, String propertyName) {
        Section section = sections.get(sectionName);

        if (section == null)
            return null;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null)
            return null;

        final Collection<Boolean> collection = new ArrayList<>(list.size());

        for (String entry : list)
            collection.add(convertStringToBoolean(entry));

        return collection;
    }

    @Override
    public byte getByte(String sectionName, String propertyName) {
        return getByteLast(sectionName, propertyName);
    }

    @Override
    public byte getByte(String sectionName, String propertyName, int index) {
        return getByteWithDefault(sectionName, propertyName, (byte) 0, index);
    }

    @Override
    public byte getByteLast(String sectionName, String propertyName) {
        return getByteLastWithDefault(sectionName, propertyName, (byte) 0);
    }

    @Override
    public byte getByteWithDefault(String sectionName, String propertyName, byte defaultValue) {
        return getByteLastWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public byte getByteWithDefault(String sectionName, String propertyName, byte defaultValue, int index) {
        Section section = sections.get(sectionName);

        if (section == null)
            return defaultValue;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null || index < 0 || index > list.size())
            return defaultValue;

        return convertStringToByte(list.get(index));
    }

    @Override
    public byte getByteLastWithDefault(String sectionName, String propertyName, byte defaultValue) {
        Section section = sections.get(sectionName);

        if (section == null)
            return defaultValue;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null)
            return defaultValue;

        return convertStringToByte(list.getLast());
    }

    @Override
    public Collection<Byte> getByteCollection(String sectionName, String propertyName) {
        Section section = sections.get(sectionName);

        if (section == null)
            return null;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null)
            return null;

        final Collection<Byte> collection = new ArrayList<>(list.size());

        for (String entry : list)
            collection.add(convertStringToByte(entry));

        return collection;
    }

    @Override
    public short getShort(String sectionName, String propertyName) {
        return getShortLast(sectionName, propertyName);
    }

    @Override
    public short getShort(String sectionName, String propertyName, int index) {
        return getShortWithDefault(sectionName, propertyName, (short) 0, index);
    }

    @Override
    public short getShortLast(String sectionName, String propertyName) {
        return getShortLastWithDefault(sectionName, propertyName, (short) 0);
    }

    @Override
    public short getShortWithDefault(String sectionName, String propertyName, short defaultValue) {
        return getShortLastWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public short getShortWithDefault(String sectionName, String propertyName, short defaultValue, int index) {
        Section section = sections.get(sectionName);

        if (section == null)
            return defaultValue;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null || index < 0 || index > list.size())
            return defaultValue;

        return convertStringToShort(list.get(index));
    }

    @Override
    public short getShortLastWithDefault(String sectionName, String propertyName, short defaultValue) {
        Section section = sections.get(sectionName);

        if (section == null)
            return defaultValue;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null)
            return defaultValue;

        return convertStringToShort(list.getLast());
    }

    @Override
    public Collection<Short> getShortCollection(String sectionName, String propertyName) {
        Section section = sections.get(sectionName);

        if (section == null)
            return null;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null)
            return null;

        final Collection<Short> collection = new ArrayList<>(list.size());

        for (String entry : list)
            collection.add(convertStringToShort(entry));

        return collection;
    }

    @Override
    public int getInt(String sectionName, String propertyName) {
        return getIntLast(sectionName, propertyName);
    }

    @Override
    public int getInt(String sectionName, String propertyName, int index) {
        return getIntWithDefault(sectionName, propertyName, 0, index);
    }

    @Override
    public int getIntLast(String sectionName, String propertyName) {
        return getIntLastWithDefault(sectionName, propertyName, 0);
    }

    @Override
    public int getIntWithDefault(String sectionName, String propertyName, int defaultValue) {
        return getIntLastWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public int getIntWithDefault(String sectionName, String propertyName, int defaultValue, int index) {
        Section section = sections.get(sectionName);

        if (section == null)
            return defaultValue;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null || index < 0 || index > list.size())
            return defaultValue;

        return convertStringToInteger(list.get(index));
    }

    @Override
    public int getIntLastWithDefault(String sectionName, String propertyName, int defaultValue) {
        Section section = sections.get(sectionName);

        if (section == null)
            return defaultValue;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null)
            return defaultValue;

        return convertStringToInteger(list.getLast());
    }

    @Override
    public Collection<Integer> getIntCollection(String sectionName, String propertyName) {
        Section section = sections.get(sectionName);

        if (section == null)
            return null;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null)
            return null;

        final Collection<Integer> collection = new ArrayList<>(list.size());

        for (String entry : list)
            collection.add(convertStringToInteger(entry));

        return collection;
    }

    @Override
    public long getLong(String sectionName, String propertyName) {
        return getLongLast(sectionName, propertyName);
    }

    @Override
    public long getLong(String sectionName, String propertyName, int index) {
        return getLongWithDefault(sectionName, propertyName, 0, index);
    }

    @Override
    public long getLongLast(String sectionName, String propertyName) {
        return getLongLastWithDefault(sectionName, propertyName, 0);
    }

    @Override
    public long getLongWithDefault(String sectionName, String propertyName, long defaultValue) {
        return getLongLastWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public long getLongWithDefault(String sectionName, String propertyName, long defaultValue, int index) {
        Section section = sections.get(sectionName);

        if (section == null)
            return defaultValue;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null || index < 0 || index > list.size())
            return defaultValue;

        return convertStringToLong(list.get(index));
    }

    @Override
    public long getLongLastWithDefault(String sectionName, String propertyName, long defaultValue) {
        Section section = sections.get(sectionName);

        if (section == null)
            return defaultValue;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null)
            return defaultValue;

        return convertStringToLong(list.getLast());
    }

    @Override
    public Collection<Long> getLongCollection(String sectionName, String propertyName) {
        Section section = sections.get(sectionName);

        if (section == null)
            return null;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null)
            return null;

        final Collection<Long> collection = new ArrayList<>(list.size());

        for (String entry : list)
            collection.add(convertStringToLong(entry));

        return collection;
    }

    @Override
    public float getFloat(String sectionName, String propertyName) {
        return getFloatLast(sectionName, propertyName);
    }

    @Override
    public float getFloat(String sectionName, String propertyName, int index) {
        return getFloatWithDefault(sectionName, propertyName, 0, index);
    }

    @Override
    public float getFloatLast(String sectionName, String propertyName) {
        return getFloatLastWithDefault(sectionName, propertyName, 0);
    }

    @Override
    public float getFloatWithDefault(String sectionName, String propertyName, float defaultValue) {
        return getFloatLastWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public float getFloatWithDefault(String sectionName, String propertyName, float defaultValue, int index) {
        Section section = sections.get(sectionName);

        if (section == null)
            return defaultValue;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null || index < 0 || index > list.size())
            return defaultValue;

        return convertStringToFloat(list.get(index));
    }

    @Override
    public float getFloatLastWithDefault(String sectionName, String propertyName, float defaultValue) {
        Section section = sections.get(sectionName);

        if (section == null)
            return defaultValue;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null)
            return defaultValue;

        return convertStringToFloat(list.getLast());
    }

    @Override
    public Collection<Float> getFloatCollection(String sectionName, String propertyName) {
        Section section = sections.get(sectionName);

        if (section == null)
            return null;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null)
            return null;

        final Collection<Float> collection = new ArrayList<>(list.size());

        for (String entry : list)
            collection.add(convertStringToFloat(entry));

        return collection;
    }

    @Override
    public double getDouble(String sectionName, String propertyName) {
        return getDoubleLast(sectionName, propertyName);
    }

    @Override
    public double getDouble(String sectionName, String propertyName, int index) {
        return getDoubleWithDefault(sectionName, propertyName, 0, index);
    }

    @Override
    public double getDoubleLast(String sectionName, String propertyName) {
        return getDoubleLastWithDefault(sectionName, propertyName, 0);
    }

    @Override
    public double getDoubleWithDefault(String sectionName, String propertyName, double defaultValue) {
        return getDoubleLastWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public double getDoubleWithDefault(String sectionName, String propertyName, double defaultValue, int index) {
        Section section = sections.get(sectionName);

        if (section == null)
            return defaultValue;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null || index < 0 || index > list.size())
            return defaultValue;

        return convertStringToDouble(list.get(index));
    }

    @Override
    public double getDoubleLastWithDefault(String sectionName, String propertyName, double defaultValue) {
        Section section = sections.get(sectionName);

        if (section == null)
            return defaultValue;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null)
            return defaultValue;

        return convertStringToDouble(list.getLast());
    }

    @Override
    public Collection<Double> getDoubleCollection(String sectionName, String propertyName) {
        Section section = sections.get(sectionName);

        if (section == null)
            return null;

        LinkedList<String> list = section.properties.get(propertyName);

        if (list == null)
            return null;

        final Collection<Double> collection = new ArrayList<>(list.size());

        for (String entry : list)
            collection.add(convertStringToDouble(entry));

        return collection;
    }

    public void load(final String filePath) {
        try {

            final BufferedReader reader;

            if (Files.exists(Paths.get(filePath))) {
                reader = new BufferedReader(new FileReader(filePath));
            } else if (Files.exists(Paths.get(baseDirectory + File.separator + filePath))){
                reader = new BufferedReader(new FileReader(baseDirectory + File.separator + filePath));
            } else if (Files.exists(Paths.get(baseDirectory + File.separator + "../" + filePath))){
                reader = new BufferedReader(new FileReader(baseDirectory + File.separator + "../" + filePath));
            } else {
                return;
            }

            load(reader);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void load(final BufferedReader reader) {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void parseLine(String line) {
        //First, strip out anything beyond a comment character.
        line = line.substring(0, indexOfComment(line));

        if (line.length() <= 0)
            return;
        if (line.startsWith(".include")) {
            parseInclude(line);
        } else if (line.charAt(0) == '[') {
            //It's a section name.
            parseSection(line);
        } else {
            //It's probably a property of the current section
            parseProperty(line);
        }
    }

    /**
     * Find the first occurrence of a comment character. Make sure we aren't parsing a quoted string though.
     *
     * @param line
     * @return
     */
    private int indexOfComment(final String line) {
        char lastQuoteCharacter = '\0';
        int i = 0;

        for (; i < line.length(); i++) {
            //If we aren't in a quoted string, then check if the character is the start of a comment.
            if (lastQuoteCharacter == '\0') {
                switch (line.charAt(i)) {
                    //String quote character
                    case '\'':
                    case '"':
                        lastQuoteCharacter = line.charAt(i);
                        break;

                    //Comment characters
                    case '#':
                    case ';':
                        return i; //Return the current index because it's the start of the comment.
                }
            } else {
                //If we are in a quoted string, and the current character is the same type of quote
                //Then shutdown the quote.
                if (line.charAt(i) == lastQuoteCharacter)
                    lastQuoteCharacter = '\0';
            }
        }

        return i;
    }

    private void parseInclude(final String line) {
        //Try to get the file name.
        Matcher matcher = includePattern.matcher(line);

        if (matcher.find())
            load(matcher.group(1));
    }

    private void parseSection(final String line) {
        Matcher matcher = sectionPattern.matcher(line);

        if (matcher.find()) {
            String sectionName = matcher.group(1);

            Section section = sections.get(sectionName);

            if (section == null) {
                section = new Section(sectionName);
                sections.put(sectionName, section);
            }

            currentSection = section;
        }
    }

    private void parseProperty(final String line) {
        if (currentSection != null) {
            final String[] parts = line.split(":|=", 2);

            if (parts.length == 2) { //Success, otherwise ignore it...it's malformed. Should print error mesage?
                final String key = parts[0].trim();
                final String value = parts[1].trim();

                final LinkedList<String> list = currentSection.properties.get(key);

                if (list == null) {
                    currentSection.properties.put(
                            key, //key
                            new LinkedList<>(Collections.singletonList(value))); //value
                } else {
                    list.addLast(value);
                }
            }
        }
    }

    private boolean convertStringToBoolean(final String value) {
        if (value == null)
            return false;

        return value.equalsIgnoreCase("yes")
                || value.equalsIgnoreCase("1")
                || value.equalsIgnoreCase("true");
    }

    private byte convertStringToByte(final String value) {
        return Byte.parseByte(value);
    }

    private short convertStringToShort(final String value) {
        return Short.parseShort(value);
    }

    private int convertStringToInteger(final String value) {

        if (value.startsWith("0x")) {
            //Treat it like hexadecimal.
            long val = Long.parseLong(value.substring(2), 16);

            return val > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) val;
        }

        return Integer.parseInt(value);
    }

    private long convertStringToLong(final String value) {
        return Long.parseLong(value);
    }

    private float convertStringToFloat(final String value) {
        return Float.parseFloat(value);
    }

    private double convertStringToDouble(final String value) {
        return Double.parseDouble(value);
    }

    private final class Section {
        protected final Map<String, LinkedList<String>> properties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        protected final String sectionName;

        public Section(final String sectionName) {
            this.sectionName = sectionName;
        }
    }
}
