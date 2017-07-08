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

package io.bacta.conf.ini;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by crush on 3/21/14.
 */
public class IniFileTest {
    private IniFile iniFile = new IniFile(IniFileTest.class.getResource("/test.ini").getPath());

    @Test
    public void fileExists() throws Exception {
        assertTrue(iniFile != null);
    }

    @Test
    public void testInclude() throws Exception {
        assertTrue(iniFile.getBoolean("TestSection/IncludeTest", "testInclude"));
    }

    @Test
    public void testGetStringIncensitive() throws Exception {
        assertEquals("Test1", iniFile.getString("TestSection", "teststring1"));
        assertEquals("Test1", iniFile.getString("TestSection", "TESTSTRING1"));
        assertEquals("Test1", iniFile.getString("TestSection", "TestString1"));
        assertEquals("Test1", iniFile.getString("TestSection", "testString1"));
    }

    @Test
    public void testGetString() throws Exception {
        assertEquals("Test1", iniFile.getString("TestSection", "testString1"));
    }

    @Test
    public void testGetStringWithNullContent() throws Exception {
        assertEquals("", iniFile.getString("TestSection", "testString2"));
    }

    @Test
    public void testGetStringWithIndex() throws Exception {
        assertEquals("test2", iniFile.getString("TestSection", "testString", 1));
    }

    @Test
    public void testGetStringLast() throws Exception {
        assertEquals("test3", iniFile.getStringLast("TestSection", "testString"));
    }

    @Test
    public void testGetStringWithDefault() throws Exception {
        //No default value needed cases
        assertEquals("test3", iniFile.getStringWithDefault("TestSection", "testString", "testValue"));

        //Default value cases
        assertEquals("testValue", iniFile.getStringWithDefault("NonexistentSection", "testString", "testValue"));
        assertEquals("testValue", iniFile.getStringWithDefault("TestSection", "nonexistentProperty", "testValue"));
    }

    @Test
    public void testGetStringWithDefaultWithIndex() throws Exception {
        //No default value needed cases
        assertEquals("test3", iniFile.getStringWithDefault("TestSection", "testString", "testValue", 2));

        //Default value cases
        assertEquals("testValue", iniFile.getStringWithDefault("NonexistentSection", "testString", "testValue", 2));
        assertEquals("testValue", iniFile.getStringWithDefault("TestSection", "nonexistentProperty", "testValue", 2));
        assertEquals("testValue", iniFile.getStringWithDefault("TestSection", "testString", "testValue", 129234));
    }

    @Test
    public void testGetStringLastWithDefault() throws Exception {
        assertEquals("test3", iniFile.getStringLastWithDefault("TestSection", "testString", "testValue"));
        assertEquals("testValue", iniFile.getStringLastWithDefault("TestSection", "nonexistentProperty", "testValue"));
        assertEquals("testValue", iniFile.getStringLastWithDefault("NonexistentSection", "testString", "testValue"));
    }

    @Test
    public void testGetStringCollection() throws Exception {
        final String[] expected = new String[]{"test1", "test2", "test3"};
        final String[] actual = iniFile.getStringCollection("TestSection", "testString").toArray(new String[3]);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetBoolean() throws Exception {

    }

    @Test
    public void testGetBoolean1() throws Exception {

    }

    @Test
    public void testGetBooleanLast() throws Exception {

    }

    @Test
    public void testGetBooleanWithDefault() throws Exception {

    }

    @Test
    public void testGetBooleanWithDefault1() throws Exception {

    }

    @Test
    public void testGetBooleanLastWithDefault() throws Exception {

    }

    @Test
    public void testGetBooleanCollection() throws Exception {

    }

    @Test
    public void testGetByte() throws Exception {

    }

    @Test
    public void testGetByte1() throws Exception {

    }

    @Test
    public void testGetByteLast() throws Exception {

    }

    @Test
    public void testGetByteWithDefault() throws Exception {

    }

    @Test
    public void testGetByteWithDefault1() throws Exception {

    }

    @Test
    public void testGetByteLastWithDefault() throws Exception {

    }

    @Test
    public void testGetByteCollection() throws Exception {

    }

    @Test
    public void testGetShort() throws Exception {

    }

    @Test
    public void testGetShort1() throws Exception {

    }

    @Test
    public void testGetShortLast() throws Exception {

    }

    @Test
    public void testGetShortWithDefault() throws Exception {

    }

    @Test
    public void testGetShortWithDefault1() throws Exception {

    }

    @Test
    public void testGetShortLastWithDefault() throws Exception {

    }

    @Test
    public void testGetShortCollection() throws Exception {

    }

    @Test
    public void testGetInt() throws Exception {

    }

    @Test
    public void testGetInt1() throws Exception {

    }

    @Test
    public void testGetIntLast() throws Exception {

    }

    @Test
    public void testGetIntWithDefault() throws Exception {

    }

    @Test
    public void testGetIntWithDefault1() throws Exception {

    }

    @Test
    public void testGetIntLastWithDefault() throws Exception {

    }

    @Test
    public void testGetIntCollection() throws Exception {

    }

    @Test
    public void testGetLong() throws Exception {

    }

    @Test
    public void testGetLong1() throws Exception {

    }

    @Test
    public void testGetLongLast() throws Exception {

    }

    @Test
    public void testGetLongWithDefault() throws Exception {

    }

    @Test
    public void testGetLongWithDefault1() throws Exception {

    }

    @Test
    public void testGetLongLastWithDefault() throws Exception {

    }

    @Test
    public void testGetLongCollection() throws Exception {

    }

    @Test
    public void testGetFloat() throws Exception {

    }

    @Test
    public void testGetFloat1() throws Exception {

    }

    @Test
    public void testGetFloatLast() throws Exception {

    }

    @Test
    public void testGetFloatWithDefault() throws Exception {

    }

    @Test
    public void testGetFloatWithDefault1() throws Exception {

    }

    @Test
    public void testGetFloatLastWithDefault() throws Exception {

    }

    @Test
    public void testGetFloatCollection() throws Exception {

    }

    @Test
    public void testGetDouble() throws Exception {

    }

    @Test
    public void testGetDouble1() throws Exception {

    }

    @Test
    public void testGetDoubleLast() throws Exception {

    }

    @Test
    public void testGetDoubleWithDefault() throws Exception {

    }

    @Test
    public void testGetDoubleWithDefault1() throws Exception {

    }

    @Test
    public void testGetDoubleLastWithDefault() throws Exception {

    }

    @Test
    public void testGetDoubleCollection() throws Exception {

    }
}
