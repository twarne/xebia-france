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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.AppCookieStickinessPolicy;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLBCookieStickinessPolicyRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerPolicyRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.DisableAvailabilityZonesForLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.EnableAvailabilityZonesForLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.HealthCheck;
import com.amazonaws.services.elasticloadbalancing.model.LBCookieStickinessPolicy;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerNotFoundException;
import com.amazonaws.services.elasticloadbalancing.model.Policies;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.SetLoadBalancerPoliciesOfListenerRequest;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DBInstanceNotFoundException;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
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
public class AmazonAwsPetclinicInfrastructureEnforcer {

    public enum Distribution {
        /**
         * <a href="http://aws.amazon.com/amazon-linux-ami/">Amazon Linux
         * AMI</a>
         */
        AMZN_LINUX("ami-47cefa33", "cloud-config-amzn-linux.txt", "/usr/share/tomcat6", "t1.micro"), //
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

    public static final Function<Instance, String> EC2_INSTANCE_TO_AVAILABILITY_ZONE = new Function<Instance, String>() {
        @Override
        public String apply(Instance instance) {
            return instance.getPlacement().getAvailabilityZone();
        }
    };

    public final static Function<Instance, String> EC2_INSTANCE_TO_INSTANCE_ID = new Function<Instance, String>() {
        @Override
        public String apply(Instance instance) {
            return instance.getInstanceId();
        }
    };

    public final static Function<com.amazonaws.services.elasticloadbalancing.model.Instance, String> ELB_INSTANCE_TO_INSTANCE_ID = new Function<com.amazonaws.services.elasticloadbalancing.model.Instance, String>() {

        @Override
        public String apply(com.amazonaws.services.elasticloadbalancing.model.Instance instance) {
            return instance.getInstanceId();
        }
    };

    public final static Function<String, com.amazonaws.services.elasticloadbalancing.model.Instance> INSTANCE_ID_TO_ELB_INSTANCE = new Function<String, com.amazonaws.services.elasticloadbalancing.model.Instance>() {

        @Override
        public com.amazonaws.services.elasticloadbalancing.model.Instance apply(String instanceId) {
            return new com.amazonaws.services.elasticloadbalancing.model.Instance(instanceId);
        }
    };

    public static void main(String[] args) throws Exception {
        AmazonAwsPetclinicInfrastructureEnforcer infrastructureMaker = new AmazonAwsPetclinicInfrastructureEnforcer();
        infrastructureMaker.createInfrastructure(Distribution.AMZN_LINUX);

    }

    private AmazonEC2 ec2;

    private AmazonElasticLoadBalancing elb;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private AmazonRDS rds;

