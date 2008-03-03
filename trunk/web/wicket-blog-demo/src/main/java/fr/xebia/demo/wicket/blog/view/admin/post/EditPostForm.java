package fr.xebia.demo.wicket.blog.view.admin.post;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;

import fr.xebia.demo.wicket.blog.data.Post;
import fr.xebia.demo.wicket.blog.view.util.PageParametersUtils;

public class EditPostForm extends AddPostForm {

    private static final long serialVersionUID = 1L;
    
    private static final Logger logger = Logger.getLogger(EditPostForm.class);

    public EditPostForm(String id, Post post) {
        super(id, post);
        createComponents();
    }

    private void createComponents() {
        add(new Label("idValue", String.valueOf(post.getId())));
    }

    @Override
    public void onSubmit() {
        updatePost(post);
    }

    private void updatePost(Post post) {
        try {
            post.setModified(new Date());
            logger.debug("Updating post: " + post);
            Post updatedPost = postService.update(post);
            setResponsePage(PostListPage.class, PageParametersUtils.fromStringMessage("Updated post: " + updatedPost));
        } catch (Exception e) {
            logger.error("Error while updating post", e);
            PageParameters parameters = PageParametersUtils.fromException(e);
            parameters.put(EditPostPage.PARAM_POST_KEY, post);
            throw new RestartResponseException(EditPostPage.class, parameters);
        }
    }
}
