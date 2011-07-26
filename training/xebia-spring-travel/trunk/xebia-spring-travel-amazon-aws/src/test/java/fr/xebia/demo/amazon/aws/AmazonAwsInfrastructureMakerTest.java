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

import static org.junit.Assert.*;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.Endpoint;

import fr.xebia.demo.amazon.aws.AmazonAwsInfrastructureMaker.Distribution;

public class AmazonAwsInfrastructureMakerTest {

    @Test
    public void test_generate_ubuntu_10_10_user_data() {
        test_generate_user_data(Distribution.UBUNTU_10_10);
    }

    @Test
    public void test_generate_ubuntu_10_04_user_data() {
        test_generate_user_data(Distribution.UBUNTU_10_04);
    }

    @Test
    public void test_generate_amzn_linux_user_data() {
        test_generate_user_data(Distribution.AMZN_LINUX);
    }

    void test_generate_user_data(Distribution distribution) {
        AmazonAwsInfrastructureMaker maker = new AmazonAwsInfrastructureMaker();
        DBInstance dbInstance = new DBInstance() //
                .withEndpoint(new Endpoint() //
                        .withAddress("my-db-host") //
                        .withPort(3306) //
                ) //
                .withMasterUsername("travel");

        String userData = maker.buildUserData(distribution, dbInstance, "travel", "travel",
                "http://example.com/the/path/to/my/test-war-1.2.3.war");

        System.out.println(distribution);
        System.out.println(new String(Base64.decodeBase64(userData)));
    }
}
