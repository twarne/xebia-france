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
package fr.xebia.demo.wicket.blog.view.util;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;

public class ParametizedPageLink extends Link {

    private static final long serialVersionUID = 1L;
    private Class<? extends WebPage> pageClass;
    private PageParameters pageParameters;

    public ParametizedPageLink(String id, Class<? extends WebPage> pageClass, PageParameters pageParameters) {
        super(id);
        this.pageClass = pageClass;
        this.pageParameters = pageParameters;
    }

    @Override
    public void onClick() {
        setResponsePage(pageClass, pageParameters);
    }
}
