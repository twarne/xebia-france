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
package fr.xebia.cloud.cloudinit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.codec.binary.Base64;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;

/**
 * Build a <a href="https://help.ubuntu.com/community/CloudInit">CloudInit</a>
 * UserData file.
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class CloudInitUserDataBuilder {

    public enum FileType {
        CLOUD_BOOTBOOK("text/cloud-boothook", "cloudinit-cloud-boothook.txt"), //
        CLOUD_CONFIG("text/cloud-config", "cloudinit-cloud-config.txt"), //
        INCLUDE_URL("text/x-include-url", "cloudinit-x-include-url.txt"), //
        PART_HANDLER("text/part-handler", "cloudinit-part-handler.txt"), //
        SHELL_SCRIPT("text/x-shellscript", "cloudinit-userdata-script.txt"), //
        UPSTART_JOB("text/upstart-job", "cloudinit-upstart-job.txt");

        private final String fileName;
        private final String mimeType;

        private FileType(String mimeType, String fileName) {
            this.mimeType = mimeType;
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }

        /**
         * e.g. "cloud-config" for "text/cloud-config"
         */
        public String getMimeTextSubType() {
            return getMimeType().substring("text/".length());
        }

        /**
         * e.g. "text/cloud-config"
         */
        public String getMimeType() {
            return mimeType;
        }

        @Override
        public String toString() {
            return name() + "[" + mimeType + "]";
        }
    }

    public static CloudInitUserDataBuilder start() {
        return new CloudInitUserDataBuilder(Charsets.UTF_8);
    }

    public static CloudInitUserDataBuilder start(String charset) {
        return new CloudInitUserDataBuilder(Charset.forName(charset));
    }

    private Set<FileType> alreadyAddedFileTypes = Sets.newHashSet();

    private Charset charset;

    private final MimeMessage userDataMimeMessage;

    private final MimeMultipart userDataMultipart;

    private CloudInitUserDataBuilder(Charset charset) {
        super();
        userDataMimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));
        userDataMultipart = new MimeMultipart();
        try {
            userDataMimeMessage.setContent(userDataMultipart);
        } catch (MessagingException e) {
            throw Throwables.propagate(e);
        }
        this.charset = Preconditions.checkNotNull(charset, "'charset' can NOT be null");
    }

    public CloudInitUserDataBuilder addBootHook(Readable in) {
        return addFile(FileType.CLOUD_BOOTBOOK, in);
    }

    public CloudInitUserDataBuilder addCloudConfig(Readable cloudConfig) {
        return addFile(FileType.CLOUD_CONFIG, cloudConfig);
    }

    public CloudInitUserDataBuilder addCloudConfig(String cloudConfig) {
        return addCloudConfig(new StringReader(cloudConfig));
    }

    @Nonnull
    public CloudInitUserDataBuilder addFile(@Nonnull FileType fileType, @Nonnull Readable in) {
        Preconditions.checkNotNull(fileType, "'fileType' can NOT be null");
        Preconditions.checkNotNull(in, "'in' can NOT be null");
        Preconditions.checkArgument(!alreadyAddedFileTypes.contains(fileType), "%s as already been added", fileType);
        alreadyAddedFileTypes.add(fileType);

        try {
            StringWriter sw = new StringWriter();
            CharStreams.copy(in, sw);
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setText(sw.toString(), charset.name(), fileType.getMimeTextSubType());
            mimeBodyPart.setFileName(fileType.getFileName());
            userDataMultipart.addBodyPart(mimeBodyPart);

        } catch (IOException e) {
            throw Throwables.propagate(e);
        } catch (MessagingException e) {
            throw Throwables.propagate(e);
        }
        return this;
    }

    @Nonnull
    public CloudInitUserDataBuilder addIncludeUrl(@Nonnull Readable includeUrl) {
        return addFile(FileType.INCLUDE_URL, includeUrl);
    }

    @Nonnull
    public CloudInitUserDataBuilder addIncludeUrl(@Nonnull String includeUrl) {
        return addIncludeUrl(new StringReader(includeUrl));
    }

    @Nonnull
    public CloudInitUserDataBuilder addPartHandler(@Nonnull Readable partHandler) {
        return addFile(FileType.PART_HANDLER, partHandler);
    }

    @Nonnull
    public CloudInitUserDataBuilder addPartHandler(@Nonnull String partHandler) {
        return addPartHandler(new StringReader(partHandler));
    }

    @Nonnull
    public CloudInitUserDataBuilder addShellScript(@Nonnull Readable shellScript) {
        return addFile(FileType.SHELL_SCRIPT, shellScript);
    }

    @Nonnull
    public CloudInitUserDataBuilder addShellScript(@Nonnull String shellScript) {
        return addShellScript(new StringReader(shellScript));
    }

    @Nonnull
    public CloudInitUserDataBuilder addUpstartJob(@Nonnull Readable in) {
        return addFile(FileType.UPSTART_JOB, in);
    }

    @Nonnull
    public CloudInitUserDataBuilder addUpstartJob(@Nonnull String upstartJob) {
        return addUpstartJob(new StringReader(upstartJob));
    }

    @Nonnull
    public String buildUserData() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            userDataMimeMessage.writeTo(baos);
            return new String(baos.toByteArray(), this.charset);

        } catch (MessagingException e) {
            throw Throwables.propagate(e);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
    
    @Nonnull
    public String buildBase64UserData(){
        return Base64.encodeBase64String(buildUserData().getBytes());
    }
}
