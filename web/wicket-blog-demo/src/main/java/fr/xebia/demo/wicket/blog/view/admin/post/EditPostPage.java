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
import org.apache.wicket.markup.html.link.PageLink;

import fr.xebia.demo.wicket.blog.data.Post;

public class EditPostPage extends PostPage {

    private static final long serialVersionUID = 1L;

    public EditPostPage(PageParameters pageParameters) {
         super(pageParameters);
         if (pageParameters.containsKey(PARAM_POST_KEY)) {
             Post post = (Post) pageParameters.get(PARAM_POST_KEY);
             createComponents(post);
         } else {
             throw new IllegalArgumentException("Parameter 'post' is mandatory");
         }
    }

    private void createComponents(final Post post) {
        add(new EditPostForm("postForm", post));
        add(new PageLink("backToListLink", PostListPage.class));
    }
}
