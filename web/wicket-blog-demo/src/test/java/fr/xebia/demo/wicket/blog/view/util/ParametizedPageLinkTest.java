package fr.xebia.demo.wicket.blog.view.util;

import static org.junit.Assert.assertTrue;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

public class ParametizedPageLinkTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testOnClick() {
        // Created just for initialization purpose
        new WicketTester();
        PageParameters pageParameters = new PageParameters();
        pageParameters.put("key", "value");
        ParametizedPageLink link = new ParametizedPageLink("fakeId", WebPage.class, pageParameters);
        link.onClick();
        Class pageClass = link.getRequestCycle().getResponsePageClass();
        assertTrue("Wrong response page class", WebPage.class.isAssignableFrom(pageClass));
    }
}
