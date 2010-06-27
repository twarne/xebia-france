import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.Test;


public class Slf4jTest {

    @Test
    public void test() throws Exception {
        Logger logger = LoggerFactory.getLogger(Slf4jTest.class);
        logger.error("my message", new Exception("an exception", new IOException("an ioexception")));
    }
}
