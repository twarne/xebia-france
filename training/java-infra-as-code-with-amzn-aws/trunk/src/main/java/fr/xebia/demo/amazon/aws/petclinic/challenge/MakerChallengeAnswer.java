package fr.xebia.demo.amazon.aws.petclinic.challenge;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.DBInstance;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import fr.xebia.demo.amazon.aws.petclinic.CloudInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MakerChallengeAnswer implements MakerChallenge {
    private static final Logger LOGGER = LoggerFactory.getLogger(MakerChallengeAnswer.class);

    @Nonnull
    @Override
    public String getTrigram() {
        return "xeb";
    }

    @Nonnull
    @Override
    public String getSshKeyPairName() {
        return "xebia-france";
    }

    @Nonnull
    @Override
    public AmazonRDSClient createRDSClient() {
        LOGGER.debug("Create RDS client.");
        AmazonRDSClient rdsClient = new AmazonRDSClient(getCredentials());
        rdsClient.setEndpoint("rds.eu-west-1.amazonaws.com");
        return rdsClient;
    }

    @Nonnull
    @Override
    public DBInstance createDBInstance(String dbInstanceIdentifier) {
        LOGGER.debug("Request creation of db instance {}", dbInstanceIdentifier);
        CreateDBInstanceRequest dbInstanceRequest = new CreateDBInstanceRequest() //
                .withDBInstanceIdentifier(dbInstanceIdentifier) //
                .withDBName("petclinic") //
                .withDBInstanceClass("db.m1.small") //
                .withEngine("MySQL") //
                .withMasterUsername("petclinic") //
                .withMasterUserPassword("petclinic") //
                .withDBSecurityGroups("default") //
                .withAllocatedStorage(5 /* Go */) //
                .withBackupRetentionPeriod(0);
        return createRDSClient().createDBInstance(dbInstanceRequest);
    }

    @Nonnull
    @Override
    public AmazonEC2Client createEC2Client() {
        LOGGER.debug("Create EC2 client.");
        AmazonEC2Client ec2Client = new AmazonEC2Client(getCredentials());
        ec2Client.setEndpoint("ec2.eu-west-1.amazonaws.com");
        return ec2Client;
    }

    @Nonnull
    @Override
    public List<Instance> createTwoEC2Instances(CloudInit cloudInit, DBInstance dbInstance,
            String warUrl) {
        LOGGER.debug("Request creation of 2 Ec2 instances.");
        RunInstancesRequest runInstanceRequest = new RunInstancesRequest() //
                .withImageId("ami-47cefa33") // eu-west : ami-47cefa33; us-east :ami-8c1fece5
                .withMinCount(2) //
                .withMaxCount(2) //
                .withSecurityGroups("tomcat") //
                .withKeyName(getSshKeyPairName()) //
                .withInstanceType(InstanceType.T1Micro.toString()) //
                .withUserData(cloudInit.createUserDataBuilder(dbInstance, warUrl).buildBase64UserData());
        RunInstancesResult runInstances = createEC2Client().runInstances(runInstanceRequest);
        return runInstances.getReservation().getInstances();
    }

    @Nonnull
    @Override
    public AmazonElasticLoadBalancingClient createELBClient() {
        LOGGER.debug("Create ELB client.");
        AmazonElasticLoadBalancingClient elbClient = new AmazonElasticLoadBalancingClient(getCredentials());
        elbClient.setEndpoint("elasticloadbalancing.eu-west-1.amazonaws.com");
        return elbClient;
    }

    @Override
    public void createLoadBalancerWithListeners(String loadBalancerName,
            Listener expectedListener, List<String> expectedAvailabilityZones) {
        LOGGER.debug("Request creation load balancer {}.", loadBalancerName);
        CreateLoadBalancerRequest createLoadBalancerRequest = new CreateLoadBalancerRequest() //
                .withLoadBalancerName(loadBalancerName) //
                .withAvailabilityZones(expectedAvailabilityZones) //
                .withListeners(expectedListener);
        createELBClient().createLoadBalancer(createLoadBalancerRequest);
    }

    @Override
    public void registerEC2InstancesForElasticLoadBalancer(
            String loadBalancerName, List<Instance> ec2Instances) {
        List<com.amazonaws.services.elasticloadbalancing.model.Instance> instances = getAvailableInstances(ec2Instances);
        
        LOGGER.debug("Request registration of {} instances to loadbalancer {}", instances.size(), loadBalancerName);

        RegisterInstancesWithLoadBalancerRequest registerInstancesWithLoadBalancerRequest = new RegisterInstancesWithLoadBalancerRequest( //
                loadBalancerName, //
                instances);
        createELBClient().registerInstancesWithLoadBalancer(registerInstancesWithLoadBalancerRequest);
    }

    @Nonnull
    protected AWSCredentials getCredentials() {
        InputStream credentialsAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("AwsCredentials.properties");
        Preconditions.checkNotNull(credentialsAsStream, "File 'AwsCredentials.properties' NOT found in the classpath");
        try {
            return new PropertiesCredentials(credentialsAsStream);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private List<com.amazonaws.services.elasticloadbalancing.model.Instance> getAvailableInstances(
            List<Instance> ec2Instances) {
        List<com.amazonaws.services.elasticloadbalancing.model.Instance> instances = new ArrayList<com.amazonaws.services.elasticloadbalancing.model.Instance>();
        for (Instance instance : ec2Instances) {
            // terminated and shutting-down instances should not be used
            if(instance.getState().getName().startsWith("terminat") || instance.getState().getName().startsWith("shutting")) {
                LOGGER.debug("Ignore Ec2 instance {} due to state {}",instance.getInstanceId(), instance.getState().getName());
                continue;
            }
            instances.add(new com.amazonaws.services.elasticloadbalancing.model.Instance(instance.getInstanceId()));
        }
        return instances;
    }

}
