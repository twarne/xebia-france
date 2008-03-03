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
package fr.xebia.demo.wicket.blog.view;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;

public class LogoutPage extends PublicPage {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(LogoutPage.class);

    public LogoutPage(PageParameters pageParameters) {
        super(pageParameters);
        logger.debug("Clearing user login data from session");
        BlogWebSession session = (BlogWebSession) getSession();
        session.clearUser();
//        session.setRedirectToPage(null);
        throw new RestartResponseException(Application.get().getHomePage());
    }
}
