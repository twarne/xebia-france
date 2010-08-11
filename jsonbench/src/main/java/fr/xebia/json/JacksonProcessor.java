package fr.xebia.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implémentation Jackson du {@link JSONProcessor}.
 * 
 * @author slm
 * 
 */
public class JacksonProcessor implements JSONProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(JacksonProcessor.class);

    private final ObjectMapper mapper = new ObjectMapper();

    public Object fromJSON(InputStream in, Class dst) {
        try {
            return mapper.readValue(in, dst);
        } catch (JsonParseException e) {
            LOG.warn("error on jackson API", e);
        } catch (JsonMappingException e) {
            LOG.warn("error on jackson API", e);
        } catch (IOException e) {
            LOG.warn("error on jackson API", e);
        }
        return null;
    }

    public void toJSON(OutputStream out, Object src) {
        try {
            mapper.writeValue(out, src);
        } catch (JsonGenerationException e) {
            LOG.warn("error on jackson API", e);
        } catch (JsonMappingException e) {
            LOG.warn("error on jackson API", e);
        } catch (IOException e) {
            LOG.warn("error on jackson API", e);
        }
    }

}
