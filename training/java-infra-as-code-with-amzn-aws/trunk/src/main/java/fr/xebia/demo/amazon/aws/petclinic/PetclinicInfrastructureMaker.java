package fr.xebia.demo.amazon.aws.petclinic;

import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.elasticloadbalancing.model.LBCookieStickinessPolicy;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.amazonaws.services.rds.model.DBInstance;
import com.google.common.collect.Lists;

import fr.xebia.demo.amazon.aws.petclinic.challenge.MakerChallenge;
import fr.xebia.demo.amazon.aws.petclinic.client.EC2Client;
import fr.xebia.demo.amazon.aws.petclinic.client.ELBClient;
import fr.xebia.demo.amazon.aws.petclinic.client.RDSClient;

/**
 * Uses a {@link MakerChallenge} with a {@link RDSClient}, a {@link ELBClient}
 * and a {@link RDSClient} to instantiate a full petclinic application.
 */
public class PetclinicInfrastructureMaker {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PetclinicInfrastructureMaker.class);
    private static final String WAR_URL = "http://xebia-france.googlecode.com/svn/repository/maven2/fr/xebia/demo/xebia-petclinic/1.0.2/xebia-petclinic-1.0.2.war";
    private static final String SEPARATOR = "======================";

    private final MakerChallenge makerChallenge;

    private final RDSClient rds;
    private EC2Client ec2;
    private ELBClient elb;

    public PetclinicInfrastructureMaker(MakerChallenge makerChallenge) {
        this.makerChallenge = makerChallenge;
        rds = new RDSClient(makerChallenge.createRDSClient());
        try {
            ec2 = new EC2Client(makerChallenge.createEC2Client());
        } catch (Exception e) {
            LOGGER.warn(
                    "Error during creation of ec2 client. Full petclinic creation will not be possible.",
                    e);
        }
        try {
            elb = new ELBClient(makerChallenge.createELBClient());
        } catch (Exception e) {
            LOGGER.warn(
                    "Error during creation of elb client. Full petclinic creation will not be possible.",
                    e);
        }
    }

    @Nonnull
    DBInstance createDBInstanceAndWaitForAvailability(String trigram) {
        String dbInstanceIdentifier = getDbInstanceIdentifier(trigram);
        DBInstance dbInstance = rds.findDBInstance(dbInstanceIdentifier);
        if (dbInstance == null) {
            LOGGER.debug(
                    "Db instance {} was not found, it need to be created.",
                    dbInstanceIdentifier);
            dbInstance = makerChallenge.createDBInstance(dbInstanceIdentifier);
        }
        dbInstance = rds.waitForDBInstanceAvailability(dbInstanceIdentifier);

        LOGGER.info(SEPARATOR);
        LOGGER.info("Db instance {} is ready for use.", dbInstanceIdentifier);
        LOGGER.info("Db instance {} endpoint : {} ", dbInstanceIdentifier,
                dbInstance.getEndpoint());
        LOGGER.info(SEPARATOR);

        return dbInstance;
    }

    List<Instance> terminateExistingAndCreateNewInstance(String trigram) {
        ec2.terminateMyAlreadyExistingEC2Instances(trigram);
        DBInstance dbInstance = rds
                .findDBInstance(getDbInstanceIdentifier(trigram));
        List<Instance> instances = makerChallenge.createTwoEC2Instances(
                new CloudInit(), dbInstance, WAR_URL);
        ec2.tagInstances(instances, trigram);
        List<Instance> availableInstances = ec2
                .waitForEc2InstancesAvailability(instances);

        LOGGER.info(SEPARATOR);
        LOGGER.info("Ec2 instances are ready for use.");
        for (Instance instance : availableInstances) {
            LOGGER.info(
                    "Ec2 instance {} available at http://{}:8080/petclinic",
                    instance.getInstanceId(), instance.getPublicDnsName());
        }
        LOGGER.info(SEPARATOR);

        return instances;

    }

    LoadBalancerDescription createElasticLoadBalancer(String trigram) {
        String loadBalancerName = getLoadBalancerName(trigram);

        List<Instance> ec2Instances = ec2.displayInstancesDetails(trigram);

        Listener expectedListener = new Listener("HTTP", 80, 8080);
        List<String> expectedAvailabilityZones = Lists.newArrayList(
                "eu-west-1a", "eu-west-1b", "eu-west-1c");

        makerChallenge.createLoadBalancerWithListeners(loadBalancerName,
                expectedListener, expectedAvailabilityZones);

        // configure load balancer after creation
        elb.enableAvailabilityZonesForLoadBalancer(loadBalancerName,
                expectedAvailabilityZones);
        elb.createElasticLoadBalancerHealthCheck(loadBalancerName,
                "/petclinic/healthcheck.jsp");
        LBCookieStickinessPolicy expectedLbCookieStickinessPolicy = elb
                .createElasticLoadBalancerCookieStickiness(loadBalancerName);
        elb.setupElasticLoadBalancerPolicy(loadBalancerName, expectedListener,
                expectedLbCookieStickinessPolicy);

        makerChallenge.registerEC2InstancesForElasticLoadBalancer(
                loadBalancerName, ec2Instances);

        LoadBalancerDescription elasticLoadBalancerDescription = elb
                .describeLoadBalancer(loadBalancerName);

        LOGGER.info(SEPARATOR);
        LOGGER.info("Load balancer {} is ready for use.", loadBalancerName);
        LOGGER.info("Load balancer {} available at http://{}/petclinic",
                loadBalancerName, elasticLoadBalancerDescription.getDNSName());
        LOGGER.info(SEPARATOR);

        return elasticLoadBalancerDescription;
    }

    public void deleteDBInstance(String trigram) {
        rds.deleteDBInstance(getDbInstanceIdentifier(trigram));
    }

    public void terminateMyAlreadyExistingEC2Instances(String trigram) {
        ec2.terminateMyAlreadyExistingEC2Instances(trigram);
    }

    public void deleteExistingElasticLoadBalancer(String trigram) {
        elb.deleteExistingElasticLoadBalancer(getLoadBalancerName(trigram));
    }

    private String getDbInstanceIdentifier(String trigram) {
        return "petclinic-" + trigram;
    }

    private String getLoadBalancerName(String trigram) {
        String loadBalancerName = "elb-" + trigram;
        return loadBalancerName;
    }
}
