package fr.xebia.demo.wicket.blog.view.util;

import javax.servlet.ServletContextEvent;

import org.apache.wicket.protocol.http.MockServletContext;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.service.CategoryService;
import fr.xebia.demo.wicket.blog.service.PostService;
import fr.xebia.demo.wicket.blog.view.WicketPageTest;


public class InitDataListenerTest extends WicketPageTest {

    @BeforeClass
    public static void setUpAppContext() {
        PostService postService = new PostService();
        postService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("postService", postService);

        CategoryService categoryService = new CategoryService();
        categoryService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("categoryService", categoryService);
    }

    @Test
    public void testContextInitialized() {
        InitDataListener listener = new InitDataListener();
        listener.setApplicationContext(appContext);
        MockServletContext mockServletContext = new MockServletContext(application, ".");
        ServletContextEvent event = new ServletContextEvent(mockServletContext);
        listener.contextInitialized(event);
    }

    @Test
    public void testContextDestroyed() {
        InitDataListener listener = new InitDataListener();
        listener.contextDestroyed(null);
    }
}
