package fr.xebia.demo.wicket.blog.view;

import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.injection.annot.test.AnnotApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.BeforeClass;

// TODO Tester le delete des objets
public abstract class WicketPageTest {

    protected static final String ERROR_MESSAGE = "An expected error has occured";

    protected static EntityManagerFactory entityManagerFactory;
    protected static BlogApplication application;
    protected static WicketTester tester;
    protected static AnnotApplicationContextMock appContext;

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
                // Nothing to do here, we don't want security for unit testing
            }
        };
		tester = new WicketTester(application);
    }

	protected void assertTextFieldValue(String fieldId, String value) {
		TextField textField = (TextField) tester.getComponentFromLastRenderedPage(fieldId);
		assertTrue("Field Form is not correctly filled", textField.getModelObjectAsString().equals(value));
	}

    @Before
    public void clearContext() {
    	Session.get().getFeedbackMessages().clear();
	}
}