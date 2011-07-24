/*
 * Copyright 2008-2010 Xebia and the original author or authors.
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
package fr.xebia.demo.amazon.aws;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class CloudInitFileBuilder {

    protected String buildUserData() {
        try {
            Thread.currentThread().getContextClassLoader().getResourceAsStream("provision_tomcat.py");
            byte[] bytes = ByteStreams.toByteArray(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("provision_tomcat.py"));

            MimeBodyPart userDataScriptPart = new MimeBodyPart();
            userDataScriptPart.setText(new String(bytes), "us-ascii", "x-shellscript");
            userDataScriptPart.setFileName("provision_tomcat.py");
            MimeMultipart userData = new MimeMultipart();
            userData.addBodyPart(userDataScriptPart);

            MimeMessage msg = new MimeMessage(Session.getDefaultInstance(new Properties()));
            msg.setContent(userData);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            msg.writeTo(baos);
            return new String(baos.toByteArray());
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public void buildPythonScript(Map<String, String> systemProperties) throws Exception {
        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(this.getClass(), "/");

        Map<String, Object> root = Maps.newHashMap();
        root.put("systemProperties", systemProperties);
        Template template = cfg.getTemplate("provision_tomcat.py.ftl");
        Writer out = new OutputStreamWriter(System.out);
        template.process(root, out);
        out.flush();

    }
}
