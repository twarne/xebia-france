package fr.xebia.demo.amazon.aws.petclinic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLBCookieStickinessPolicyRequest;
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
import com.amazonaws.services.elasticloadbalancing.model.SetLoadBalancerPoliciesOfListenerRequest;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DBInstanceNotFoundException;
import com.amazonaws.services.rds.model.DeleteDBInstanceRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import fr.xebia.demo.amazon.aws.petclinic.challenge.MakerChallenge;

public class InfrastructureMaker {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfrastructureMaker.class);
    private static final String SEPARATOR = "======================";

    private final MakerChallenge makerChallenge;
    
    private final AmazonRDS rds;
    private AmazonEC2 ec2;
    private AmazonElasticLoadBalancing elb;
    
    public InfrastructureMaker(MakerChallenge makerChallenge) {
        this.makerChallenge = makerChallenge;
        rds = makerChallenge.createRDSClient();
        try {
            ec2 = makerChallenge.createEC2Client();
        } catch (Exception e) {
            LOGGER.warn("Error during creation of ec2 client. Full petclinic creation will not be possible.", e);
        }
        try {
            elb = makerChallenge.createELBClient();
        } catch (Exception e) {
            LOGGER.warn("Error during creation of elb client. Full petclinic creation will not be possible.", e);
        }
    }

    @Nullable
    DBInstance findDBInstance(String dbInstanceIdentifier) {
       LOGGER.debug("Request description for db instance {}.", dbInstanceIdentifier);
        try {
            DescribeDBInstancesResult describeDBInstances = rds.describeDBInstances(new DescribeDBInstancesRequest().withDBInstanceIdentifier(dbInstanceIdentifier));
            return Iterables.getFirst(describeDBInstances.getDBInstances(), null);
        } catch (DBInstanceNotFoundException e) {
            LOGGER.trace("Db instance {} not found.", dbInstanceIdentifier);
            return null;
        }
    }
    
    void deleteDBInstance(String dbInstanceIdentifier) {
        LOGGER.debug("Request deletion of db instance {}.", dbInstanceIdentifier);
        rds.deleteDBInstance(new DeleteDBInstanceRequest() //
        .withDBInstanceIdentifier(dbInstanceIdentifier) //
        .withSkipFinalSnapshot(true));
    }

    @Nonnull
    DBInstance waitForDBInstanceAvailability(String dbInstanceIdentifier) {
        LOGGER.debug("Wait for availability of db instance {}.", dbInstanceIdentifier);
        while (true) {
            DBInstance dbInstance = findDBInstance(dbInstanceIdentifier);
            if (dbInstance == null) {
                throw new DBInstanceNotFoundException("No DBInstance " + dbInstanceIdentifier + " exists");
            };
            LOGGER.trace("Db instance {} status : {}", dbInstanceIdentifier, dbInstance.getDBInstanceStatus());
            if ("available".equals(dbInstance.getDBInstanceStatus())) {
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
            LOGGER.debug("Db instance {} was not found, it need to be created.", dbInstanceIdentifier);
            dbInstance = makerChallenge.createDBInstance(dbInstanceIdentifier);
        }
        dbInstance = waitForDBInstanceAvailability(dbInstanceIdentifier);
        
        LOGGER.info(SEPARATOR);
        LOGGER.info("Db instance {} is ready for use.", dbInstanceIdentifier);
        LOGGER.info("Db instance {} endpoint : {} ", dbInstanceIdentifier, dbInstance.getEndpoint());
        LOGGER.info(SEPARATOR);
        
        return dbInstance;
    }

    List<Instance> terminateExistingAndCreateNewInstance(String trigram) {
        terminateMyAlreadyExistingEC2Instances(trigram);
        DBInstance dbInstance = findDBInstance("petclinic-"+trigram);
        String warUrl = "http://xebia-france.googlecode.com/svn/repository/maven2/fr/xebia/demo/xebia-petclinic/1.0.2/xebia-petclinic-1.0.2.war";

        List<Instance> instances = makerChallenge.createTwoEC2Instances(new CloudInit(), dbInstance, warUrl);
        tagInstances(instances, trigram);
        waitForEc2InstancesAvailability(instances);

        return instances;

    }

    void terminateMyAlreadyExistingEC2Instances(String trigram) {
        List<Instance> instances = displayInstancesDetails(trigram);

        List<String> instanceIds = new ArrayList<String>();
        for (Instance instance : instances) {
            instanceIds.add(instance.getInstanceId());
        }

        if (instanceIds.isEmpty()) {
            LOGGER.debug("No existent Ec2 instances to terminate.");            
        } else {
            LOGGER.debug("Request termination of {} Ec2 instances with trigram {}",
                    instances.size(), trigram);
            ec2.terminateInstances(new TerminateInstancesRequest()//
                    .withInstanceIds(instanceIds));
        }
    }

    List<Instance> displayInstancesDetails(String trigram) {
        LOGGER.debug("Request description of Ec2 instances with trigram {}", trigram);

        DescribeInstancesResult describeInstances = ec2.describeInstances(new DescribeInstancesRequest()//
                .withFilters(new Filter("tag:Name", Arrays.asList("petclinic-" + trigram + "-*"))));
        List<Instance> instances = new ArrayList<Instance>();
        for (Reservation reservation : describeInstances.getReservations()) {
            for (Instance instance : reservation.getInstances()) {
                LOGGER.debug("Received description of Ec2 instance {}", instance.getInstanceId());
                instances.add(instance);
            }
        }
        return instances;
    }

    void tagInstances(List<Instance> instances, String trigram) {
        int i = 1;
        for (Instance instance : instances) {
            LOGGER.debug("Tag instance {} with trigram {}", instance.getInstanceId(), trigram);
            ec2.createTags(new CreateTagsRequest() //
                    .withResources(instance.getInstanceId()) //
                    .withTags(new Tag("Name", "petclinic-" + trigram + "-" + i), //
                            new Tag("Owner", trigram), //
                            new Tag("Role", "tomcat-petclinic")) //
            );
            i++;
        }
    }
    
    private void waitForEc2InstancesAvailability(List<Instance> instances) {
        LOGGER.debug("Wait for availability of {} Ec2 instance.", instances.size());
        List<Instance> availableInstances = new ArrayList<Instance>(instances.size());
        
        for (Instance instance : instances) {
            while (InstanceStateName.Pending.name().toLowerCase().equals(instance.getState().getName())) {
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    throw Throwables.propagate(e);
                }
                LOGGER.debug("Request description of Ec2 instance {}", instance.getInstanceId());
                DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest().withInstanceIds(instance.getInstanceId());
                DescribeInstancesResult describeInstances = ec2.describeInstances(describeInstancesRequest);

                instance = describeInstances.getReservations().get(0).getInstances().get(0);
            }
            availableInstances.add(instance);
        }
        
        LOGGER.info(SEPARATOR);
        LOGGER.info("Ec2 instances are ready for use.");
        for (Instance instance : availableInstances) {
            LOGGER.info("Ec2 instance {} available at http://{}:8080/petclinic", instance.getInstanceId(), instance.getPublicDnsName());
        }
        LOGGER.info(SEPARATOR);
    }


    LoadBalancerDescription createElasticLoadBalancer(String trigram) {
        String loadBalancerName = "elb-" + trigram;

        List<Instance> ec2Instances = displayInstancesDetails(trigram);

        Listener expectedListener = new Listener("HTTP", 80, 8080);
        List<String> expectedAvailabilityZones = Lists.newArrayList("eu-west-1a", "eu-west-1b", "eu-west-1c");
        
        makerChallenge.createLoadBalancerWithListeners(loadBalancerName, expectedListener, expectedAvailabilityZones);

        // AVAILABILITY ZONES
        LOGGER.debug("Request setup of availability zones for load balancer {}", loadBalancerName);
        EnableAvailabilityZonesForLoadBalancerRequest enableAvailabilityZonesForLoadBalancerRequest = new EnableAvailabilityZonesForLoadBalancerRequest( //
                loadBalancerName, expectedAvailabilityZones);
        elb.enableAvailabilityZonesForLoadBalancer(enableAvailabilityZonesForLoadBalancerRequest);

        // HEALTH CHECK
        String healthCheckUri = "/petclinic/healthcheck.jsp";
        createElasticLoadBalancerHealthCheck(loadBalancerName, healthCheckUri);

        // COOKIE STICKINESS POLICY
        final LBCookieStickinessPolicy expectedLbCookieStickinessPolicy = new LBCookieStickinessPolicy( //
                "petclinic" + "-stickiness-policy", null);
        createElasticLoadBalancerCookieStickiness(loadBalancerName, expectedLbCookieStickinessPolicy);
        setupElasticLoadBalancerPolicy(loadBalancerName, expectedListener, expectedLbCookieStickinessPolicy);

        // EC2 INSTANCES
        makerChallenge.registerEC2InstancesForElasticLoadBalancer(loadBalancerName, ec2Instances);

        LOGGER.debug("Request description of load balancer {}", loadBalancerName);
        LoadBalancerDescription elasticLoadBalancerDescription = elb.describeLoadBalancers(new DescribeLoadBalancersRequest(Arrays.asList(loadBalancerName))).getLoadBalancerDescriptions().get(0);

        LOGGER.info(SEPARATOR);
        LOGGER.info("Load balancer {} is ready for use.", loadBalancerName);
        LOGGER.info("Load balancer {} available at http://{}/petclinic", loadBalancerName, elasticLoadBalancerDescription.getDNSName());
        LOGGER.info(SEPARATOR);

        return elasticLoadBalancerDescription;
    }

    void createElasticLoadBalancerHealthCheck(String loadBalancerName, String healthCheckUri) {
        LOGGER.debug("Request setup of health check for load balancer {}", loadBalancerName);
        HealthCheck expectedHealthCheck = new HealthCheck() //
                .withTarget("HTTP:8080" + healthCheckUri) //
                .withHealthyThreshold(2) //
                .withUnhealthyThreshold(2) //
                .withInterval(30) //
                .withTimeout(2);
        elb.configureHealthCheck(new ConfigureHealthCheckRequest(loadBalancerName, expectedHealthCheck));
    }

    void createElasticLoadBalancerCookieStickiness(String loadBalancerName, LBCookieStickinessPolicy expectedLbCookieStickinessPolicy) {
        LOGGER.debug("Request creation of cookie stickiness policy for load balancer {}", loadBalancerName);
        CreateLBCookieStickinessPolicyRequest createLbCookieStickinessPolicy = new CreateLBCookieStickinessPolicyRequest() //
                .withLoadBalancerName(loadBalancerName) //
                .withPolicyName(expectedLbCookieStickinessPolicy.getPolicyName()) //
                .withCookieExpirationPeriod(expectedLbCookieStickinessPolicy.getCookieExpirationPeriod());
        elb.createLBCookieStickinessPolicy(createLbCookieStickinessPolicy);
    }

    void setupElasticLoadBalancerPolicy(String loadBalancerName, Listener expectedListener, LBCookieStickinessPolicy expectedLbCookieStickinessPolicy) {
        LOGGER.debug("Request setup of policy for load balancer {}", loadBalancerName);
        SetLoadBalancerPoliciesOfListenerRequest setLoadBalancerPoliciesOfListenerRequest = new SetLoadBalancerPoliciesOfListenerRequest() //
                .withLoadBalancerName(loadBalancerName) //
                .withLoadBalancerPort(expectedListener.getLoadBalancerPort()) //
                .withPolicyNames(expectedLbCookieStickinessPolicy.getPolicyName());
        elb.setLoadBalancerPoliciesOfListener(setLoadBalancerPoliciesOfListenerRequest);
    }

    void deleteExistingElasticLoadBalancer(String trigram) {
        String loadBalancerName = "elb-" + trigram;
        try {
            LOGGER.debug("Request deletion of load balancer {}", loadBalancerName);
            elb.deleteLoadBalancer(new DeleteLoadBalancerRequest() //
                    .withLoadBalancerName(loadBalancerName));
            
            LOGGER.debug("Request deletion of listeners for load balancer {}", loadBalancerName);
            elb.deleteLoadBalancerListeners(new DeleteLoadBalancerListenersRequest() //
                    .withLoadBalancerName(loadBalancerName) //
                    .withLoadBalancerPorts(80, 8080));
            
            LOGGER.debug("Request deletion of policy for load balancer {}", loadBalancerName);
            elb.deleteLoadBalancerPolicy(new DeleteLoadBalancerPolicyRequest().withLoadBalancerName(loadBalancerName));
        } catch (LoadBalancerNotFoundException e) {
            // Nothing to delete
        }
    }
}