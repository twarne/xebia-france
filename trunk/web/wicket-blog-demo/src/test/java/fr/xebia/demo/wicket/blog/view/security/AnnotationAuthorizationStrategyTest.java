package fr.xebia.demo.wicket.blog.view.security;

import static org.junit.Assert.assertSame;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.WebPage;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.view.BasePage;
import fr.xebia.demo.wicket.blog.view.WicketPageTest;


public class AnnotationAuthorizationStrategyTest extends WicketPageTest {

    @Test
    public void testIsActionAuthorizedForAnyPage() {
        AnnotationAuthorizationStrategy strategy = new AnnotationAuthorizationStrategy(WebPage.class);
        WebPage anyPage = new WebPage() {
            private static final long serialVersionUID = 1L;
        };
        boolean isAuthorized = strategy.isActionAuthorized(anyPage, new Action("any"));
        assertSame("Page should be authorized", isAuthorized, true);
    }

    @Test
    public void testIsActionAuthorizedForNonSecuredPage() {
        AnnotationAuthorizationStrategy strategy = new AnnotationAuthorizationStrategy(WebPage.class);
        WebPage nonsecuredPage = new BasePage(new PageParameters()) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isSecured() {
                return false;
            }
        
            @Override
            protected List<MenuItem> getMenuItems() {
                return new LinkedList<MenuItem>();
            }
        
        };
        boolean isAuthorized = strategy.isActionAuthorized(nonsecuredPage, new Action("any"));
        assertSame("Page should be authorized", isAuthorized, true);
    }

    @Test(expected=RestartResponseAtInterceptPageException.class)
    public void testIsActionAuthorizedForSecuredPage() {
        AnnotationAuthorizationStrategy strategy = new AnnotationAuthorizationStrategy(WebPage.class);
        WebPage securedPage = new BasePage(new PageParameters()) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isSecured() {
                return true;
            }
        
            @Override
            protected List<MenuItem> getMenuItems() {
                return new LinkedList<MenuItem>();
            }
        
        };
        strategy.isActionAuthorized(securedPage, new Action("any"));
    }

}
