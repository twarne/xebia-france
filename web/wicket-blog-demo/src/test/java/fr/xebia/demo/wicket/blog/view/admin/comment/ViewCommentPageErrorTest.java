package fr.xebia.demo.wicket.blog.view.admin.comment;

import java.io.Serializable;
import java.util.Date;

import org.apache.wicket.markup.html.link.Link;
import org.junit.Before;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.data.Comment;
import fr.xebia.demo.wicket.blog.service.CommentService;
import fr.xebia.demo.wicket.blog.service.ServiceException;
import fr.xebia.demo.wicket.blog.view.WicketPageTest;

public class ViewCommentPageErrorTest extends WicketPageTest {

    @Before
    public void setUpAppContext() throws ServiceException {
        CommentService commentService = getCommentService();
        commentService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("commentService", commentService);

        Comment comment = new Comment();
        comment.setApproved(Boolean.TRUE);
        comment.setAuthor("Me");
        comment.setDate(new Date());
        comment.setEmail("none@nowhere.com");
        comment.setPostId(new Long(1));
        comment.setContent("Fake Content");
        commentService.save(comment);
    }

    protected CommentService getCommentService() {
        CommentService commentService = new CommentService() {
            @Override
            public Comment get(Serializable id) throws ServiceException {
                throw new ServiceException(ERROR_MESSAGE);
            }
        };
        return commentService;
    }

    @Test
    public void testErrorRender() {
        tester.startPage(CommentListPage.class);
        tester.assertRenderedPage(CommentListPage.class);
        tester.assertNoErrorMessage();
        tester.assertComponent("comments:0:viewLink", Link.class);
        tester.clickLink("comments:0:viewLink");
        tester.assertRenderedPage(CommentListPage.class);
        tester.assertErrorMessages(new String[]{ERROR_MESSAGE});
    }
}
