package fr.xebia.demo.amazon.aws.petclinic.challenge;

import java.util.List;

import javax.annotation.Nonnull;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.DBInstance;

import fr.xebia.demo.amazon.aws.petclinic.CloudInit;

/**
 * <p>
 * Builds a java petclinic infrastructure on Amazon EC2.
 * </p>
 * <p>
 * Implements the core tasks in {@link YourMakerChallenge} by following the steps.
 * <ol>
 * 
 * <li>setup your personal information
 * <ol>
 * <li>{@link #getTrigram()}</li>
 * <li>{@link #getKeyPair()}</li>
 * </ol>
 * </li>
 * 
 * <li>setup database
 * <ol>
 * <li>{@link #createRDSClient()}</li>
 * <li>{@link #createDBInstance(String)}</li>
 * </ol>
 * </li>
 * 
 * <li>setup tomcats
 * <ol>
 * <li>{@link #createRDSClient()}</li>
 * <li>{@link #createDBInstance(String)}</li>
 * </ol>
 * </li>
 * 
 * <li>setup load balancer
 * <ol>
 * <li>{@link #createELBClient()}</li>
 * <li>{@link #createLoadBalancerWithListeners(String, Listener, List)}</li>
 * <li>{@link #registerEC2InstancesForElasticLoadBalancer(String, List)}</li>
 * </ol>
 * </li>
 * 
 * </ol>
 * </p>
 */
public interface MakerChallenge {
    
    /**
     * <p>
     * In order to help identify your instances, they will be named with your
     * trigram.
     * </p>
     * <p>
     * Eg. Cyrille Le Clerc => clc
     * </p>
     * 
     * @return your trigram
     */
    @Nonnull
    String getTrigram();
    
    /**
     * Your EC2 key pair will allows you to connect via to your instances.
     */
    @Nonnull
    String getKeyPair();
    
    /**
     * @return the amazon client for communicating with the Relational Database Service.
     */
    @Nonnull
    AmazonRDSClient createRDSClient();
    
    /**
     * Creates a database instance using the rds client.
     */
    @Nonnull
    DBInstance createDBInstance(String dbInstanceIdentifier);

    /**
     * @return the amazon client for communicating with the Elastic Cloud Compute Service.
     */
    @Nonnull
    AmazonEC2Client createEC2Client();
    
    /**
     * Creates two Ec2 instances that will be configured with cloudinit.
     * They host a tomcat with a deployed petclinic application.
     */
    @Nonnull
    List<Instance> createTwoEC2Instances(CloudInit cloudInit, DBInstance dbInstance, String warUrl);

    /**
     * @return the amazon client for communicating with the Elastic Load Balancing Service.
     */
    @Nonnull
    AmazonElasticLoadBalancingClient createELBClient();

    /**
     * Creates a load balancer.
     */
    void createLoadBalancerWithListeners(String loadBalancerName, Listener expectedListener, List<String> expectedAvailabilityZones);
    
    /**
     * Registers the previously created Ec2 instances for the load balancer.
     */
    void registerEC2InstancesForElasticLoadBalancer(String loadBalancerName, List<Instance> ec2Instances);
}
