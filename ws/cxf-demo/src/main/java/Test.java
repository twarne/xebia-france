import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.wsdl.extensions.ExtensionDeserializer;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.util.StringUtils;

import junit.framework.TestCase;

public class Test extends TestCase {

    public static final String KEY = "org.apache.cxf.Logger";

    public void testExtensionDeserializer() throws Exception {
        try {
            String cname = System.getProperty(KEY);
            if (StringUtils.isEmpty(cname)) {
                InputStream ins = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/cxf/" + KEY);
                if (ins == null) {
                    ins = ClassLoader.getSystemResourceAsStream("META-INF/cxf/" + KEY);
                }
                if (ins != null) {
                    BufferedReader din = new BufferedReader(new InputStreamReader(ins));
                    cname = din.readLine();
                }
            }
            if (!StringUtils.isEmpty(cname)) {
                Class loggerClass = Class.forName(cname, true, Thread.currentThread().getContextClassLoader());
                System.out.println(loggerClass);
            }
        } catch (Exception ex) {
            // ignore
        }
    }
}
