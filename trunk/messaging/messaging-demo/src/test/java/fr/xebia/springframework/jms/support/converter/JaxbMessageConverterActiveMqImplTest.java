/*
 * Copyright 2002-2008 Xebia and the original author or authors.
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
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Marshaller;

import org.junit.Test;

/**
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public class JaxbMessageConverterActiveMqImplTest {

    @Test
    public void testSetMessageCharsetUtf8LowerCase() throws Exception {
        String encoding = "utf-8";

        JaxbMessageConverterActiveMqImpl jaxbMessageConverter = createJaxbMessageConverterActiveMqImpl(encoding);

        jaxbMessageConverter.postProcessResponseMessage(null);
    }

    private JaxbMessageConverterActiveMqImpl createJaxbMessageConverterActiveMqImpl(final String encoding) {
        JaxbMessageConverterActiveMqImpl jaxbMessageConverter = new JaxbMessageConverterActiveMqImpl();
        Map<String, String> marshallerProperties = new HashMap<String, String>();
        marshallerProperties.put(Marshaller.JAXB_ENCODING, encoding);
        jaxbMessageConverter.setMarshallerProperties(marshallerProperties);
        return jaxbMessageConverter;
    }

    @Test
    public void testSetMessageCharsetUtf8UpperCase() throws Exception {
        String encoding = "UTF-8";

        JaxbMessageConverterActiveMqImpl jaxbMessageConverter = createJaxbMessageConverterActiveMqImpl(encoding);

        jaxbMessageConverter.postProcessResponseMessage(null);
    }

    @Test
    public void testSetMessageCharsetUtf8NoDash() throws Exception {
        String encoding = "utf8";

        JaxbMessageConverterActiveMqImpl jaxbMessageConverter = createJaxbMessageConverterActiveMqImpl(encoding);

        jaxbMessageConverter.postProcessResponseMessage(null);
    }

    @Test(expected = UnsupportedCharsetException.class)
    public void testSetMessageCharsetUnsupported() throws Exception {
        String encoding = "ISO-8859-1";

        JaxbMessageConverterActiveMqImpl jaxbMessageConverter = createJaxbMessageConverterActiveMqImpl(encoding);

        jaxbMessageConverter.postProcessResponseMessage(null);
        // should throw an UnsupportedCharsetException
    }

}
