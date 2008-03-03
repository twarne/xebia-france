package fr.xebia.demo.wicket.blog.view.admin.post;

import org.junit.BeforeClass;

import fr.xebia.demo.wicket.blog.service.CategoryService;
import fr.xebia.demo.wicket.blog.service.PostService;
import fr.xebia.demo.wicket.blog.view.WicketPageTest;

public abstract class PostPageTest extends WicketPageTest {

    @BeforeClass
    public static void setUpAppContext() {
        CategoryService categoryService = new CategoryService();
        categoryService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("categoryService", categoryService);

        PostService postService = new PostService();
        postService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("postService", postService);
    }
}
