/*
 * Copyright 2007 Xebia and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.xebia.demo.wicket.blog.view.admin.post;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.PageLink;

import fr.xebia.demo.wicket.blog.data.Post;
import fr.xebia.demo.wicket.blog.view.util.ParametizedPageLink;

public class ViewPostPage extends PostPage {

    private static final long serialVersionUID = 1L;

    public ViewPostPage(PageParameters pageParameters) {
        super(pageParameters);
        if (pageParameters.containsKey(PARAM_POST_KEY)) {
            Post post = (Post) pageParameters.get(PARAM_POST_KEY);
            createComponents(post);
        } else {
            throw new IllegalArgumentException("Parameter 'post' is mandatory");
        }
    }

    protected void createComponents(Post post) {
        add(new Label("id", String.valueOf(post.getId())));
        add(new Label("commentStatus", String.valueOf(post.getCommentsAllowed())));
        add(new MultiLineLabel("content", String.valueOf(post.getContent())));
        add(new Label("date", String.valueOf(post.getDate())));
        add(new Label("modified", String.valueOf(post.getModified())));
        add(new Label("password", String.valueOf(post.getPassword())));
        add(new Label("pingStatus", String.valueOf(post.getPingAllowed())));
        add(new Label("author", String.valueOf(post.getAuthor())));
        add(new Label("status", String.valueOf(post.getStatus())));
        add(new Label("title", String.valueOf(post.getTitle())));
        if (post.getCategory() != null) {
            add(new Label("category", post.getCategory().getNicename()));
        } else {
            add(new Label("category", ""));
        }

        PageParameters parameters = new PageParameters();
        parameters.put(EditPostPage.PARAM_POST_KEY, post);
        add(new ParametizedPageLink("editLink", EditPostPage.class, parameters));

        add(new PageLink("backToListLink", PostListPage.class));
    }
}
