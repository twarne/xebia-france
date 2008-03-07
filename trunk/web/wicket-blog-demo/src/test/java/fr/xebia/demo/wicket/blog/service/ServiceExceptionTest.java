package fr.xebia.demo.wicket.blog.service;

import static org.junit.Assert.assertSame;

import org.junit.Test;



public class ServiceExceptionTest {

    @Test
    public void testServiceException() {
        Exception rootException = new Exception();
        assertSame("RootException is not keept", new ServiceException("", rootException).getCause(), rootException);
    }

    @Test
    public void testServiceExceptionWithNestedException() {
        Exception rootException = new Exception();
        assertSame("RootException is not keept", new ServiceException("", new Exception(rootException)).getCause(), rootException);
    }
}
