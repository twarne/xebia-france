package fr.xebia.demo.wicket.blog.view.util;

import java.util.MissingResourceException;

import org.apache.wicket.PageParameters;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.view.HomePage;
import fr.xebia.demo.wicket.blog.view.HomePageTest;

public class LocalizerUtilsTest extends HomePageTest {

    @Test
    public void testGetString() {
        HomePage component = new HomePage(new PageParameters());
        // Get from bundle directly attached to component
        LocalizerUtils.getString(component, "comment.addCommentLink");
        // Get from bundle attached to component
        LocalizerUtils.getString(component, "project.name");
    }

//    @Test(expected=MissingResourceException.class)
//    public void testGetStringWithParams() {
//        HomePage component = new HomePage(new PageParameters());
//        String actualMessage = LocalizerUtils.getString(component, "Required", "myLabel");
//        String expectedMessage = "Field 'myLabel' is required";
//        assertEquals("Wrong returned message", expectedMessage, actualMessage);
//    }

    @Test(expected=MissingResourceException.class)
    public void testGetStringFailed() {
        HomePage component = new HomePage(new PageParameters());
        LocalizerUtils.getString(component, "non.existing.key");
    }
}
