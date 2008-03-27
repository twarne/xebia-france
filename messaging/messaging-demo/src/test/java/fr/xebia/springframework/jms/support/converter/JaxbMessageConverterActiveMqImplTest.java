/*
 * Copyright 2007 Xebia and the original author or authors.
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

import java.nio.charset.UnsupportedCharsetException;

import org.junit.Test;

/**
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public class JaxbMessageConverterActiveMqImplTest {

    @Test
    public void testSetMessageCharsetUtf8LowerCase() throws Exception {
        JaxbMessageConverterActiveMqImpl jaxbMessageConverter = new JaxbMessageConverterActiveMqImpl();

        jaxbMessageConverter.setMessageCharset(null, "utf-8");
    }

    @Test
    public void testSetMessageCharsetUtf8UpperCase() throws Exception {
        JaxbMessageConverterActiveMqImpl jaxbMessageConverter = new JaxbMessageConverterActiveMqImpl();

        jaxbMessageConverter.setMessageCharset(null, "UTF-8");
    }

    @Test
    public void testSetMessageCharsetUtf8NoDash() throws Exception {
        JaxbMessageConverterActiveMqImpl jaxbMessageConverter = new JaxbMessageConverterActiveMqImpl();

        jaxbMessageConverter.setMessageCharset(null, "UTF8");
    }

    @Test(expected = UnsupportedCharsetException.class)
    public void testSetMessageCharsetUnsupported() throws Exception {
        JaxbMessageConverterActiveMqImpl jaxbMessageConverter = new JaxbMessageConverterActiveMqImpl();

        jaxbMessageConverter.setMessageCharset(null, "ISO-8859-1");
        // should throw an UnsupportedCharsetException
    }

}
