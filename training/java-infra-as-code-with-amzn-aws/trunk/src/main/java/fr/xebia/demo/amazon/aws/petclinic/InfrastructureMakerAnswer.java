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
package fr.xebia.demo.amazon.aws.petclinic;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * <p>
 * Builds a java petclinic infrastructure on Amazon EC2.
 * </p>
 * <p>
 * creates:
 * <ul>
 * <li>1 MySQL database</li>
 * <li>2 Tomcat / xebia-pet-clinic servers connected to the mysql database (connected via the injection of the jdbc parameters in catalina.properties via cloud-init)</li>
 * <li>1 load balancer</li>
 * </ul>
 * </p>
 * 
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class InfrastructureMakerAnswer extends AbstractInfrastructureMaker {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfrastructureMakerAnswer.class);
    
    public InfrastructureMakerAnswer() {
        LOGGER.debug("Create RDS, EC2 and ELB clients.");

        try {
            AWSCredentials credentials = getCredentials();
            
            rds = new AmazonRDSClient(credentials);
            rds.setEndpoint("eu-west-1.rds.amazonaws.com");
            
            ec2 = new AmazonEC2Client(credentials);
            ec2.setEndpoint("eu-west-1.ec2.amazonaws.com");
            
            elb = new AmazonElasticLoadBalancingClient(credentials);
            elb.setEndpoint("eu-west-1.elasticloadbalancing.amazonaws.com");
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    protected AWSCredentials getCredentials() throws IOException {
        InputStream credentialsAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("AwsCredentials.properties");
        Preconditions.checkNotNull(credentialsAsStream, "File 'AwsCredentials.properties' NOT found in the classpath");
        AWSCredentials credentials = new PropertiesCredentials(credentialsAsStream);
        return credentials;
    }

    @Nonnull
    @Override
    DBInstance createDBInstance(String dbInstanceIdentifier) {
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
        return rds.createDBInstance(dbInstanceRequest);
    }

    @Nonnull
    @Override
    List<Instance> createTwoEC2Instances(DBInstance dbInstance, String warUrl) {
        LOGGER.debug("Request creation of 2 Ec2 instances.");
        RunInstancesRequest runInstanceRequest = new RunInstancesRequest() //
                .withImageId("ami-47cefa33") // eu-west : ami-47cefa33; us-east :ami-8c1fece5
                .withMinCount(2) //
                .withMaxCount(2) //
                .withSecurityGroups("tomcat") //
                .withKeyName(PersonalConfig.KEY_PAIR) //
                .withInstanceType(InstanceType.T1Micro.toString()) //
                .withUserData(createCloudInitUserDataBuilder(dbInstance, warUrl).buildBase64UserData());
        RunInstancesResult runInstances = ec2.runInstances(runInstanceRequest);
        return runInstances.getReservation().getInstances();
    }
    
    @Override
    void createLoadBalancerWithListeners(String loadBalancerName, Listener expectedListener, List<String> expectedAvailabilityZones) {
        LOGGER.debug("Request creation load balancer {}.", loadBalancerName);
        CreateLoadBalancerRequest createLoadBalancerRequest = new CreateLoadBalancerRequest() //
                .withLoadBalancerName(loadBalancerName) //
                .withAvailabilityZones(expectedAvailabilityZones) //
                .withListeners(expectedListener);
        elb.createLoadBalancer(createLoadBalancerRequest);
    }

    @Override
    void registerEC2InstancesForElasticLoadBalancer(String loadBalancerName, List<Instance> ec2Instances) {
        List<com.amazonaws.services.elasticloadbalancing.model.Instance> instances = new ArrayList<com.amazonaws.services.elasticloadbalancing.model.Instance>();
        for (Instance instance : ec2Instances) {
            // terminated and shutting-down instances should not be used
            if(instance.getState().getName().startsWith("terminat") || instance.getState().getName().startsWith("shutting")) {
                LOGGER.debug("Ignore Ec2 instance {} due to state {}",instance.getInstanceId(), instance.getState().getName());
                continue;
            }
            instances.add(new com.amazonaws.services.elasticloadbalancing.model.Instance(instance.getInstanceId()));
        }
        
        LOGGER.debug("Request registration of {} instances to loadbalancer {}", instances.size(), loadBalancerName);

        RegisterInstancesWithLoadBalancerRequest registerInstancesWithLoadBalancerRequest = new RegisterInstancesWithLoadBalancerRequest( //
                loadBalancerName, //
                instances);
        elb.registerInstancesWithLoadBalancer(registerInstancesWithLoadBalancerRequest);
    }

}
