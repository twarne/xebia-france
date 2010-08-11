package fr.xebia.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

/**
 * Mapping JSON avec XStream
 * 
 * @author slm
 * 
 */
public class XStreamProcessor implements JSONProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(XStreamProcessor.class);

    private final XStream xstream = new XStream(new JettisonMappedXmlDriver());

    public XStreamProcessor() {
        xstream.setMode(XStream.NO_REFERENCES);

    }

    @SuppressWarnings("unchecked")
    public Object fromJSON(InputStream in, Class dst) {
        xstream.alias("object", dst);
        return xstream.fromXML(new InputStreamReader(in, Charset.forName("UTF-8")));
    }

    public void toJSON(OutputStream out, Object src) {
        xstream.alias("object", src.getClass());
        OutputStreamWriter writer = new OutputStreamWriter(out, Charset.forName("UTF-8"));
        xstream.toXML(src, writer);
        try {
            writer.flush();
        } catch (IOException e) {
            LOG.error("IO error while flushing buffer after json serialization", e);
        }
    }

}
