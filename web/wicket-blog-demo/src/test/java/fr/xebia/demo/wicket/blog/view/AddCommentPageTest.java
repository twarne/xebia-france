package fr.xebia.demo.wicket.blog.view;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.tester.FormTester;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.xebia.demo.wicket.blog.service.CommentService;
import fr.xebia.demo.wicket.blog.service.PostService;


public class AddCommentPageTest extends HomePageTest {

    @BeforeClass
    public static void setUpAppContext() {
        PostService postService = new PostService();
        postService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("postService", postService);

        CommentService commentService = new CommentService();
        commentService.setEntityManagerFactory(entityManagerFactory);
        appContext.putBean("commentService", commentService);
    }

    @Test
    public void testRender() {
//        super.testRender();
        PageParameters pageParameters = new PageParameters();
        pageParameters.put(AddCommentPage.PARAM_POSTID_KEY, Long.valueOf(1));
        tester.startPage(AddCommentPage.class, pageParameters);
        tester.assertRenderedPage(AddCommentPage.class);
        tester.assertNoErrorMessage();

        tester.assertComponent("feedbackPanel", FeedbackPanel.class);
        tester.assertComponent("commentForm", AddCommentForm.class);
        tester.assertComponent("commentForm:author", TextField.class);
        tester.assertComponent("commentForm:email", TextField.class);
        tester.assertComponent("commentForm:postId", Label.class);
        tester.assertComponent("commentForm:content", TextArea.class);
        tester.assertComponent("commentForm:submitButton", Button.class);

        // create the form tester object, mapping to its wicket:id
        FormTester form = tester.newFormTester("commentForm");
        // set the parameters for each component in the form
        form.setValue("author", "Test");
        form.setValue("email", "none@nowhere.com");
        form.setValue("content", "Comment World !");
        // all set, submit
        form.submit();
        tester.assertNoErrorMessage();
        // check if the page is correct: in this case, I'm expecting an error to take me back to the same page
        tester.assertRenderedPage(HomePage.class);
        // if you're not expecting an error (testing for submit unsuccessful) use assertErrorMessage(String) instead
        tester.assertNoErrorMessage();
    }

}
