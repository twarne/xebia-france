package fr.xebia.demo.wicket.blog.view;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.wicket.Application;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.injection.annot.test.AnnotApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;

// TODO Tester le delete des objets
public abstract class WicketPageTest {

    protected static final String ERROR_MESSAGE = "An expected error has occured";

    protected WicketTester tester;
    protected AnnotApplicationContextMock appContext;
    protected EntityManagerFactory entityManagerFactory;
    protected BlogApplication application;

    @Before
    public void setUpClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory("mainManager");
        // 1. setup mock injection environment
        appContext = new AnnotApplicationContextMock();
        // 2. setup WicketTester and injector for @SpringBean
        application = new BlogApplication(appContext) {
        	@Override
        	public String getConfigurationType() {
        		return Application.DEVELOPMENT;
        	}

            @Override
            protected void initSpringInjection() {
                final SpringComponentInjector springComponentInjector = new SpringComponentInjector(this, appContext);
                addComponentInstantiationListener(springComponentInjector);
            }

            @Override
            protected void initSecuritySettings() {
            }
        };
		tester = new WicketTester(application);
    }
}