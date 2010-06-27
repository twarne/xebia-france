import java.security.Provider;
import java.security.Security;

import javax.net.ssl.SSLContext;

import org.junit.Test;


public class SSLContextTest {

    @Test
    public void test() throws Exception {
        for (Provider provider : Security.getProviders()) {
            System.out.println(provider);
            System.out.println(provider.getInfo());
        }
        
        System.out.println("Security.getProviders(TLS)");
        for (Provider provider : Security.getProviders("TLS")) {
            System.out.println(provider);
            System.out.println(provider.getInfo());
        }
        
        
        SSLContext sslcontext = SSLContext.getInstance("TLS");
        System.out.println(sslcontext);
    }
}
