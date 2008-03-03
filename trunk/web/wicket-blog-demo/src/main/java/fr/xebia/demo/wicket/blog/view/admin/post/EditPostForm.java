package fr.xebia.demo.wicket.blog.view.admin.post;

import java.util.Date;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;

import fr.xebia.demo.wicket.blog.data.Post;
import fr.xebia.demo.wicket.blog.view.util.PageParametersUtils;

public class EditPostForm extends AddPostForm {

    private static final long serialVersionUID = 1L;

    public EditPostForm(String id, Post post) {
        super(id, post);
    }

    @Override
    protected void createComponents() {
        add(new Label("idValue", String.valueOf(post.getId())));
        super.createComponents();
    }

    @Override
    public void onSubmit() {
        updatePost(post);
    }

    protected void updatePost(Post post) {
        try {
            post.setModified(new Date());
            logger.debug("Updating post: " + post);
            post = postService.update(post);
            setResponsePage(PostListPage.class, PageParametersUtils.fromStringMessage("Updated post: " + post));
        } catch (Exception e) {
            PageParameters parameters = PageParametersUtils.fromException(e);
            parameters.put(EditPostPage.PARAM_POST_KEY, post);
            throw new RestartResponseException(EditPostPage.class, parameters);
        }
    }
}
