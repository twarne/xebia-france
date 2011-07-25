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
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.amazonaws.services.rds.model.Endpoint;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import fr.xebia.cloud.cloudinit.CloudInitUserDataBuilder;
import fr.xebia.cloud.cloudinit.FreemarkerUtils;

public class AmazonAwsInfrastructureMaker {

    /**
     * see <a href="http://cloud.ubuntu.com/ami/">Ubuntu Cloud Portal - AMI
     * Locator</a>
     */
    protected final static String AMI_UBUNTU_ONEIRIC_I386_EBS_EU_WEST_1 = "ami-0aa7967e";

    protected final static String AMI_CUSTOM_LINUX_SUN_JDK6_TOMCAT7 = "ami-44506030";

    public static void main(String[] args) throws Exception {
        AmazonAwsInfrastructureMaker infrastructureMaker = new AmazonAwsInfrastructureMaker();
        infrastructureMaker.createAll();

    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private AmazonEC2 ec2;

    private AmazonRDS rds;

    private AmazonElasticLoadBalancing elb;

    public void createAll() {
        String jdbcUsername = "travel";
        String jdbcPassword = "travel";
        String warUrl = "http://mirrors.ibiblio.org/pub/mirrors/maven2/org/eclipse/jetty/tests/test-webapp-rfc2616/7.0.2.RC0/test-webapp-rfc2616-7.0.2.RC0.war";

        // DBInstance dbInstance = createDatabaseInstance(jdbcUsername,
        // jdbcPassword);
        DBInstance dbInstance = new DBInstance() //
                .withAvailabilityZone("eu-west-1c") //
                .withEndpoint(new Endpoint().withAddress("travel.cccb4ickfoh9.eu-west-1.rds.amazonaws.com").withPort(3306)) //
        ;

        // dbInstance = awaitForDbInstanceCreation(dbInstance);
        System.out.println(dbInstance);
        List<Instance> travelEcommerceInstances = createTravelEcommerceTomcatServers(dbInstance, jdbcUsername, jdbcPassword, warUrl);
        // CreateLoadBalancerResult createLoadBalancerResult = createElasticLoadBalancer(travelEcommerceInstances);
        // System.out.println("Load Balancer DNS name: " + createLoadBalancerResult.getDNSName());
    }

    public AmazonAwsInfrastructureMaker() {
        try {
            InputStream credentialsAsStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("AwsCredentials.properties");
            Preconditions.checkNotNull(credentialsAsStream, "File 'AwsCredentials.properties' NOT found in the classpath");
            AWSCredentials credentials = new PropertiesCredentials(credentialsAsStream);
            ec2 = new AmazonEC2Client(credentials);
            ec2.setEndpoint("ec2.eu-west-1.amazonaws.com");
            rds = new AmazonRDSClient(credentials);
            rds.setEndpoint("rds.eu-west-1.amazonaws.com");
            elb = new AmazonElasticLoadBalancingClient(credentials);
            elb.setEndpoint("elasticloadbalancing.eu-west-1.amazonaws.com");
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
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
                System.out
                        .println("Instance " + dbInstance.getDBInstanceIdentifier() + "/" + dbInstance.getDBName() + " not yet available");
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

    public DBInstance createDatabaseInstance(String jdbcUserName, String jdbcPassword) {
        CreateDBInstanceRequest createDBInstanceRequest = new CreateDBInstanceRequest() //
                .withDBInstanceIdentifier("travel") //
                .withDBName("travel") //
                .withEngine("MySQL") //
                .withEngineVersion("5.1.57") //
                .withDBInstanceClass("db.m1.small") //
                .withMasterUsername(jdbcUserName) //
                .withMasterUserPassword(jdbcPassword) //
                .withAllocatedStorage(5) //
                .withBackupRetentionPeriod(0) //
                .withDBSecurityGroups("default") //
                .withLicenseModel("general-public-license") //
        ;

        DBInstance dbInstance = rds.createDBInstance(createDBInstanceRequest);
        logger.info("Created {}", dbInstance);
        return dbInstance;
    }

    protected String buildUserData(DBInstance dbInstance, String jdbcUsername, String jdbcPassword, String warUrl) {

        // USER DATA SHELL SCRIPT
        Map<String, Object> rootMap = Maps.newHashMap();
        Map<String, String> systemProperties = Maps.newHashMap();
        String jdbcUrl = "jdbc:mysql://" + dbInstance.getEndpoint().getAddress() + ":" + dbInstance.getEndpoint().getPort() + "/"
                + dbInstance.getDBName();
        systemProperties.put("jdbc.url", jdbcUrl);
        systemProperties.put("jdbc.username", jdbcUsername);
        systemProperties.put("jdbc.password", jdbcPassword);

        rootMap.put("systemProperties", systemProperties);
        rootMap.put("warUrl", warUrl);
        String warName = Iterables.getLast(Splitter.on("/").split(warUrl));
        rootMap.put("warName", warName);
        rootMap.put("warUrl", warUrl);
        String shellScript = FreemarkerUtils.generate(rootMap, "/provision_tomcat.py.fmt");

        // USER DATA CLOUD CONFIG
        InputStream cloudConfigAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("cloud-config.txt");
        Preconditions.checkNotNull(cloudConfigAsStream, "'cloud-config.txt' not found in path");
        Readable cloudConfig = new InputStreamReader(cloudConfigAsStream);

        return CloudInitUserDataBuilder.start() //
                .addShellScript(shellScript) //
                .addCloudConfig(cloudConfig) //
                .buildBase64UserData();

    }

    public List<Instance> createTravelEcommerceTomcatServers(DBInstance dbInstance, String jdbcUsername, String jdbcPassword, String warUrl) {

        String userData = buildUserData(dbInstance, jdbcUsername, jdbcPassword, warUrl);

        // CREATE EC2 INSTANCES
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest() //
                .withInstanceType("m1.small") //
                .withImageId(AMI_UBUNTU_ONEIRIC_I386_EBS_EU_WEST_1) //
                .withMinCount(1) //
                .withMaxCount(1) //
                .withSecurityGroupIds("tomcat") //
                .withPlacement(new Placement(dbInstance.getAvailabilityZone())) //
                .withKeyName("xebia-france") //
                .withUserData(userData) //

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
