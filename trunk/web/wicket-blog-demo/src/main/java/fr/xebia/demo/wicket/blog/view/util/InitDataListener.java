package fr.xebia.demo.wicket.blog.view.util;

import java.util.Date;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import fr.xebia.demo.wicket.blog.data.Category;
import fr.xebia.demo.wicket.blog.data.Post;
import fr.xebia.demo.wicket.blog.service.CategoryService;
import fr.xebia.demo.wicket.blog.service.PostService;
import fr.xebia.demo.wicket.blog.service.ServiceException;

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
        if (logger.isDebugEnabled()) {
            logger.debug("Listing BeanDefinitionNames from spring context");
            for (String beanName : springContext.getBeanDefinitionNames()) {
                logger.debug("beanName:" + beanName);
            }
        }
    }

	@SuppressWarnings("unchecked")
    public void contextInitialized(ServletContextEvent sce) {
        getSpringContext(sce);

        logger.debug("Creating sample data");
		Category category = createSampleCategory();
        Post post = createSamplePost(category);
		try {
		    CategoryService categoryService = (CategoryService) springContext.getBean("categoryService");
		    PostService postService = (PostService) springContext.getBean("postService");
			categoryService.save(category);
			postService.save(post);
	        logger.debug("Sample category and post added");
		} catch (BeansException e) {
            logger.error("Spring configuration problem or loading error : can't get service instances", e);
        } catch (ServiceException e) {
			logger.error("Error storing defaults objects in database", e);
		}
    }

    private Post createSamplePost(Category category) {
        Post post = new Post();
		post.setTitle("Welcome to the Wicket Blog Demo");
		post.setAuthor("Manuel@Xebia");
		post.setCategory(category);
		post.setDate(new Date());
		post.setModified(new Date());
		post.setCommentsAllowed(true);
		post.setStatus(Post.STATUS_PUBLISHED);
		post.setContent("Voici une application demonstratrice du framework web appelé Wicket !!\n"
		        + "Vous pouvez retrouver l'ensemble du code source à l'adresse suivante :\n"
		        + "http://xebia-france.googlecode.com/svn/trunk/web/wicket-blog-demo/");
        return post;
    }

    private Category createSampleCategory() {
        Category category = new Category();
		category.setName("divers");
		category.setNicename("Divers");
		category.setDescription("Articles divers");
        return category;
    }

	public void contextDestroyed(ServletContextEvent sce) {
	}
}
