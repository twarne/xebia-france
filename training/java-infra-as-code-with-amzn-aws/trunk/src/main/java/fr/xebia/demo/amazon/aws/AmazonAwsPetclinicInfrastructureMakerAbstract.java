package fr.xebia.demo.amazon.aws;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLBCookieStickinessPolicyRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerListenersRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerPolicyRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancing.model.EnableAvailabilityZonesForLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.HealthCheck;
import com.amazonaws.services.elasticloadbalancing.model.LBCookieStickinessPolicy;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerNotFoundException;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.SetLoadBalancerPoliciesOfListenerRequest;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DBInstanceNotFoundException;
import com.amazonaws.services.rds.model.DeleteDBInstanceRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fr.xebia.cloud.cloudinit.CloudInitUserDataBuilder;
import fr.xebia.cloud.cloudinit.FreemarkerUtils;

public abstract class AmazonAwsPetclinicInfrastructureMakerAbstract {

    protected AmazonEC2 ec2;

    protected AmazonElasticLoadBalancing elb;

    protected AmazonRDS rds;

    protected final Logger logger = LoggerFactory.getLogger(getClass());


    @Nullable
    DBInstance findDBInstance(String dbInstanceIdentifier) {
        try {
            DescribeDBInstancesResult describeDBInstances = rds.describeDBInstances(new DescribeDBInstancesRequest().withDBInstanceIdentifier(dbInstanceIdentifier));
            return Iterables.getFirst(describeDBInstances.getDBInstances(), null);
        } catch (DBInstanceNotFoundException e) {
            return null;
        }
    }
    
    void deleteDBInstance(String dbInstanceIdentifier) {
        rds.deleteDBInstance(new DeleteDBInstanceRequest() //
        .withDBInstanceIdentifier(dbInstanceIdentifier) //
        .withSkipFinalSnapshot(true));
    }

    @Nonnull
    abstract DBInstance createDBInstance(String dbInstanceIdentifier) ;

