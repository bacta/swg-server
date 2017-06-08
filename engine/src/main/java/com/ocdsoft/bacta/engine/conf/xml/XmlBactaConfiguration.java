package com.ocdsoft.bacta.engine.conf.xml;

import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;

import java.io.FileInputStream;
import java.util.Collection;

public class XmlBactaConfiguration implements BactaConfiguration {
    private final XMLConfiguration config = new XMLConfiguration();

    public XmlBactaConfiguration() {
        try {
            XMLConfiguration.setDefaultExpressionEngine(new XPathExpressionEngine());
            config.load(new FileInputStream("../com.ocdsoft.conf/com.ocdsoft.conf.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getString(String sectionName, String propertyName) {
        return null;
    }

    @Override
    public String getString(String sectionName, String propertyName, int index) {
        return null;
    }

    @Override
    public String getStringLast(String sectionName, String propertyName) {
        return null;
    }

    @Override
    public String getStringWithDefault(String sectionName, String propertyName, String defaultValue) {
        return null;
    }

    @Override
    public String getStringWithDefault(String sectionName, String propertyName, String defaultValue, int index) {
        return null;
    }

    @Override
    public String getStringLastWithDefault(String sectionName, String propertyName, String defaultValue) {
        return null;
    }

    @Override
    public Collection<String> getStringCollection(String sectionName, String propertyName) {
        return null;
    }

    @Override
    public boolean getBoolean(String sectionName, String propertyName) {
        return false;
    }

    @Override
    public boolean getBoolean(String sectionName, String propertyName, int index) {
        return false;
    }

    @Override
    public boolean getBooleanLast(String sectionName, String propertyName) {
        return false;
    }

    @Override
    public boolean getBooleanWithDefault(String sectionName, String propertyName, boolean defaultValue) {
        return false;
    }

    @Override
    public boolean getBooleanWithDefault(String sectionName, String propertyName, boolean defaultValue, int index) {
        return false;
    }

    @Override
    public boolean getBooleanLastWithDefault(String sectionName, String propertyName, boolean defaultValue) {
        return false;
    }

    @Override
    public Collection<Boolean> getBooleanCollection(String sectionName, String propertyName) {
        return null;
    }

    @Override
    public byte getByte(String sectionName, String propertyName) {
        return 0;
    }

    @Override
    public byte getByte(String sectionName, String propertyName, int index) {
        return 0;
    }

    @Override
    public byte getByteLast(String sectionName, String propertyName) {
        return 0;
    }

    @Override
    public byte getByteWithDefault(String sectionName, String propertyName, byte defaultValue) {
        return 0;
    }

    @Override
    public byte getByteWithDefault(String sectionName, String propertyName, byte defaultValue, int index) {
        return 0;
    }

    @Override
    public byte getByteLastWithDefault(String sectionName, String propertyName, byte defaultValue) {
        return 0;
    }

    @Override
    public Collection<Byte> getByteCollection(String sectionName, String propertyName) {
        return null;
    }

    @Override
    public short getShort(String sectionName, String propertyName) {
        return 0;
    }

    @Override
    public short getShort(String sectionName, String propertyName, int index) {
        return 0;
    }

    @Override
    public short getShortLast(String sectionName, String propertyName) {
        return 0;
    }

    @Override
    public short getShortWithDefault(String sectionName, String propertyName, short defaultValue) {
        return 0;
    }

    @Override
    public short getShortWithDefault(String sectionName, String propertyName, short defaultValue, int index) {
        return 0;
    }

    @Override
    public short getShortLastWithDefault(String sectionName, String propertyName, short defaultValue) {
        return 0;
    }

    @Override
    public Collection<Short> getShortCollection(String sectionName, String propertyName) {
        return null;
    }

    @Override
    public int getInt(String sectionName, String propertyName) {
        return 0;
    }

    @Override
    public int getInt(String sectionName, String propertyName, int index) {
        return 0;
    }

    @Override
    public int getIntLast(String sectionName, String propertyName) {
        return 0;
    }

    @Override
    public int getIntWithDefault(String sectionName, String propertyName, int defaultValue) {
        return 0;
    }

    @Override
    public int getIntWithDefault(String sectionName, String propertyName, int defaultValue, int index) {
        return 0;
    }

    @Override
    public int getIntLastWithDefault(String sectionName, String propertyName, int defaultValue) {
        return 0;
    }

    @Override
    public Collection<Integer> getIntCollection(String sectionName, String propertyName) {
        return null;
    }

    @Override
    public long getLong(String sectionName, String propertyName) {
        return 0;
    }

    @Override
    public long getLong(String sectionName, String propertyName, int index) {
        return 0;
    }

    @Override
    public long getLongLast(String sectionName, String propertyName) {
        return 0;
    }

    @Override
    public long getLongWithDefault(String sectionName, String propertyName, long defaultValue) {
        return 0;
    }

    @Override
    public long getLongWithDefault(String sectionName, String propertyName, long defaultValue, int index) {
        return 0;
    }

    @Override
    public long getLongLastWithDefault(String sectionName, String propertyName, long defaultValue) {
        return 0;
    }

    @Override
    public Collection<Long> getLongCollection(String sectionName, String propertyName) {
        return null;
    }

    @Override
    public float getFloat(String sectionName, String propertyName) {
        return 0;
    }

    @Override
    public float getFloat(String sectionName, String propertyName, int index) {
        return 0;
    }

    @Override
    public float getFloatLast(String sectionName, String propertyName) {
        return 0;
    }

    @Override
    public float getFloatWithDefault(String sectionName, String propertyName, float defaultValue) {
        return 0;
    }

    @Override
    public float getFloatWithDefault(String sectionName, String propertyName, float defaultValue, int index) {
        return 0;
    }

    @Override
    public float getFloatLastWithDefault(String sectionName, String propertyName, float defaultValue) {
        return 0;
    }

    @Override
    public Collection<Float> getFloatCollection(String sectionName, String propertyName) {
        return null;
    }

    @Override
    public double getDouble(String sectionName, String propertyName) {
        return 0;
    }

    @Override
    public double getDouble(String sectionName, String propertyName, int index) {
        return 0;
    }

    @Override
    public double getDoubleLast(String sectionName, String propertyName) {
        return 0;
    }

    @Override
    public double getDoubleWithDefault(String sectionName, String propertyName, double defaultValue) {
        return 0;
    }

    @Override
    public double getDoubleWithDefault(String sectionName, String propertyName, double defaultValue, int index) {
        return 0;
    }

    @Override
    public double getDoubleLastWithDefault(String sectionName, String propertyName, double defaultValue) {
        return 0;
    }

    @Override
    public Collection<Double> getDoubleCollection(String sectionName, String propertyName) {
        return null;
    }
}
