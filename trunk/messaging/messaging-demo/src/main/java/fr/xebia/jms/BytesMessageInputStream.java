/*
 * Copyright 2007 the original author or authors.
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

package fr.xebia.jms;

import java.io.IOException;
import java.io.InputStream;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MessageEOFException;

import org.springframework.util.Assert;

/**
 * Small modification on {@link org.springframework.ws.transport.jms.BytesMessageInputStream} 
 * in {@link #read()} method inspired by {@link java.io.ByteArrayInputStream.read()} : 
 * <code>return message.readByte() & 0xff;</code> instead of <code>return message.readByte() </code>.
 */
/**
 * Input stream that wraps a {@link BytesMessage}.
 * 
 * @author Arjen Poutsma
 * @since 1.5.0
 */
public class BytesMessageInputStream extends InputStream {

    private final BytesMessage message;

    public BytesMessageInputStream(BytesMessage message) {
        Assert.notNull(message, "'message' must not be null");
        this.message = message;
    }

    @Override
    public long skip(long n) throws IOException {
        throw new IOException("NOT supported skip(" + n + ")");
    }

    public int read(byte b[]) throws IOException {
        try {
            return message.readBytes(b);
        } catch (JMSException ex) {
            throw new IOException(ex);
        }
    }

    public int read(byte b[], int off, int len) throws IOException {
        if (off == 0) {
            try {
                return message.readBytes(b, len);
            } catch (JMSException ex) {
                throw new IOException(ex);
            }
        } else {
            return super.read(b, off, len);
        }
    }

    public int read() throws IOException {
        try {
            // & 0xff is inspired by java.io.ByteArrayInputStream.read() to
            // ensure value is in 0..255
            return message.readByte() & 0xff;
        } catch (MessageEOFException ex) {
            return -1;
        } catch (JMSException ex) {
            throw new IOException(ex);
        }
    }
}
