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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.amazonaws.services.rds.model.DBInstance;

public class AmazonAwsPetclinicInfrastructureMakerTest {

    @Test
    @Ignore
    public void test_update_existing_load_balancer() {
        AmazonAwsPetclinicInfrastructureMaker maker = new AmazonAwsPetclinicInfrastructureMaker();

        // maker.createOrUpdateElasticLoadBalancer("/petclinic/",
        // "petclinic-tomcat");

    }

    @Test
    @Ignore
    public void test_find_dbinstances_return_null_when_not_exist() {
        AmazonAwsPetclinicInfrastructureMaker maker = new AmazonAwsPetclinicInfrastructureMaker();
        Assert.assertNull("No DB Instance must be returned", maker.findDBInstance("notexist"));
    }

    @Test
    @Ignore
    public void test_find_dbinstances() {
        AmazonAwsPetclinicInfrastructureMaker maker = new AmazonAwsPetclinicInfrastructureMaker();
        DBInstance dbInstance = maker.findDBInstance("petclinic-xeb");
        Assert.assertNotNull("No DB Instance must be returned", dbInstance);
        System.out.println("MySQL instance : " + dbInstance.getEndpoint().getAddress() + ":" + dbInstance.getEndpoint().getPort());
    }

    @Test
    @Ignore
    public void test_create_dbinstances() {
        AmazonAwsPetclinicInfrastructureMaker maker = new AmazonAwsPetclinicInfrastructureMaker();
        Assert.assertNotNull("No DB Instance available", maker.createDBInstanceAndWaitForAvailability("petclinic-xeb"));

    }

    @Test
    @Ignore
    public void test_terminate_ec2instances() {
        AmazonAwsPetclinicInfrastructureMaker maker = new AmazonAwsPetclinicInfrastructureMaker();
        maker.terminateMyAlreadyExistingEC2Instances("xeb");
    }

    @Test
    @Ignore
    public void test_create_ec2instances_and_tag() {
        AmazonAwsPetclinicInfrastructureMaker maker = new AmazonAwsPetclinicInfrastructureMaker();
        Assert.assertNotNull("No EC2 Instance created", maker.terminateExistingAndCreateNewInstance("xeb"));
    }

    @Test
    public void test_delete_elb() {
        AmazonAwsPetclinicInfrastructureMaker maker = new AmazonAwsPetclinicInfrastructureMaker();
        maker.deleteExistingElasticLoadBalancer("xeb");
    }

    @Test
    @Ignore
    public void test_create_elb() {
        AmazonAwsPetclinicInfrastructureMaker maker = new AmazonAwsPetclinicInfrastructureMaker();
        Assert.assertNotNull("No ELB Instance created", maker.createElasticLoadBalancer("xeb"));
    }

}
