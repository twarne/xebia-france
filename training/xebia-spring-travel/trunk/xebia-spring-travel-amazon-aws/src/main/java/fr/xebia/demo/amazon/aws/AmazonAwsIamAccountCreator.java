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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.AccessKey;
import com.amazonaws.services.identitymanagement.model.AddUserToGroupRequest;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyRequest;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyResult;
import com.amazonaws.services.identitymanagement.model.CreateLoginProfileRequest;
import com.amazonaws.services.identitymanagement.model.CreateUserRequest;
import com.amazonaws.services.identitymanagement.model.CreateUserResult;
import com.amazonaws.services.identitymanagement.model.User;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

/**
 * Create Amazon IAM accounts.
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class AmazonAwsIamAccountCreator {

    public static void main(String[] args) throws Exception {
        try {
            AmazonAwsIamAccountCreator amazonAwsIamAccountCreator = new AmazonAwsIamAccountCreator();
            amazonAwsIamAccountCreator.createUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AmazonIdentityManagement iam;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @VisibleForTesting
    protected AmazonSimpleEmailService ses;

    public AmazonAwsIamAccountCreator() throws IOException {
        InputStream credentialsAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("AwsCredentials.properties");
        Preconditions.checkNotNull(credentialsAsStream, "File 'AwsCredentials.properties' NOT found in the classpath");
        AWSCredentials awsCredentials = new PropertiesCredentials(credentialsAsStream);
        iam = new AmazonIdentityManagementClient(awsCredentials);

        ses = new AmazonSimpleEmailServiceClient(awsCredentials);
    }

    /**
     * Builds difference between list of emails provided in
     * "accounts-to-create.txt" and the already created users (obtained via
     * {@link AmazonIdentityManagement#listUsers()}).
     */
    public Set<String> buildUserNamesToCreate() {
        List<String> existingUserNames = Lists.transform(iam.listUsers().getUsers(), new Function<User, String>() {
            @Override
            public String apply(User user) {
                return user.getUserName();
            }
        });

        URL emailsToVerifyURL = Thread.currentThread().getContextClassLoader().getResource("accounts-to-create.txt");
        Preconditions.checkNotNull(emailsToVerifyURL, "File 'accounts-to-create.txt' NOT found in the classpath");
        List<String> xebiaFranceEmails;
        try {
            xebiaFranceEmails = Resources.readLines(emailsToVerifyURL, Charsets.ISO_8859_1);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }

        return Sets.difference(Sets.newHashSet(xebiaFranceEmails), Sets.newHashSet(existingUserNames));

    }

    public void createUsers() {
        Set<String> userNames = buildUserNamesToCreate();
        System.out.println("Create accounts for: " + userNames);
        for (String userName : userNames) {
            createUsers(userName);

            // sleep 10 seconds to prevent "Throttling exception"
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    /**
     * Create an Amazon IAM account with a password, a secret key and member of
     * "Admins". The password, access key and secret key are sent by email.
     * 
     * @param userName
     *            valid email used as userName of the created account.
     */
    public void createUsers(String userName) {

        CreateUserRequest createUserRequest = new CreateUserRequest(userName);
        CreateUserResult createUserResult = iam.createUser(createUserRequest);
        User user = createUserResult.getUser();

        String password = RandomStringUtils.randomAlphanumeric(8);

        iam.createLoginProfile(new CreateLoginProfileRequest(user.getUserName(), password));
        iam.addUserToGroup(new AddUserToGroupRequest("Admins", user.getUserName()));
        CreateAccessKeyResult createAccessKeyResult = iam.createAccessKey(new CreateAccessKeyRequest().withUserName(user.getUserName()));
        AccessKey accessKey = createAccessKeyResult.getAccessKey();

        System.out.println("CREATED userName=" + user.getUserName() + "\tpassword=" + password + "\taccessKeyId="
                + accessKey.getAccessKeyId() + "\tsecretAccessKey=" + accessKey.getSecretAccessKey());

        String subject = "Xebia France Amazon EC2 Credentials";

        String body = "Hello,\n";
        body += "\n";
        body += "Here are the credentials to connect to Xebia Amazon AWS/EC2 training infrastructure:\n";
        body += "\n";
        body += "User Name: " + user.getUserName() + "\n";
        body += "Password: " + password + "\n";
        body += "Access Key Id: " + accessKey.getAccessKeyId() + "\n";
        body += "Secret Access Key: " + accessKey.getSecretAccessKey() + "\n";
        body += "\n";
        body += "The authentication page is https://xebia-france.signin.aws.amazon.com/console";
        body += "\n";
        body += "Don't hesitate to connect to Amazon AWS, to play with it but please DO NOT FORGET TO STOP INSTANCES OR IF POSSIBLE TERMINATE THEM AFTER USING THEM.\n";
        body += "Letting instances started would cost unnecessary money to Xebia.\n";
        body += "\n";
        body += "\n";
        body += "Thanks,\n";
        body += "\n";
        body += "Cyrille";
        try {
            sendEmail(subject, body, "example@example.org", user.getUserName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send email with Amazon Simple Email Service.
     * <p/>
     * 
     * Please note that the sender (ie 'from') must be a verified address (see
     * {@link AmazonSimpleEmailService#verifyEmailAddress(com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest)}
     * ).
     * <p/>
     * 
     * Please note that the sender is a CC of the meail to ease support.
     * <p/>
     * 
     * @param subject
     * @param body
     * @param from
     * @param toAddresses
     */

    public void sendEmail(String subject, String body, String from, String... toAddresses) {

        SendEmailRequest sendEmailRequest = new SendEmailRequest( //
                from, //
                new Destination().withToAddresses(toAddresses).withCcAddresses(from), //
                new Message(new Content(subject), //
                        new Body(new Content(body))));
        SendEmailResult sendEmailResult = ses.sendEmail(sendEmailRequest);
        System.out.println(sendEmailResult);
    }
}
