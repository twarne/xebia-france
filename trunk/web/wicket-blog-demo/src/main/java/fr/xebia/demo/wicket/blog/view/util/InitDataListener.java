package fr.xebia.demo.wicket.blog.view.util;

import java.util.Date;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
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

	public void contextInitialized(ServletContextEvent sce) {
	    if (springContext == null) {
	        springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext());
	    }

		CategoryService categoryService = (CategoryService) springContext.getBean("categoryService");
		Category category = new Category();
		category.setName("divers");
		category.setNicename("Divers");
		category.setDescription("Articles divers");
		try {
			categoryService.save(category);
		} catch (ServiceException e) {
			logger.error("Error storing defaults objects in database", e);
			return ;
		}

		PostService postService = (PostService) springContext.getBean("postService");
		Post post = new Post();
		post.setTitle("Welcome to the Wicket Blog Demo");
		post.setAuthor("Xebia");
		post.setCategory(category);
		post.setDate(new Date());
		post.setModified(new Date());
		post.setCommentsAllowed(true);
		post.setStatus(Post.STATUS_PUBLISHED);
		post.setContent("Voici une application demonstratrice du framework web appelé Wicket !!\n" +
				"Vous pouvez retrouver l'ensemble du code source à l'adresse suivante :\n" +
				"http://xebia-france.googlecode.com/svn/trunk/web/wicket-blog-demo/");
		
		try {
			postService.save(post);
		} catch (ServiceException e) {
			logger.error("Error storing defaults objects in database", e);
			return ;
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {
	}
}
