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
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLBCookieStickinessPolicyRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.HealthCheck;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.SetLoadBalancerPoliciesOfListenerRequest;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class AmazonAwsInfrastructureMaker {

    public static void main(String[] args) throws Exception {
        AmazonAwsInfrastructureMaker infrastructureMaker = new AmazonAwsInfrastructureMaker();
        infrastructureMaker.createAll();

    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private AmazonEC2 ec2;

    private AmazonRDS rds;

    private AmazonIdentityManagement iam;

    private AmazonElasticLoadBalancing elb;

    public void createAll() {
        DBInstance dbInstance = createDatabaseInstance();
        dbInstance = awaitForDbInstanceCreation(dbInstance);
        System.out.println(dbInstance);
        List<Instance> travelEcommerceInstances = createTravelEcommerceTomcatServers(dbInstance);
        CreateLoadBalancerResult createLoadBalancerResult = createElasticLoadBalancer(travelEcommerceInstances);
        System.out.println("Load Balancer DNS name: " + createLoadBalancerResult.getDNSName());
    }

    public AmazonAwsInfrastructureMaker() throws IOException {
        InputStream credentialsAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("AwsCredentials.properties");
        Preconditions.checkNotNull(credentialsAsStream, "File 'AwsCredentials.properties' NOT found in the classpath");
        AWSCredentials credentials = new PropertiesCredentials(credentialsAsStream);
        ec2 = new AmazonEC2Client(credentials);
        ec2.setEndpoint("ec2.eu-west-1.amazonaws.com");
        rds = new AmazonRDSClient(credentials);
        rds.setEndpoint("rds.eu-west-1.amazonaws.com");
        elb = new AmazonElasticLoadBalancingClient(credentials);
        elb.setEndpoint("elasticloadbalancing.eu-west-1.amazonaws.com");
        
        iam = new AmazonIdentityManagementClient(credentials);
    }

    public void listDbInstances() {
        DescribeDBInstancesResult describeDBInstancesResult = rds.describeDBInstances();
        System.out.println(describeDBInstancesResult);
        for (DBInstance dbInstance : describeDBInstancesResult.getDBInstances()) {
            System.out.println(dbInstance);
        }
    }

    public DBInstance awaitForDbInstanceCreation(DBInstance dbInstance) {
        int counter = 0;
        while (!"available".equals(dbInstance.getDBInstanceStatus())) {
            if (counter > 0) {
                try {
                    Thread.sleep(20 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            DescribeDBInstancesRequest describeDbInstanceRequest = new DescribeDBInstancesRequest().withDBInstanceIdentifier(dbInstance
                    .getDBInstanceIdentifier());
            DescribeDBInstancesResult describeDBInstancesResult = rds.describeDBInstances(describeDbInstanceRequest);
            List<DBInstance> dbInstances = describeDBInstancesResult.getDBInstances();
            Preconditions.checkState(dbInstances.size() == 1, "Exactly 1 db instance expected : %S", dbInstances);
            dbInstance = Iterables.getFirst(dbInstances, null);

        }
        return dbInstance;
    }

    public DBInstance createDatabaseInstance() {
        CreateDBInstanceRequest createDBInstanceRequest = new CreateDBInstanceRequest() //
                .withDBInstanceIdentifier("travel") //
                .withDBName("travel") //
                .withEngine("MySQL") //
                .withEngineVersion("5.1.57") //
                .withDBInstanceClass("db.m1.small") //
                .withMasterUsername("root") //
                .withMasterUserPassword("root") //
                .withAllocatedStorage(5) //
                .withBackupRetentionPeriod(0) //
                .withDBSecurityGroups("default") //
                .withLicenseModel("general-public-license") //
        ;

        DBInstance dbInstance = rds.createDBInstance(createDBInstanceRequest);
        logger.info("Created {}", dbInstance);
        return dbInstance;
    }

    public List<Instance> createTravelEcommerceTomcatServers(DBInstance dbInstance) {

        String jdbcUrl = "jdbc:mysql://" + dbInstance.getEndpoint().getAddress() + ":" + dbInstance.getEndpoint().getPort() + "/"
                + dbInstance.getDBName();

        // CREATE EC2 INSTANCES
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest() //
                .withInstanceType("t1.micro") //
                .withImageId("ami-62201116") //
                .withMinCount(2) //
                .withMaxCount(2) //
                .withSecurityGroupIds("tomcat") //
                .withPlacement(new Placement(dbInstance.getAvailabilityZone())) //
                .withKeyName("xebia-france") //
                .withUserData(Base64.encodeBase64String(jdbcUrl.getBytes())) //

        ;

        RunInstancesResult runInstances = ec2.runInstances(runInstancesRequest);

        // TAG EC2 INSTANCES
        List<Instance> instances = runInstances.getReservation().getInstances();
        int idx = 1;
        for (Instance instance : instances) {
            CreateTagsRequest createTagsRequest = new CreateTagsRequest();
            createTagsRequest.withResources(instance.getInstanceId()) //
                    .withTags(new Tag("Name", "travel-ecommerce-" + idx));
            ec2.createTags(createTagsRequest);

            idx++;
        }

        logger.info("Created {}", instances);

        return instances;
    }

    public CreateLoadBalancerResult createElasticLoadBalancer(List<Instance> ec2Instances) {
        Set<String> availabilityZones = Sets.newHashSet(Lists.transform(ec2Instances, new Function<Instance, String>() {
            @Override
            public String apply(Instance instance) {
                return instance.getPlacement().getAvailabilityZone();
            }
        }));

        List<com.amazonaws.services.elasticloadbalancing.model.Instance> elbInstances = Lists.transform(ec2Instances,
                new Function<Instance, com.amazonaws.services.elasticloadbalancing.model.Instance>() {
                    @Override
                    public com.amazonaws.services.elasticloadbalancing.model.Instance apply(Instance ec2Instance) {
                        return new com.amazonaws.services.elasticloadbalancing.model.Instance(ec2Instance.getInstanceId());
                    }
                });

        CreateLoadBalancerRequest createLoadBalancerRequest = new CreateLoadBalancerRequest() //
                .withLoadBalancerName("travel-ecommerce") //
                .withListeners(new Listener("HTTP", 80, 8080)) //
                .withAvailabilityZones(availabilityZones) //
        ;
        CreateLoadBalancerResult createLoadBalancerResult = elb.createLoadBalancer(createLoadBalancerRequest);

        // HEALTH CHECK
        HealthCheck helsathCheck = new HealthCheck() //
                .withTarget("HTTP:8080/") //
                .withHealthyThreshold(2) //
                .withUnhealthyThreshold(2) //
                .withInterval(30) //
                .withTimeout(2);
        ConfigureHealthCheckRequest configureHealthCheckRequest = new ConfigureHealthCheckRequest(
                createLoadBalancerRequest.getLoadBalancerName(), //
                helsathCheck);
        elb.configureHealthCheck(configureHealthCheckRequest);

        // COOKIE STICKINESS
        CreateLBCookieStickinessPolicyRequest createLbCookieStickinessPolicy = new CreateLBCookieStickinessPolicyRequest() //
                .withLoadBalancerName(createLoadBalancerRequest.getLoadBalancerName())//
                .withPolicyName("travel-ecommerce-stickiness-policy");
        elb.createLBCookieStickinessPolicy(createLbCookieStickinessPolicy);

        SetLoadBalancerPoliciesOfListenerRequest setLoadBalancerPoliciesOfListenerRequest = new SetLoadBalancerPoliciesOfListenerRequest() //
                .withLoadBalancerName(createLoadBalancerRequest.getLoadBalancerName()) //
                .withLoadBalancerPort(80) //
                .withPolicyNames(createLbCookieStickinessPolicy.getPolicyName())//
        ;
        elb.setLoadBalancerPoliciesOfListener(setLoadBalancerPoliciesOfListenerRequest);

        // INSTANCES
        RegisterInstancesWithLoadBalancerRequest registerInstancesWithLoadBalancerRequest = new RegisterInstancesWithLoadBalancerRequest(
                createLoadBalancerRequest.getLoadBalancerName(), elbInstances);
        elb.registerInstancesWithLoadBalancer(registerInstancesWithLoadBalancerRequest);

        logger.info("Created {}", createLoadBalancerResult);

        return createLoadBalancerResult;
    }
}
