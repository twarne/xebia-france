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

import java.text.MessageFormat;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.apache.wicket.Session;

public class LocalizerUtils {

    private static final Logger logger = Logger.getLogger(LocalizerUtils.class);

    public static String getString(Component component, String key, Object... parameters) {
        String message = getLocalizer(component).getString(key, component);
        if (parameters != null && parameters.length != 0) {
            for (int i = 0; i < parameters.length; i++) {
                if (!(parameters[i] instanceof String)) {
                    parameters[i] = String.valueOf(parameters[i]);
                }
            }
            // Apply the parameters
            try {
                final MessageFormat format = new MessageFormat(message, getLocale(component));
                message = format.format(parameters);
            } catch (RuntimeException e) {
                logger.warn("Can't get i18n resource for key: " + key, e);
                return '%' + key + '%';
            }
        }
        return message;
    }

    private static Locale getLocale(Component component) {
        if (component != null) {
            component.getLocale();
        } else {
            Session.get().getLocale();
        }
        return null;
    }

    private static Localizer getLocalizer(Component component) {
        Localizer localizer = Application.get().getResourceSettings().getLocalizer();
        if (localizer == null) {
            if (component != null) {
                localizer = component.getLocalizer();
            } else {
                throw new IllegalStateException("No localizer has been set");
            }
        }
        return localizer;
    }
}
