package fr.xebia.demo.wicket.blog.view.security;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebPage;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.view.WicketPageTest;

public class UnauthorizedComponentInstantiationListenerTest extends WicketPageTest {

    @SuppressWarnings("unchecked")
    @Test(expected=RestartResponseAtInterceptPageException.class)
    public void testOnUnauthorizedInstantiationForPage() {
        UnauthorizedComponentInstantiationListener listener = 
            new UnauthorizedComponentInstantiationListener(Application.get()
                .getHomePage());
        listener.onUnauthorizedInstantiation(new WebPage() {
            private static final long serialVersionUID = 1L;
        });
    }

    @SuppressWarnings("unchecked")
    @Test(expected=UnauthorizedInstantiationException.class)
    public void testOnUnauthorizedInstantiationForComponent() {
        UnauthorizedComponentInstantiationListener listener = 
            new UnauthorizedComponentInstantiationListener(Application.get()
                .getHomePage());
        listener.onUnauthorizedInstantiation(new Component("fakeId") {
            private static final long serialVersionUID = 1L;
            @Override
            protected void onRender(MarkupStream markupStream) {
            }
        });
    }
}
