package fr.xebia.demo.wicket.blog.view.security;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.WebPage;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.User;
import fr.xebia.demo.wicket.blog.view.BasePage;
import fr.xebia.demo.wicket.blog.view.BlogWebSession;
import fr.xebia.demo.wicket.blog.view.WicketPageTest;


public class AnnotationAuthorizationStrategyTest extends WicketPageTest {

    @Test
    public void testIsActionAuthorizedForPage() {
        AnnotationAuthorizationStrategy strategy = new AnnotationAuthorizationStrategy(WebPage.class);
        Page anyPage = new Page() {
            private static final long serialVersionUID = 1L;
        };
        boolean isAuthorized = strategy.isActionAuthorized(anyPage, new Action("any"));
        assertSame("Page should be authorized", isAuthorized, true);
    }

    @Test
    public void testIsActionAuthorizedForWebPage() {
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

    @Test
    public void testIsActionAuthorizedForSecuredPageWithUserInSession() {
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
        BlogWebSession session = (BlogWebSession) Session.get();
        session.setUser(new User());
        boolean isAuthorized = strategy.isActionAuthorized(securedPage, new Action("any"));
        assertSame("Page should be authorized", isAuthorized, true);
    }

    @Test
    public void testIsInstantiationAuthorized() {
        AnnotationAuthorizationStrategy strategy = new AnnotationAuthorizationStrategy(WebPage.class);
        assertTrue("Should always return 'true'", strategy.isInstantiationAuthorized(Class.class));
    }
}
