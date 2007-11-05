/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.xebia.springframework.jms.support.converter;

import junit.framework.TestCase;

/**
 * Test for {@link IbmCharsetUtils}
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public class IbmCharsetUtilsTest extends TestCase {

    public void testGetCharsetName819() {

        String actual = IbmCharsetUtils.getCharsetName(819);
        String expected = "ISO-8859-1";
        assertEquals(expected, actual);
    }

    public void testGetCharsetName923() {

        String actual = IbmCharsetUtils.getCharsetName(923);
        String expected = "ISO-8859-15";
        assertEquals(expected, actual);
    }

    public void testGetCharsetName1208() {

        String actual = IbmCharsetUtils.getCharsetName(1208);
        String expected = "UTF-8";
        assertEquals(expected, actual);
    }

    public void testGetCharsetName1200() {

        String actual = IbmCharsetUtils.getCharsetName(1200);
        String expected = "UTF-16";
        assertEquals(expected, actual);
    }

    public void testGetIbmCharacterSetIdUTF8() throws Exception {

        int actual = IbmCharsetUtils.getIbmCharacterSetId("UTF8");
        int expected = 1208;
        assertEquals(expected, actual);
    }

    public void testGetIbmCharacterSetIdUTF_8() throws Exception {

        int actual = IbmCharsetUtils.getIbmCharacterSetId("UTF-8");
        int expected = 1208;
        assertEquals(expected, actual);
    }

    public void testGetIbmCharacterSetIdUtf8() throws Exception {

        int actual = IbmCharsetUtils.getIbmCharacterSetId("utf8");
        int expected = 1208;
        assertEquals(expected, actual);
    }

    public void testGetIbmCharacterSetIdUtf_8() throws Exception {

        int actual = IbmCharsetUtils.getIbmCharacterSetId("utf-8");
        int expected = 1208;
        assertEquals(expected, actual);
    }

    public void testGetIbmCharacterSetIdISO88591() throws Exception {

        int actual = IbmCharsetUtils.getIbmCharacterSetId("ISO-8859-1");
        int expected = 819;
        assertEquals(expected, actual);
    }

    public void testGetIbmCharacterSetIdISO885915() throws Exception {

        int actual = IbmCharsetUtils.getIbmCharacterSetId("ISO-8859-15");
        int expected = 923;
        assertEquals(expected, actual);
    }
}
