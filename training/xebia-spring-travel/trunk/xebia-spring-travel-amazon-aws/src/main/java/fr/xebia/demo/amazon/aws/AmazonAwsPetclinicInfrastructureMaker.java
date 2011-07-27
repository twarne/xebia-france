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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

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
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import fr.xebia.cloud.cloudinit.CloudInitUserDataBuilder;
import fr.xebia.cloud.cloudinit.FreemarkerUtils;

/**
 * <p>
 * Builds a java petclinic infrastructure on Amazon EC2.
 * </p>
 * <p>
 * creates:
 * <ul>
 * <li>1 MySQL database</li>
 * <li>2 Tomcat / xebia-pet-clinic servers connected to the mysql database
 * (connected via the injection of the jdbc parameters in catalina.properties
 * via cloud-init)</li>
 * <li>1 load balancer</li>
 * </ul>
 * </p>
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class AmazonAwsPetclinicInfrastructureMaker {

    public enum Distribution {
        /**
         * <a href="http://aws.amazon.com/amazon-linux-ami/">Amazon Linux
         * AMI</a>
         */
        AMZN_LINUX("ami-47cefa33", "cloud-config-redhat-5.txt", "/usr/share/tomcat6", "t1.micro"), //
        /**
         * <a href="http://cloud.ubuntu.com/ami/">Ubuntu Natty (11.04) AMI</a>
         */
        UBUNTU_11_04("ami-359ea941", "cloud-config-ubuntu-11.04.txt", "/var/lib/tomcat6", "m1.small"), //
        /**
         * <a href="http://cloud.ubuntu.com/ami/">Ubuntu Oneiric (11.10) AMI</a>
         */
        UBUNTU_11_10("ami-0aa7967e", "cloud-config-ubuntu-11.10.txt", "/var/lib/tomcat7", "m1.small");

        private final static Map<String, Distribution> DISTRIBUTIONS_BY_AMI_ID = Maps.uniqueIndex(Arrays.asList(Distribution.values()),
                new Function<Distribution, String>() {
                    @Override
                    public String apply(Distribution distribution) {
                        return distribution.getAmiId();
                    }
                });

        @Nonnull
        public static Distribution fromAmiId(@Nonnull String amiId) throws NullPointerException, IllegalArgumentException {
            Distribution distribution = DISTRIBUTIONS_BY_AMI_ID.get(Preconditions.checkNotNull(amiId, "amiId is null"));
            Preconditions.checkArgument(distribution != null, "No distribution found for amiId '%s'", amiId);
            return distribution;
        }

        private final String amiId;

        private final String catalinaBase;

        private final String cloudConfigFilePath;

        private final String instanceType;

        private Distribution(String amiId, String cloudConfigFilePath, String catalinaBase, String instanceType) {
            this.amiId = amiId;
            this.cloudConfigFilePath = cloudConfigFilePath;
            this.catalinaBase = catalinaBase;
            this.instanceType = instanceType;
        }

        /**
         * ID of the AMI in the eu-west-1 region.
         */
        public String getAmiId() {
            return amiId;
        }

        /**
         * <p>
         * "catalina_base" folder.
         * </p>
         * <p>
         * Differs between redhat/ubuntu and between versions.
         * </p>
         * <p>
         * e.g."/var/lib/tomcat7", "/usr/share/tomcat6".
         * </p>
         */
        public String getCatalinaBase() {
            return catalinaBase;
        }

        /**
         * <p>
         * Classpath relative path to the "cloud-config" file.
         * </p>
         * <p>
         * "cloud-config" files differ between distributions due to the
         * different name of the packages and the different versions available.
         * </p>
         * <p>
         * e.g."cloud-config-ubuntu-10.04.txt", "cloud-config-redhat-5.txt".
         * </p>
         */
        public String getCloudConfigFilePath() {
            return cloudConfigFilePath;
        }

        public String getInstanceType() {
            return instanceType;
        }

    }

    protected final static String AMI_CUSTOM_LINUX_SUN_JDK6_TOMCAT7 = "ami-44506030";

    public static void main(String[] args) throws Exception {
        AmazonAwsPetclinicInfrastructureMaker infrastructureMaker = new AmazonAwsPetclinicInfrastructureMaker();
        infrastructureMaker.createInfrastructure(Distribution.AMZN_LINUX, Distribution.UBUNTU_11_10, Distribution.UBUNTU_11_04);

    }

    private AmazonEC2 ec2;

    private AmazonElasticLoadBalancing elb;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private AmazonRDS rds;

    public AmazonAwsPetclinicInfrastructureMaker() {
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

    public DBInstance awaitForDbInstanceCreation(DBInstance dbInstance) {
        System.out.println("Get Instance " + dbInstance.getDBInstanceIdentifier() + "/" + dbInstance.getDBName() + " status");

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

    /**
     * Returns a base-64 version of the mime-multi-part user-data file.
     * 
     * @param distribution
     * @param dbInstance
     * @param jdbcUsername
     * @param jdbcPassword
     * @param warUrl
     * @return
     */
    protected String buildUserData(Distribution distribution, DBInstance dbInstance, String jdbcUsername, String jdbcPassword,
            String warUrl, String warFileName) {

        // USER DATA SHELL SCRIPT
        Map<String, Object> rootMap = Maps.newHashMap();
        rootMap.put("catalinaBase", distribution.getCatalinaBase());
        rootMap.put("warUrl", warUrl);
        rootMap.put("warName", warFileName);

        Map<String, String> systemProperties = Maps.newHashMap();
        rootMap.put("systemProperties", systemProperties);
        String jdbcUrl = "jdbc:mysql://" + dbInstance.getEndpoint().getAddress() + ":" + dbInstance.getEndpoint().getPort() + "/"
                + dbInstance.getDBName();
        systemProperties.put("jdbc.url", jdbcUrl);
        systemProperties.put("jdbc.username", jdbcUsername);
        systemProperties.put("jdbc.password", jdbcPassword);

        String shellScript = FreemarkerUtils.generate(rootMap, "/provision_tomcat.py.fmt");

        // USER DATA CLOUD CONFIG
        InputStream cloudConfigAsStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(distribution.getCloudConfigFilePath());
        Preconditions.checkNotNull(cloudConfigAsStream, "'" + distribution.getCloudConfigFilePath() + "' not found in path");
        Readable cloudConfig = new InputStreamReader(cloudConfigAsStream);

        return CloudInitUserDataBuilder.start() //
                .addShellScript(shellScript) //
                .addCloudConfig(cloudConfig) //
                .buildBase64UserData();

    }

    public void createAmznLinuxBasedInfrastructure() {
        createInfrastructure(Distribution.AMZN_LINUX);
    }

    public DBInstance createDatabaseInstance(String jdbcUserName, String jdbcPassword) {
        CreateDBInstanceRequest createDBInstanceRequest = new CreateDBInstanceRequest() //
                .withDBInstanceIdentifier("petclinic") //
                .withDBName("petclinic") //
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
                .withLoadBalancerName("petclinic") //
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
                .withPolicyName("petclinic-stickiness-policy");
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

    void createInfrastructure(Distribution... distributions) {
        String jdbcUsername = "petclinic";
        String jdbcPassword = "petclinic";
        String warUrl = "http://xebia-france.googlecode.com/svn/repository/maven2/fr/xebia/demo/xebia-petclinic/1.0.0/xebia-petclinic-1.0.0.war";
        String warFileName = "petclinic.war";

        DBInstance dbInstance = createDatabaseInstance(jdbcUsername, jdbcPassword);
        // DBInstance dbInstance = new DBInstance().withDBInstanceIdentifier("petclinic");
        dbInstance = awaitForDbInstanceCreation(dbInstance);
        System.out.println("MySQL instance: " + dbInstance);

        List<Instance> petclinicInstances = createPetclinicTomcatServers(dbInstance, jdbcUsername, jdbcPassword, warUrl, warFileName,
                distributions);
        System.out.println("EC2 instances: " + petclinicInstances);
        CreateLoadBalancerResult createLoadBalancerResult = createElasticLoadBalancer(petclinicInstances);
        System.out.println("Load Balancer DNS name: " + createLoadBalancerResult.getDNSName());
    }

    public List<Instance> createPetclinicTomcatServers(DBInstance dbInstance, String jdbcUsername, String jdbcPassword, String warUrl,
            String warFileName, Distribution... distributions) {

        List<Instance> instances = Lists.newArrayList();
        for (Distribution distribution : distributions) {
            String userData = buildUserData(distribution, dbInstance, jdbcUsername, jdbcPassword, warUrl, warFileName);

            // CREATE EC2 INSTANCES
            RunInstancesRequest runInstancesRequest = new RunInstancesRequest() //
                    .withInstanceType(distribution.getInstanceType()) //
                    .withImageId(distribution.getAmiId()) //
                    .withMinCount(1) //
                    .withMaxCount(1) //
                    .withSecurityGroupIds("tomcat") //
                    .withPlacement(new Placement(dbInstance.getAvailabilityZone())) //
                    .withKeyName("xebia-france") //
                    .withUserData(userData) //

            ;
            RunInstancesResult runInstances = ec2.runInstances(runInstancesRequest);
            instances.addAll(runInstances.getReservation().getInstances());
        }

        // TAG EC2 INSTANCES
        int idx = 1;
        for (Instance instance : instances) {
            CreateTagsRequest createTagsRequest = new CreateTagsRequest();
            createTagsRequest.withResources(instance.getInstanceId()) //
                    .withTags(//
                            new Tag("Name", "petclinic-" + idx), //
                            new Tag("Type", Distribution.fromAmiId(instance.getImageId()).name().toLowerCase()));
            ec2.createTags(createTagsRequest);

            idx++;
        }

        logger.info("Created {}", instances);

        return instances;
    }

    public void createUbuntuOneiricBasedInfrastructure() {
        createInfrastructure(Distribution.UBUNTU_11_10);
    }

    public void listDbInstances() {
        DescribeDBInstancesResult describeDBInstancesResult = rds.describeDBInstances();
        System.out.println(describeDBInstancesResult);
        for (DBInstance dbInstance : describeDBInstancesResult.getDBInstances()) {
            System.out.println(dbInstance);
        }
    }
}