    public AmazonAwsPetclinicInfrastructureEnforcer() {
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

        AtomicInteger counter = new AtomicInteger();
        while (!"available".equals(dbInstance.getDBInstanceStatus())) {
            if (counter.incrementAndGet() > 1) {
                System.out.println("Instance " + dbInstance.getDBInstanceIdentifier() + "/" + dbInstance.getDBName()
                        + " not yet available, sleep...");
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
        System.out.println("\nENFORCE DATABASE");

        DescribeDBInstancesRequest describeDbInstanceRequest = new DescribeDBInstancesRequest().withDBInstanceIdentifier("petclinic");
        try {
            DescribeDBInstancesResult describeDBInstances = rds.describeDBInstances(describeDbInstanceRequest);
            if (describeDBInstances.getDBInstances().isEmpty()) {
                // good, db does not exist
            } else {
                DBInstance dbInstance = Iterables.getFirst(describeDBInstances.getDBInstances(), null);
                System.out.println("Database already exists! Skip creation" + dbInstance);
                return dbInstance;
            }
        } catch (DBInstanceNotFoundException e) {
            // good, db does not exist
        }

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

    public void createInfrastructure(Distribution... distributions) {

        String rootContext = "/petclinic";
        String healthCheckUri = rootContext + "/healthcheck.jsp";

        String jdbcUsername = "petclinic";
        String jdbcPassword = "petclinic";
        String warUrl = "http://xebia-france.googlecode.com/svn/repository/maven2/fr/xebia/demo/xebia-petclinic/1.0.2/xebia-petclinic-1.0.2.war";
        String warFileName = rootContext + ".war";

        DBInstance dbInstance = createDatabaseInstance(jdbcUsername, jdbcPassword);
        dbInstance = awaitForDbInstanceCreation(dbInstance);
        System.out.println("MySQL instance: " + dbInstance);

        List<Instance> petclinicInstances = createPetclinicTomcatServers(dbInstance, jdbcUsername, jdbcPassword, warUrl, warFileName,
                distributions);
        System.out.println("EC2 instances: " + petclinicInstances);
        LoadBalancerDescription loadBalancerDescription = createOrUpdateElasticLoadBalancer(healthCheckUri, "petclinic-tomcat");
        System.out.println("Load Balancer DNS name: " + loadBalancerDescription.getDNSName());
    }

    /**
     * 
     * @param healthCheckUri
     *            start with slash. E.g. "/petclinic/healthcheck.jsp
     * @param instanceTypeTag
     * @return created load balancer description
     */
    @Nonnull
    public LoadBalancerDescription createOrUpdateElasticLoadBalancer(@Nonnull String healthCheckUri, @Nonnull String instanceTypeTag) {
        System.out.println("\nENFORCE LOAD BALANCER");

        DescribeInstancesResult petclinicInstancesResult = ec2.describeInstances(new DescribeInstancesRequest().withFilters(new Filter(
                "tag:Type", Arrays.asList(instanceTypeTag))));

        Collection<List<Instance>> petClinicInstancesListOfLists = Collections2.transform(petclinicInstancesResult.getReservations(),
                new Function<Reservation, List<Instance>>() {
                    @Override
                    public List<Instance> apply(Reservation reservation) {
                        return reservation.getInstances();
                    }
                });
        Iterable<Instance> expectedPetclinicInstances = Iterables.concat(petClinicInstancesListOfLists);

        Set<String> expectedAvailabilityZones = Sets.newHashSet(Iterables.transform(expectedPetclinicInstances,
                EC2_INSTANCE_TO_AVAILABILITY_ZONE));

        String loadBalancerName = "petclinic";

        LoadBalancerDescription actualLoadBalancerDescription;
        try {
            DescribeLoadBalancersResult describeLoadBalancers = elb.describeLoadBalancers(new DescribeLoadBalancersRequest(Arrays
                    .asList(loadBalancerName)));
            if (describeLoadBalancers.getLoadBalancerDescriptions().isEmpty()) {
                actualLoadBalancerDescription = null;
            } else {
                // re-query to get updated config

                actualLoadBalancerDescription = Iterables.getFirst(describeLoadBalancers.getLoadBalancerDescriptions(), null);
            }
        } catch (LoadBalancerNotFoundException e) {
            actualLoadBalancerDescription = null;
        }

        Set<String> actualAvailabilityZones;
        Set<String> actualInstanceIds;
        Policies actualPolicies;
        HealthCheck actualHealthCheck;
        if (actualLoadBalancerDescription == null) {
            CreateLoadBalancerRequest createLoadBalancerRequest = new CreateLoadBalancerRequest() //
                    .withLoadBalancerName(loadBalancerName) //
                    .withAvailabilityZones(expectedAvailabilityZones) //
                    .withListeners(new Listener("HTTP", 80, 8080));
            elb.createLoadBalancer(createLoadBalancerRequest);

            actualAvailabilityZones = expectedAvailabilityZones;
            actualInstanceIds = Collections.emptySet();
            actualHealthCheck = new HealthCheck();
            actualPolicies = new Policies();
        } else {
            actualAvailabilityZones = Sets.newHashSet(actualLoadBalancerDescription.getAvailabilityZones());
            actualInstanceIds = Sets.newHashSet(Iterables.transform(actualLoadBalancerDescription.getInstances(),
                    ELB_INSTANCE_TO_INSTANCE_ID));

            actualHealthCheck = actualLoadBalancerDescription.getHealthCheck();

            actualPolicies = actualLoadBalancerDescription.getPolicies();
        }

        // HEALTH CHECK
        if (!healthCheckUri.startsWith("/")) {
            healthCheckUri = "/" + healthCheckUri;
        }

        HealthCheck expectedHealthCheck = new HealthCheck() //
                .withTarget("HTTP:8080" + healthCheckUri) //
                .withHealthyThreshold(2) //
                .withUnhealthyThreshold(2) //
                .withInterval(30) //
                .withTimeout(2);
        if (Objects.equal(expectedHealthCheck.getTarget(), actualHealthCheck.getTarget()) && //
                Objects.equal(expectedHealthCheck.getHealthyThreshold(), actualHealthCheck.getHealthyThreshold()) && //
                Objects.equal(expectedHealthCheck.getInterval(), actualHealthCheck.getInterval()) && //
                Objects.equal(expectedHealthCheck.getTimeout(), actualHealthCheck.getTimeout()) && //
                Objects.equal(expectedHealthCheck.getUnhealthyThreshold(), actualHealthCheck.getHealthyThreshold())) {

        } else {
            System.out.println("Set Healthcheck: " + expectedHealthCheck);
            elb.configureHealthCheck(new ConfigureHealthCheckRequest(loadBalancerName, expectedHealthCheck));
        }

        // AVAILABILITY ZONES
        // enable
        Iterable<String> availabilityZonesToEnable = Sets.difference(expectedAvailabilityZones, actualAvailabilityZones);
        System.out.println("Enable availability zones: " + availabilityZonesToEnable);
        if (!Iterables.isEmpty(availabilityZonesToEnable)) {
            elb.enableAvailabilityZonesForLoadBalancer(new EnableAvailabilityZonesForLoadBalancerRequest(loadBalancerName, Lists
                    .newArrayList(availabilityZonesToEnable)));
        }

        // disable
        Iterable<String> availabilityZonesToDisable = Sets.difference(actualAvailabilityZones, expectedAvailabilityZones);
        System.out.println("Disable availability zones: " + availabilityZonesToDisable);
        if (!Iterables.isEmpty(availabilityZonesToDisable)) {
            elb.disableAvailabilityZonesForLoadBalancer(new DisableAvailabilityZonesForLoadBalancerRequest(loadBalancerName, Lists
                    .newArrayList(availabilityZonesToDisable)));
        }

        // STICKINESS
        List<AppCookieStickinessPolicy> appCookieStickinessPoliciesToDelete = actualPolicies.getAppCookieStickinessPolicies();
        System.out.println("Delete app cookie stickiness policies:" + appCookieStickinessPoliciesToDelete);
        for (AppCookieStickinessPolicy appCookieStickinessPolicyToDelete : appCookieStickinessPoliciesToDelete) {
            elb.deleteLoadBalancerPolicy(new DeleteLoadBalancerPolicyRequest(loadBalancerName, appCookieStickinessPolicyToDelete
                    .getPolicyName()));
        }

        final LBCookieStickinessPolicy expectedLbCookieStickinessPolicy = new LBCookieStickinessPolicy("petclinic-stickiness-policy", null);
        Predicate<LBCookieStickinessPolicy> isExpectedPolicyPredicate = new Predicate<LBCookieStickinessPolicy>() {
            @Override
            public boolean apply(LBCookieStickinessPolicy lbCookieStickinessPolicy) {
                return Objects.equal(expectedLbCookieStickinessPolicy.getPolicyName(), lbCookieStickinessPolicy.getPolicyName()) && //
                        Objects.equal(expectedLbCookieStickinessPolicy.getCookieExpirationPeriod(),
                                lbCookieStickinessPolicy.getCookieExpirationPeriod());
            }
        };
        Collection<LBCookieStickinessPolicy> lbCookieStickinessPoliciesToDelete = Collections2.filter(
                actualPolicies.getLBCookieStickinessPolicies(), Predicates.not(isExpectedPolicyPredicate));
        System.out.println("Delete lb cookie stickiness policies: " + lbCookieStickinessPoliciesToDelete);
        for (LBCookieStickinessPolicy lbCookieStickinessPolicy : lbCookieStickinessPoliciesToDelete) {
            elb.deleteLoadBalancerPolicy(new DeleteLoadBalancerPolicyRequest(loadBalancerName, lbCookieStickinessPolicy.getPolicyName()));
        }

        Collection<LBCookieStickinessPolicy> matchingLbCookieStyckinessPolicy = Collections2.filter(
                actualPolicies.getLBCookieStickinessPolicies(), isExpectedPolicyPredicate);
        if (matchingLbCookieStyckinessPolicy.isEmpty()) {
            // COOKIE STICKINESS
            CreateLBCookieStickinessPolicyRequest createLbCookieStickinessPolicy = new CreateLBCookieStickinessPolicyRequest() //
                    .withLoadBalancerName(loadBalancerName) //
                    .withPolicyName(expectedLbCookieStickinessPolicy.getPolicyName()) //
                    .withCookieExpirationPeriod(expectedLbCookieStickinessPolicy.getCookieExpirationPeriod());
            System.out.println("Create LBCookieStickinessPolicy: " + createLbCookieStickinessPolicy);
            elb.createLBCookieStickinessPolicy(createLbCookieStickinessPolicy);

            SetLoadBalancerPoliciesOfListenerRequest setLoadBalancerPoliciesOfListenerRequest = new SetLoadBalancerPoliciesOfListenerRequest() //
                    .withLoadBalancerName(loadBalancerName) //
                    .withLoadBalancerPort(80) //
                    .withPolicyNames(createLbCookieStickinessPolicy.getPolicyName());
            elb.setLoadBalancerPoliciesOfListener(setLoadBalancerPoliciesOfListenerRequest);
        } else {
            // todo verify load balancer policy is set
        }

        // INSTANCES
        Set<String> expectedPetclinicInstanceIds = Sets.newHashSet(Iterables.transform(expectedPetclinicInstances,
                EC2_INSTANCE_TO_INSTANCE_ID));
        // enable
        Iterable<String> instanceIdsToEnable = Sets.difference(expectedPetclinicInstanceIds, actualInstanceIds);
        System.out.println("Enable petclinic instances: " + instanceIdsToEnable);
        if (!Iterables.isEmpty(instanceIdsToEnable)) {
            elb.registerInstancesWithLoadBalancer(new RegisterInstancesWithLoadBalancerRequest(loadBalancerName, Lists
                    .newArrayList(Iterables.transform(instanceIdsToEnable, INSTANCE_ID_TO_ELB_INSTANCE))));
        }

        // disable
        Iterable<String> instanceIdsToDisable = Sets.difference(actualInstanceIds, expectedPetclinicInstanceIds);
        System.out.println("Disable petclinic instances: " + instanceIdsToDisable);
        if (!Iterables.isEmpty(instanceIdsToDisable)) {
            elb.registerInstancesWithLoadBalancer(new RegisterInstancesWithLoadBalancerRequest(loadBalancerName, Lists
                    .newArrayList(Iterables.transform(instanceIdsToDisable, INSTANCE_ID_TO_ELB_INSTANCE))));
        }

        // QUERY TO GET UP TO DATE LOAD BALANCER DESCRIPTION
        LoadBalancerDescription elasticLoadBalancerDescription = Iterables.getOnlyElement(elb.describeLoadBalancers(
                new DescribeLoadBalancersRequest(Arrays.asList(loadBalancerName))).getLoadBalancerDescriptions());

        System.out.println("URLs");
        for (Instance instance : expectedPetclinicInstances) {
            System.out.println("http://" + instance.getPublicDnsName() + ":8080" + healthCheckUri);
        }
        System.out.println("http://" + elasticLoadBalancerDescription.getDNSName() + ":80" + healthCheckUri);

        logger.info("Created {}", elasticLoadBalancerDescription);

        return elasticLoadBalancerDescription;
    }

    public List<Instance> createPetclinicTomcatServers(DBInstance dbInstance, String jdbcUsername, String jdbcPassword, String warUrl,
            String warFileName, Distribution... distributions) {
        System.out.println("\nENFORCE TOMCAT SERVERS");

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
                            new Tag("Type", "petclinic-tomcat"), //
                            new Tag("Distribution", Distribution.fromAmiId(instance.getImageId()).name()));
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
