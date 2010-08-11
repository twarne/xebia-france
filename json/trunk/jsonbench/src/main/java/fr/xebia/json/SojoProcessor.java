package fr.xebia.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import net.sf.sojo.interchange.json.JsonSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mapping JSON avec Sojo
 * 
 * @author slm
 * 
 */
public class SojoProcessor implements JSONProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(SojoProcessor.class);

    private final JsonSerializer ser = new JsonSerializer();

    public Object fromJSON(InputStream in, Class dst) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")), 200);
            StringBuilder sbuff = new StringBuilder(200);
            String nbc = null;

            while ((nbc = reader.readLine()) != null) {
                sbuff.append(nbc);
            }

            LOG.debug("La chaine json est: {}", sbuff);
            return ser.deserialize(sbuff, dst);
        } catch (IOException e) {
            LOG.warn("error on sojo API", e);
        }
        return null;
    }

    public void toJSON(OutputStream out, Object src) {

        String res = (String) ser.serialize(src);
        LOG.debug("La chaine JSON est : {}", res);
        try {
            OutputStreamWriter o = new OutputStreamWriter(out, Charset.forName("UTF-8"));
            o.write(res);
            o.flush();
        } catch (IOException e) {
            LOG.warn("error on sojo API", e);
        }

    }

}
