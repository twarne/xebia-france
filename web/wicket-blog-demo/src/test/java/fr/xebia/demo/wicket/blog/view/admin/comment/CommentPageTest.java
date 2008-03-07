package fr.xebia.demo.wicket.blog.view.admin.comment;

import org.junit.BeforeClass;

import fr.xebia.demo.wicket.blog.service.CommentService;
import fr.xebia.demo.wicket.blog.view.WicketPageTest;

public abstract class CommentPageTest extends WicketPageTest {

    @BeforeClass
    public static void setUpAppContext() {
        CommentService commentService = new CommentService();
        commentService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("commentService", commentService);
    }
}
