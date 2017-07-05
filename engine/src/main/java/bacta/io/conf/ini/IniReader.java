package bacta.io.conf.ini;

import java.util.Collection;

/**
 * Created by crush on 3/21/14.
 */
public interface IniReader {
    String getString(String sectionName, String propertyName);

    String getString(String sectionName, String propertyName, int index);

    String getStringLast(String sectionName, String propertyName);

    String getStringWithDefault(String sectionName, String propertyName, String defaultValue);

    String getStringWithDefault(String sectionName, String propertyName, String defaultValue, int index);

    String getStringLastWithDefault(String sectionName, String propertyName, String defaultValue);

    Collection<String> getStringCollection(String sectionName, String propertyName);

    boolean getBoolean(String sectionName, String propertyName);

    boolean getBoolean(String sectionName, String propertyName, int index);

    boolean getBooleanLast(String sectionName, String propertyName);

    boolean getBooleanWithDefault(String sectionName, String propertyName, boolean defaultValue);

    boolean getBooleanWithDefault(String sectionName, String propertyName, boolean defaultValue, int index);

    boolean getBooleanLastWithDefault(String sectionName, String propertyName, boolean defaultValue);

    Collection<Boolean> getBooleanCollection(String sectionName, String propertyName);

    byte getByte(String sectionName, String propertyName);

    byte getByte(String sectionName, String propertyName, int index);

    byte getByteLast(String sectionName, String propertyName);

    byte getByteWithDefault(String sectionName, String propertyName, byte defaultValue);

    byte getByteWithDefault(String sectionName, String propertyName, byte defaultValue, int index);

    byte getByteLastWithDefault(String sectionName, String propertyName, byte defaultValue);

    Collection<Byte> getByteCollection(String sectionName, String propertyName);

    short getShort(String sectionName, String propertyName);

    short getShort(String sectionName, String propertyName, int index);

    short getShortLast(String sectionName, String propertyName);

    short getShortWithDefault(String sectionName, String propertyName, short defaultValue);

    short getShortWithDefault(String sectionName, String propertyName, short defaultValue, int index);

    short getShortLastWithDefault(String sectionName, String propertyName, short defaultValue);

    Collection<Short> getShortCollection(String sectionName, String propertyName);

    int getInt(String sectionName, String propertyName);

    int getInt(String sectionName, String propertyName, int index);

    int getIntLast(String sectionName, String propertyName);

    int getIntWithDefault(String sectionName, String propertyName, int defaultValue);

    int getIntWithDefault(String sectionName, String propertyName, int defaultValue, int index);

    int getIntLastWithDefault(String sectionName, String propertyName, int defaultValue);

    Collection<Integer> getIntCollection(String sectionName, String propertyName);

    long getLong(String sectionName, String propertyName);

    long getLong(String sectionName, String propertyName, int index);

    long getLongLast(String sectionName, String propertyName);

    long getLongWithDefault(String sectionName, String propertyName, long defaultValue);

    long getLongWithDefault(String sectionName, String propertyName, long defaultValue, int index);

    long getLongLastWithDefault(String sectionName, String propertyName, long defaultValue);

    Collection<Long> getLongCollection(String sectionName, String propertyName);

    float getFloat(String sectionName, String propertyName);

    float getFloat(String sectionName, String propertyName, int index);

    float getFloatLast(String sectionName, String propertyName);

    float getFloatWithDefault(String sectionName, String propertyName, float defaultValue);

    float getFloatWithDefault(String sectionName, String propertyName, float defaultValue, int index);

    float getFloatLastWithDefault(String sectionName, String propertyName, float defaultValue);

    Collection<Float> getFloatCollection(String sectionName, String propertyName);

    double getDouble(String sectionName, String propertyName);

    double getDouble(String sectionName, String propertyName, int index);

    double getDoubleLast(String sectionName, String propertyName);

    double getDoubleWithDefault(String sectionName, String propertyName, double defaultValue);

    double getDoubleWithDefault(String sectionName, String propertyName, double defaultValue, int index);

    double getDoubleLastWithDefault(String sectionName, String propertyName, double defaultValue);

    Collection<Double> getDoubleCollection(String sectionName, String propertyName);
}
