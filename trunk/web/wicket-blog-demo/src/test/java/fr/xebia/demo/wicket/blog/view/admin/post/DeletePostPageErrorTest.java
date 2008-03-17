package fr.xebia.demo.wicket.blog.view.admin.post;

import static org.junit.Assert.fail;

import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.Post;
import fr.xebia.demo.wicket.blog.service.PostService;
import fr.xebia.demo.wicket.blog.service.ServiceException;

public class DeletePostPageErrorTest extends PostListPageErrorTest {

    private static final Logger logger = Logger.getLogger(DeletePostPageErrorTest.class);
    
    @Override
    @Test
    public void testErrorRender() {
    	PostService postService = new PostService();
    	postService.setEntityManagerFactory(entityManagerFactory);
    	Post post = new Post();
    	post.setAuthor("test");
    	post.setTitle("Title");
    	post.setContent("Post content");
    	post.setDate(new Date());
    	post.setModified(new Date());
    	post.setCommentsAllowed(Boolean.FALSE);
    	post.setStatus(Post.STATUS_PUBLISHED);
		try {
			postService.save(post);
		} catch (ServiceException e) {
		    logger.error(e);
			fail("Error while creating fixture");
		}
    	
        tester.startPage(PostListPage.class);
        tester.assertRenderedPage(PostListPage.class);
        tester.assertNoErrorMessage();
        
        tester.clickLink("posts:0:deleteLink");
        tester.assertErrorMessages(new String[] { ERROR_MESSAGE });
        tester.assertRenderedPage(PostListPage.class);
    }
}