    @Nonnull
    DBInstance waitForDBInstanceAvailability(String dbInstanceIdentifier) {
        while (true) {
            DBInstance dbInstance = findDBInstance(dbInstanceIdentifier);
            if (dbInstance == null) {
                throw new DBInstanceNotFoundException("Not DBInstance " + dbInstanceIdentifier + " exists");
            } else if ("available".equals(dbInstance.getDBInstanceStatus())) {
                return dbInstance;
            } else {
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Nonnull
    DBInstance createDBInstanceAndWaitForAvailability(String dbInstanceIdentifier) {
        DBInstance dbInstance = findDBInstance(dbInstanceIdentifier);
        if (dbInstance == null) {
            dbInstance = createDBInstance(dbInstanceIdentifier);
        }
        dbInstance = waitForDBInstanceAvailability(dbInstanceIdentifier);

        System.out.println("DB INSTANCE");
        System.out.println("=============");
        System.out.println("MySQL instance : " + dbInstance.getEndpoint().getAddress() + ":" + dbInstance.getEndpoint().getPort());
        System.out.println("=============");
        System.out.println("");
        return dbInstance;
    }

    List<Instance> terminateExistingAndCreateNewInstance(String trigram) {
        terminateMyAlreadyExistingEC2Instances(trigram);
        List<Instance> instances = createTwoEC2Instances();
        tagInstances(instances, trigram);

        System.out.println("EC2 INSTANCES");
        System.out.println("=============");

        for (Instance instance : instances) {
            while (InstanceStateName.Pending.name().toLowerCase().equals(instance.getState().getName())) {
                try {
                    // 3s because ec2 instance creation < 10 seconds
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    throw Throwables.propagate(e);
                }
                DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest().withInstanceIds(instance.getInstanceId());
                DescribeInstancesResult describeInstances = ec2.describeInstances(describeInstancesRequest);

                instance = describeInstances.getReservations().get(0).getInstances().get(0);
            }
            System.out.println("http://" + instance.getPublicDnsName() + ":8080" + "/petclinic");
        }
        System.out.println("=============");
        System.out.println("");

        return instances;

    }

    void terminateMyAlreadyExistingEC2Instances(String trigram) {
        List<Instance> instances = displayInstancesDetails(trigram);

        List<String> instanceIds = new ArrayList<String>();
        for (Instance instance : instances) {
            instanceIds.add(instance.getInstanceId());
        }

        if (!instanceIds.isEmpty()) {
            ec2.terminateInstances(new TerminateInstancesRequest()//
                    .withInstanceIds(instanceIds));
        }
    }

    List<Instance> displayInstancesDetails(String trigram) {
        DescribeInstancesResult describeInstances = ec2.describeInstances(new DescribeInstancesRequest()//
                .withFilters(new Filter("tag:Name", Arrays.asList("petclinic-" + trigram + "-*"))));
        List<Instance> instances = new ArrayList<Instance>();
        for (Reservation reservation : describeInstances.getReservations()) {
            for (Instance instance : reservation.getInstances()) {
                instances.add(instance);
            }
        }
        return instances;
    }

    @Nonnull
    abstract List<Instance> createTwoEC2Instances() ;

    void tagInstances(List<Instance> instances, String trigram) {
        int i = 1;
        for (Instance instance : instances) {
            ec2.createTags(new CreateTagsRequest() //
                    .withResources(instance.getInstanceId()) //
                    .withTags(new Tag("Name", "petclinic-" + trigram + "-" + i), //
                            new Tag("Owner", trigram), //
                            new Tag("Role", "tomcat-petclinic")) //
            );
            i++;
        }
    }

    /**
     * Returns a base-64 version of the mime-multi-part cloud-init file to put in the user-data attribute of the ec2 instance.
     * 
     * @param distribution
     * @param dbInstance
     * @param jdbcUsername
     * @param jdbcPassword
     * @param warUrl
     * @return
     */
    @Nonnull
    String buildCloudInitUserData(DBInstance dbInstance, String warUrl) {

        // SHELL SCRIPT
        Map<String, Object> rootMap = Maps.newHashMap();
        rootMap.put("catalinaBase", "/usr/share/tomcat6");
        rootMap.put("warUrl", warUrl);
        rootMap.put("warName", "/petclinic.war");

        Map<String, String> systemProperties = Maps.newHashMap();
        rootMap.put("systemProperties", systemProperties);
        String jdbcUrl = "jdbc:mysql://" + dbInstance.getEndpoint().getAddress() + ":" + dbInstance.getEndpoint().getPort() + "/" + dbInstance.getDBName();
        systemProperties.put("jdbc.url", jdbcUrl);
        systemProperties.put("jdbc.username", "petclinic");
        systemProperties.put("jdbc.password", "petclinic");

        String shellScript = FreemarkerUtils.generate(rootMap, "/provision_tomcat.py.fmt");

        // CLOUD CONFIG
        InputStream cloudConfigAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("cloud-config-amzn-linux.txt");
        Preconditions.checkNotNull(cloudConfigAsStream, "'" + "cloud-config-amzn-linux.txt" + "' not found in path");
        Readable cloudConfig = new InputStreamReader(cloudConfigAsStream);

        return CloudInitUserDataBuilder.start() //
                .addShellScript(shellScript) //
                .addCloudConfig(cloudConfig) //
                .buildBase64UserData();
    }


    LoadBalancerDescription createElasticLoadBalancer(String trigram) {
        String loadBalancerName = "elb-" + trigram;

        List<Instance> ec2Instances = displayInstancesDetails(trigram);

        Listener expectedListener = new Listener("HTTP", 80, 8080);

        //XXX : Problem with avaibility zones : -1a has been replaced by -1d ?!? : How get all AZ?
        List<String> expectedAvailabilityZones = Lists.newArrayList("us-east-1d", "us-east-1b", "us-east-1c");
        
        createLoadBalancerWithListeners(loadBalancerName, expectedListener, expectedAvailabilityZones);

        // AVAILABILITY ZONES
        EnableAvailabilityZonesForLoadBalancerRequest enableAvailabilityZonesForLoadBalancerRequest = new EnableAvailabilityZonesForLoadBalancerRequest( //
                loadBalancerName, expectedAvailabilityZones);
        elb.enableAvailabilityZonesForLoadBalancer(enableAvailabilityZonesForLoadBalancerRequest);

        // HEALTH CHECK
        String healthCheckUri = "/petclinic/healthcheck.jsp";
        createElasticLoadBalancerHealthCheck(loadBalancerName, healthCheckUri);

        // COOKIE STICKINESS
        final LBCookieStickinessPolicy expectedLbCookieStickinessPolicy = new LBCookieStickinessPolicy( //
                "petclinic" + "-stickiness-policy", null);
        createElasticLoadBalancerCookieStickiness(loadBalancerName, expectedLbCookieStickinessPolicy);

        // POLICY
        createElasticLoadBalancerPolicy(loadBalancerName, expectedListener, expectedLbCookieStickinessPolicy);

        // EC2 INSTANCES
        configureEC2InstancesForElasticLoadBalancer(loadBalancerName, ec2Instances);

        LoadBalancerDescription elasticLoadBalancerDescription = elb.describeLoadBalancers(new DescribeLoadBalancersRequest(Arrays.asList(loadBalancerName))).getLoadBalancerDescriptions().get(0);

        System.out.println("LOAD BALANCER");
        System.out.println("=============");
        System.out.println("http://" + elasticLoadBalancerDescription.getDNSName() + "/petclinic");
        System.out.println("=============");
        System.out.println("");

        return elasticLoadBalancerDescription;
    }

    abstract void createLoadBalancerWithListeners(String loadBalancerName, Listener expectedListener, List<String> expectedAvailabilityZones);

    abstract void configureEC2InstancesForElasticLoadBalancer(String loadBalancerName, List<Instance> ec2Instances) ;

    void createElasticLoadBalancerHealthCheck(String loadBalancerName, String healthCheckUri) {
        HealthCheck expectedHealthCheck = new HealthCheck() //
                .withTarget("HTTP:8080" + healthCheckUri) //
                .withHealthyThreshold(2) //
                .withUnhealthyThreshold(2) //
                .withInterval(30) //
                .withTimeout(2);

        elb.configureHealthCheck(new ConfigureHealthCheckRequest(loadBalancerName, expectedHealthCheck));
    }

    void createElasticLoadBalancerCookieStickiness(String loadBalancerName, LBCookieStickinessPolicy expectedLbCookieStickinessPolicy) {
        CreateLBCookieStickinessPolicyRequest createLbCookieStickinessPolicy = new CreateLBCookieStickinessPolicyRequest() //
                .withLoadBalancerName(loadBalancerName) //
                .withPolicyName(expectedLbCookieStickinessPolicy.getPolicyName()) //
                .withCookieExpirationPeriod(expectedLbCookieStickinessPolicy.getCookieExpirationPeriod());

        elb.createLBCookieStickinessPolicy(createLbCookieStickinessPolicy);

    }

    void createElasticLoadBalancerPolicy(String loadBalancerName, Listener expectedListener, LBCookieStickinessPolicy expectedLbCookieStickinessPolicy) {
        SetLoadBalancerPoliciesOfListenerRequest setLoadBalancerPoliciesOfListenerRequest = new SetLoadBalancerPoliciesOfListenerRequest() //
                .withLoadBalancerName(loadBalancerName) //
                .withLoadBalancerPort(expectedListener.getLoadBalancerPort()) //
                .withPolicyNames(expectedLbCookieStickinessPolicy.getPolicyName());

        elb.setLoadBalancerPoliciesOfListener(setLoadBalancerPoliciesOfListenerRequest);
    }

    void deleteExistingElasticLoadBalancer(String trigram) {
        String loadBalancerName = "elb-" + trigram;
        try {
            elb.deleteLoadBalancer(new DeleteLoadBalancerRequest() //
                    .withLoadBalancerName(loadBalancerName));
            elb.deleteLoadBalancerListeners(new DeleteLoadBalancerListenersRequest() //
                    .withLoadBalancerName(loadBalancerName) //
                    .withLoadBalancerPorts(80, 8080));
            elb.deleteLoadBalancerPolicy(new DeleteLoadBalancerPolicyRequest().withLoadBalancerName(loadBalancerName));
        } catch (LoadBalancerNotFoundException e) {
            // Nothing to delete
        }
    }
}