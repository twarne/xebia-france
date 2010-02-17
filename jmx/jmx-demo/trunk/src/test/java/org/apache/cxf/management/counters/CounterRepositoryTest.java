package org.apache.cxf.management.counters;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.cxf.common.logging.LogUtils;

import org.junit.Test;

public class CounterRepositoryTest {

    @Test
    public void testLog() throws Exception {

        Logger log = LogUtils.getL7dLogger(CounterRepository.class);
        log.log(Level.WARNING, "INSTRUMENTATION_REGISTER_FAULT_MSG", new Object[] {
            new ObjectName("test:test=my test"), new InstanceAlreadyExistsException("i exist")
        });

        log.log(Level.WARNING, "CANNOT_FIND_STATUS", "my-status");

        
        log.log(Level.WARNING, "CANNOT_FIND_THE_COUNTER_OBJECTNAME", "my-name");

        log.log(Level.WARNING, "CANNOT_CREATE_LOGGER_FILE", "my-name");
        
        log.log(Level.WARNING, "CANNOT_FIND_THE_COUNTERREPOSITORY");

        
        
    }
}
