package fr.xebia.demo.wicket.blog.view.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import fr.xebia.demo.wicket.blog.service.CategoryService;
import fr.xebia.demo.wicket.blog.service.PostService;
import fr.xebia.demo.wicket.blog.service.SampleDataInitializer;

/**
 * Created for backward compatibility with Spring 2.0.x as {@link SampleDataInitializer} is now annotated
 * @author manuel_eveno
 */
public class InitDataListener implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(InitDataListener.class);
	
	private ApplicationContext springContext;
    
    public void setApplicationContext(ApplicationContext springContext) {
        this.springContext = springContext;
    }

    protected void getSpringContext(ServletContextEvent sce) {
        if (springContext == null) {
            springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext());
        }
    }

	@SuppressWarnings("unchecked")
    public void contextInitialized(ServletContextEvent sce) {
        getSpringContext(sce);

        SampleDataInitializer sampleDataInitializer = new SampleDataInitializer();
        try {
            sampleDataInitializer.setCategoryService((CategoryService) springContext.getBean("categoryService"));
            sampleDataInitializer.setPostService((PostService) springContext.getBean("postService"));
            sampleDataInitializer.initSampleData();
        } catch (BeansException e) {
            logger.error("Spring configuration problem or loading error : can't get service instances", e);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
