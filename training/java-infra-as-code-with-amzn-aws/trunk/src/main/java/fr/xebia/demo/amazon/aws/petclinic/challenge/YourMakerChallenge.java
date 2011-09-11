package fr.xebia.demo.amazon.aws.petclinic.challenge;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang.NotImplementedException;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.DBInstance;

import fr.xebia.demo.amazon.aws.petclinic.CloudInit;

public class YourMakerChallenge implements MakerChallenge {

    @Nonnull
    @Override
    public String getTrigram() {
        throw new NotImplementedException("TODO");
    }

    @Nonnull
    @Override
    public String getKeyPair() {
        throw new NotImplementedException("TODO");
    }

    @Nonnull
    @Override
    public AmazonRDSClient createRDSClient() {
        throw new NotImplementedException("TODO");
    }

    @Nonnull
    @Override
    public DBInstance createDBInstance(String dbInstanceIdentifier) {
        throw new NotImplementedException("TODO");
    }

    @Nonnull
    @Override
    public AmazonEC2Client createEC2Client() {
        throw new NotImplementedException("TODO");
    }

    @Nonnull
    @Override
    public List<Instance> createTwoEC2Instances(CloudInit cloudInit, DBInstance dbInstance,
            String warUrl) {
        throw new NotImplementedException("TODO");
    }

    @Nonnull
    @Override
    public AmazonElasticLoadBalancingClient createELBClient() {
        throw new NotImplementedException("TODO");
    }

    @Override
    public void createLoadBalancerWithListeners(String loadBalancerName,
            Listener expectedListener, List<String> expectedAvailabilityZones) {
        throw new NotImplementedException("TODO");
    }

    @Override
    public void registerEC2InstancesForElasticLoadBalancer(
            String loadBalancerName, List<Instance> ec2Instances) {
        throw new NotImplementedException("TODO");
    }

}
