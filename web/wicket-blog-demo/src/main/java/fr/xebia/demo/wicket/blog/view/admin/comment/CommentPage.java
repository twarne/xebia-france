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
package fr.xebia.demo.wicket.blog.view.admin.comment;

import org.apache.wicket.PageParameters;

import fr.xebia.demo.wicket.blog.view.admin.BaseAdminPage;

public abstract class CommentPage extends BaseAdminPage {

    public static final String PARAM_COMMENTS_KEY = "comments";
    public static final String PARAM_COMMENT_KEY = "comment";

    public CommentPage(PageParameters pageParameters) {
        super(pageParameters);
    }

}
