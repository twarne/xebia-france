package fr.xebia.demo.wicket.blog.view;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.wicket.Application;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.injection.annot.test.AnnotApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.BeforeClass;

public abstract class WicketPageTest {

    protected static WicketTester tester;
    protected static AnnotApplicationContextMock appContext;
    protected static EntityManagerFactory entityManagerFactory;
    protected static BlogApplication application;

    @BeforeClass
    public static void setUpClass() {
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