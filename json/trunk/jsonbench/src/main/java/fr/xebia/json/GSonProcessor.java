package fr.xebia.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Implémentation Gson du {@link JSONProcessor}.
 * 
 * @author slm
 * 
 */
public class GSonProcessor implements JSONProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(GSonProcessor.class);

    private final Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy kk:mm:ss.S zz").create();

    @SuppressWarnings("unchecked")
    public Object fromJSON(InputStream in, Class dst) {

        return gson.fromJson(new InputStreamReader(in, Charset.forName("UTF-8")), dst);
    }

    public void toJSON(OutputStream out, Object src) {
        OutputStreamWriter writer = new OutputStreamWriter(out, Charset.forName("UTF-8"));
        gson.toJson(src, writer);
        try {
            writer.flush();
        } catch (IOException e) {
            LOG.error("IO error while flushing buffer after json serialization", e);
        }
    }

}
