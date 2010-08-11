package fr.xebia.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implémentation JSON.org du {@link JSONProcessor}. La méthode {@link #fromJSON(InputStream, Class)} retourne toujours un
 * {@link JSONObject}. Le mapping du bean doit-être fait manuellement.
 * 
 * @author slm
 * 
 */
public class JSONOrgProcessor implements JSONProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(JSONOrgProcessor.class);

    public Object fromJSON(InputStream in, Class dst) {
        try {
            JSONTokener tok = new JSONTokener(new java.io.InputStreamReader(in, Charset.forName("UTF-8")));

            return new JSONObject(tok);

        } catch (JSONException e) {
            LOG.warn("error on json.org API", e);
        }
        return null;
    }

    public void toJSON(OutputStream out, Object src) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(out, Charset.forName("UTF-8"));
            new JSONObject(src).write(writer);
            writer.flush();

        } catch (JSONException e) {
            LOG.warn("error on json.org API", e);
        } catch (IOException e) {
            LOG.warn("error on json.org API", e);
        }
    }
}
