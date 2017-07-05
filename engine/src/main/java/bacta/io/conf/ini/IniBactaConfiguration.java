/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package bacta.io.conf.ini;

import bacta.io.conf.BactaConfiguration;

import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Collection;

/**
 * Created by crush on 3/21/14.
 */

@Singleton
public class IniBactaConfiguration implements BactaConfiguration {
    private final IniFile iniFile;

    public IniBactaConfiguration() throws URISyntaxException {
        InputStream configStream = IniBactaConfiguration.class.getResourceAsStream("/conf/config.ini");
        if(configStream == null) {
            configStream = IniBactaConfiguration.class.getResourceAsStream("/config.ini");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(configStream));
        iniFile = new IniFile(reader);
    }

    @Override
    public String getString(String sectionName, String propertyName) {
        return iniFile.getString(sectionName, propertyName);
    }

    @Override
    public String getString(String sectionName, String propertyName, int index) {
        return iniFile.getString(sectionName, propertyName, index);
    }

    @Override
    public String getStringLast(String sectionName, String propertyName) {
        return iniFile.getStringLast(sectionName, propertyName);
    }

    @Override
    public String getStringWithDefault(String sectionName, String propertyName, String defaultValue) {
        return iniFile.getStringWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public String getStringWithDefault(String sectionName, String propertyName, String defaultValue, int index) {
        return iniFile.getStringWithDefault(sectionName, propertyName, defaultValue, index);
    }

    @Override
    public String getStringLastWithDefault(String sectionName, String propertyName, String defaultValue) {
        return iniFile.getStringLastWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public Collection<String> getStringCollection(String sectionName, String propertyName) {
        return iniFile.getStringCollection(sectionName, propertyName);
    }

    @Override
    public boolean getBoolean(String sectionName, String propertyName) {
        return iniFile.getBoolean(sectionName, propertyName);
    }

    @Override
    public boolean getBoolean(String sectionName, String propertyName, int index) {
        return iniFile.getBoolean(sectionName, propertyName, index);
    }

    @Override
    public boolean getBooleanLast(String sectionName, String propertyName) {
        return iniFile.getBooleanLast(sectionName, propertyName);
    }

    @Override
    public boolean getBooleanWithDefault(String sectionName, String propertyName, boolean defaultValue) {
        return iniFile.getBooleanWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public boolean getBooleanWithDefault(String sectionName, String propertyName, boolean defaultValue, int index) {
        return iniFile.getBooleanWithDefault(sectionName, propertyName, defaultValue, index);
    }

    @Override
    public boolean getBooleanLastWithDefault(String sectionName, String propertyName, boolean defaultValue) {
        return iniFile.getBooleanLastWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public Collection<Boolean> getBooleanCollection(String sectionName, String propertyName) {
        return iniFile.getBooleanCollection(sectionName, propertyName);
    }

    @Override
    public byte getByte(String sectionName, String propertyName) {
        return iniFile.getByte(sectionName, propertyName);
    }

    @Override
    public byte getByte(String sectionName, String propertyName, int index) {
        return iniFile.getByte(sectionName, propertyName, index);
    }

    @Override
    public byte getByteLast(String sectionName, String propertyName) {
        return iniFile.getByteLast(sectionName, propertyName);
    }

    @Override
    public byte getByteWithDefault(String sectionName, String propertyName, byte defaultValue) {
        return iniFile.getByteWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public byte getByteWithDefault(String sectionName, String propertyName, byte defaultValue, int index) {
        return iniFile.getByteWithDefault(sectionName, propertyName, defaultValue, index);
    }

    @Override
    public byte getByteLastWithDefault(String sectionName, String propertyName, byte defaultValue) {
        return iniFile.getByteLastWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public Collection<Byte> getByteCollection(String sectionName, String propertyName) {
        return iniFile.getByteCollection(sectionName, propertyName);
    }

    @Override
    public short getShort(String sectionName, String propertyName) {
        return iniFile.getShort(sectionName, propertyName);
    }

    @Override
    public short getShort(String sectionName, String propertyName, int index) {
        return iniFile.getShort(sectionName, propertyName, index);
    }

    @Override
    public short getShortLast(String sectionName, String propertyName) {
        return iniFile.getShortLast(sectionName, propertyName);
    }

    @Override
    public short getShortWithDefault(String sectionName, String propertyName, short defaultValue) {
        return iniFile.getShortWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public short getShortWithDefault(String sectionName, String propertyName, short defaultValue, int index) {
        return iniFile.getShortWithDefault(sectionName, propertyName, defaultValue, index);
    }

    @Override
    public short getShortLastWithDefault(String sectionName, String propertyName, short defaultValue) {
        return iniFile.getShortLastWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public Collection<Short> getShortCollection(String sectionName, String propertyName) {
        return iniFile.getShortCollection(sectionName, propertyName);
    }

    @Override
    public int getInt(String sectionName, String propertyName) {
        return iniFile.getInt(sectionName, propertyName);
    }

    @Override
    public int getInt(String sectionName, String propertyName, int index) {
        return iniFile.getInt(sectionName, propertyName, index);
    }

    @Override
    public int getIntLast(String sectionName, String propertyName) {
        return iniFile.getIntLast(sectionName, propertyName);
    }

    @Override
    public int getIntWithDefault(String sectionName, String propertyName, int defaultValue) {
        return iniFile.getIntWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public int getIntWithDefault(String sectionName, String propertyName, int defaultValue, int index) {
        return iniFile.getIntWithDefault(sectionName, propertyName, defaultValue, index);
    }

    @Override
    public int getIntLastWithDefault(String sectionName, String propertyName, int defaultValue) {
        return iniFile.getIntLastWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public Collection<Integer> getIntCollection(String sectionName, String propertyName) {
        return iniFile.getIntCollection(sectionName, propertyName);
    }

    @Override
    public long getLong(String sectionName, String propertyName) {
        return iniFile.getLong(sectionName, propertyName);
    }

    @Override
    public long getLong(String sectionName, String propertyName, int index) {
        return iniFile.getLong(sectionName, propertyName, index);
    }

    @Override
    public long getLongLast(String sectionName, String propertyName) {
        return iniFile.getLongLast(sectionName, propertyName);
    }

    @Override
    public long getLongWithDefault(String sectionName, String propertyName, long defaultValue) {
        return iniFile.getLongWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public long getLongWithDefault(String sectionName, String propertyName, long defaultValue, int index) {
        return iniFile.getLongWithDefault(sectionName, propertyName, defaultValue, index);
    }

    @Override
    public long getLongLastWithDefault(String sectionName, String propertyName, long defaultValue) {
        return iniFile.getLongLastWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public Collection<Long> getLongCollection(String sectionName, String propertyName) {
        return iniFile.getLongCollection(sectionName, propertyName);
    }

    @Override
    public float getFloat(String sectionName, String propertyName) {
        return iniFile.getFloat(sectionName, propertyName);
    }

    @Override
    public float getFloat(String sectionName, String propertyName, int index) {
        return iniFile.getFloat(sectionName, propertyName, index);
    }

    @Override
    public float getFloatLast(String sectionName, String propertyName) {
        return iniFile.getFloatLast(sectionName, propertyName);
    }

    @Override
    public float getFloatWithDefault(String sectionName, String propertyName, float defaultValue) {
        return iniFile.getFloatWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public float getFloatWithDefault(String sectionName, String propertyName, float defaultValue, int index) {
        return iniFile.getFloatWithDefault(sectionName, propertyName, defaultValue, index);
    }

    @Override
    public float getFloatLastWithDefault(String sectionName, String propertyName, float defaultValue) {
        return iniFile.getFloatLastWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public Collection<Float> getFloatCollection(String sectionName, String propertyName) {
        return iniFile.getFloatCollection(sectionName, propertyName);
    }

    @Override
    public double getDouble(String sectionName, String propertyName) {
        return iniFile.getDouble(sectionName, propertyName);
    }

    @Override
    public double getDouble(String sectionName, String propertyName, int index) {
        return iniFile.getDouble(sectionName, propertyName, index);
    }

    @Override
    public double getDoubleLast(String sectionName, String propertyName) {
        return iniFile.getDoubleLast(sectionName, propertyName);
    }

    @Override
    public double getDoubleWithDefault(String sectionName, String propertyName, double defaultValue) {
        return iniFile.getDoubleWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public double getDoubleWithDefault(String sectionName, String propertyName, double defaultValue, int index) {
        return iniFile.getDoubleWithDefault(sectionName, propertyName, defaultValue, index);
    }

    @Override
    public double getDoubleLastWithDefault(String sectionName, String propertyName, double defaultValue) {
        return iniFile.getDoubleLastWithDefault(sectionName, propertyName, defaultValue);
    }

    @Override
    public Collection<Double> getDoubleCollection(String sectionName, String propertyName) {
        return iniFile.getDoubleCollection(sectionName, propertyName);
    }
}
