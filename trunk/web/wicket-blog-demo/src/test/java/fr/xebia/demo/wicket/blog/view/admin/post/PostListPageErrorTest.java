package fr.xebia.demo.wicket.blog.view.admin.post;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.util.tester.FormTester;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.Post;
import fr.xebia.demo.wicket.blog.service.CategoryService;
import fr.xebia.demo.wicket.blog.service.PostService;
import fr.xebia.demo.wicket.blog.service.ServiceException;
import fr.xebia.demo.wicket.blog.view.WicketPageTest;

public class PostListPageErrorTest extends WicketPageTest {

    @BeforeClass
    public static void setUpAppContext() {
        PostService postService = getPostService();
        postService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("postService", postService);

        CategoryService categoryService = new CategoryService();
        categoryService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("categoryService", categoryService);
    }

    protected static PostService getPostService() {
        PostService postService = new PostService() {
            @Override
            public List<Post> search(Post exampleEntity) throws ServiceException {
                throw new ServiceException(ERROR_MESSAGE);
            }
            @Override
            public void save(Post exampleEntity) throws ServiceException {
                throw new ServiceException(ERROR_MESSAGE);
            }
            @Override
            public Post get(Serializable id) throws ServiceException {
                throw new ServiceException(ERROR_MESSAGE);
            }
            @Override
            public void deleteById(Serializable id) throws ServiceException {
            	throw new ServiceException(ERROR_MESSAGE);
            }
        };
        return postService;
    }

    @Test
    public void testErrorRender() {
        tester.startPage(PostListPage.class);
        tester.assertRenderedPage(PostListPage.class);
        tester.assertNoErrorMessage();
        tester.assertComponent("postForm", SearchPostForm.class);

        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester("postForm");
        // set the parameters for each component in the form
        form.setValue("author", "test");
        form.setValue("email", "Test");
        // all set, submit
        form.submit();
        // check if the page is correct: in this case, I'm expecting an error to take me back to the same page
        tester.assertRenderedPage(PostListPage.class);
        // if you're not expecting an error (testing for submit unsuccessful) use assertErrorMessage(String) instead
        tester.assertErrorMessages(new String[] { ERROR_MESSAGE });
    }
}
