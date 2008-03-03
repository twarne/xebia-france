package fr.xebia.demo.wicket.blog.view.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.wicket.PageParameters;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.view.BasePage;

public class PageParametersUtilsTest {
    
    @Test
    public void testFromStringMessage() {
        PageParameters pageParameters = PageParametersUtils.fromStringMessage("Not yet implemented");
        Object value = pageParameters.get(BasePage.PARAM_MESSAGE_KEY);
        assertNotNull("message is null", value);
        assertTrue("message is not string", (value instanceof String));
    }

    @Test
    public void testPageParameters() {
        PageParameters pageParameters = new PageParameters();
        pageParameters.put("key", Long.valueOf(10));
        Object value = pageParameters.get("key");
        assertNotNull("message is null", value);
        assertTrue("message is not long", (value instanceof Long));
    }

    @Test
    public void testFromStringErrorMessage() {
        PageParameters pageParameters = PageParametersUtils.fromStringErrorMessage("Not yet implemented");
        Object value = pageParameters.get(BasePage.PARAM_ERRORMESSAGE_KEY);
        assertNotNull("message is null", value);
        assertTrue("message is not string", (value instanceof String));
    }

    @Test
    public void testFromException() {
        PageParameters pageParameters = PageParametersUtils.fromException(new Exception());
        Object value = pageParameters.get(BasePage.PARAM_EXCEPTION_KEY);
        assertNotNull("message is null", value);
        assertTrue("message is not throwable", (value instanceof Throwable));
    }
}
