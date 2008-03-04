package fr.xebia.demo.wicket.blog.view;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.injection.annot.test.AnnotApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.BeforeClass;

public abstract class WicketPageTest {

    protected static WicketTester tester;
    protected static AnnotApplicationContextMock appContext;
    protected static EntityManagerFactory entityManagerFactory;

    @BeforeClass
    public static void setUpClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory("mainManager");
        // 1. setup mock injection environment
        appContext = new AnnotApplicationContextMock();
        // 2. setup WicketTester and injector for @SpringBean
        tester = new WicketTester(new WebApplication() {
			@Override
			public Class<? extends WebPage> getHomePage() {
				return HomePage.class;
			}
            @Override
            public Session newSession(Request request, Response response) {
                return new BlogWebSession(request);
            }
		});
        tester.getApplication().addComponentInstantiationListener(new SpringComponentInjector(tester.getApplication(), appContext));
    }
}