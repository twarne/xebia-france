package fr.xebia.demo.wicket.blog.view.admin.post;

import java.io.Serializable;
import java.util.Date;

import org.apache.wicket.markup.html.link.Link;
import org.junit.Before;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.Post;
import fr.xebia.demo.wicket.blog.service.CategoryService;
import fr.xebia.demo.wicket.blog.service.PostService;
import fr.xebia.demo.wicket.blog.service.ServiceException;
import fr.xebia.demo.wicket.blog.view.WicketPageTest;

public class ViewPostPageErrorTest extends WicketPageTest {

    @Before
    public void setUpAppContext() throws ServiceException {
        CategoryService categoryService = new CategoryService();
        categoryService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("categoryService", categoryService);

        PostService postService = getPostService();
        postService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("postService", postService);
        Post post = new Post();
        post.setTitle("Title");
        post.setAuthor("Me");
        post.setDate(new Date());
        post.setModified(new Date());
        post.setCommentsAllowed(true);
        post.setStatus(Post.STATUS_DRAFT);
        post.setContent("Fake Content");
        postService.save(post);
    }

    protected PostService getPostService() {
        PostService postService = new PostService() {
            @Override
            public Post get(Serializable id) throws ServiceException {
                throw new ServiceException(ERROR_MESSAGE);
            }
        };
        return postService;
    }

    @Test
    public void testErrorRender() {
        tester.startPage(ListPostPage.class);
        tester.assertRenderedPage(ListPostPage.class);
        tester.assertNoErrorMessage();
        tester.assertComponent("posts:0:viewLink", Link.class);
        tester.clickLink("posts:0:viewLink");
        tester.assertRenderedPage(ListPostPage.class);
        tester.assertErrorMessages(new String[] { ERROR_MESSAGE });
    }
}
