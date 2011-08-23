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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.Reservation;
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
public class AmazonAwsPetclinicInfrastructureMakerAnswer extends AmazonAwsPetclinicInfrastructureMakerAbstract {

    public AmazonAwsPetclinicInfrastructureMakerAnswer() {
        try {
            InputStream credentialsAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("AwsCredentials.properties");
            Preconditions.checkNotNull(credentialsAsStream, "File 'AwsCredentials.properties' NOT found in the classpath");
            AWSCredentials credentials = new PropertiesCredentials(credentialsAsStream);
            
            rds = new AmazonRDSClient(credentials);
            rds.setEndpoint("rds.us-east-1.amazonaws.com"); // rds.eu-west-1.amazon.com
            
            ec2 = new AmazonEC2Client(credentials);
            ec2.setEndpoint("ec2.us-east-1.amazonaws.com"); // ec2.eu-west-1.amazon.com
            
            elb = new AmazonElasticLoadBalancingClient(credentials);
            elb.setEndpoint("elasticloadbalancing.us-east-1.amazonaws.com"); // elasticloadbalancing.eu-west-1.amazon.com
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Nonnull
    @Override
    DBInstance createDBInstance(String dbInstanceIdentifier) {
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
        DBInstance createDBInstance = rds.createDBInstance(dbInstanceRequest);

        return createDBInstance;
    }

    @Nonnull
    @Override
    List<Instance> createTwoEC2Instances() {
        DBInstance dbInstance = findDBInstance("petclinic-xeb");
        String warUrl = "http://xebia-france.googlecode.com/svn/repository/maven2/fr/xebia/demo/xebia-petclinic/1.0.2/xebia-petclinic-1.0.2.war";

        RunInstancesRequest runInstanceRequest = new RunInstancesRequest() //
                .withImageId("ami-8c1fece5") // eu-west : ami-47cefa33; us-east :ami-8c1fece5
                .withMinCount(2) //
                .withMaxCount(2) //
                .withSecurityGroups("tomcat") //
                .withKeyName("xebia-france") //
                .withInstanceType(InstanceType.T1Micro.toString()) //
                .withUserData(buildCloudInitUserData(dbInstance, warUrl)) // CloudInit Deployment
        ;
        RunInstancesResult runInstances = ec2.runInstances(runInstanceRequest);
        Reservation reservation = runInstances.getReservation();
        return reservation.getInstances();
    }
    
    @Override
    void createLoadBalancerWithListeners(String loadBalancerName, Listener expectedListener, List<String> expectedAvailabilityZones) {
        CreateLoadBalancerRequest createLoadBalancerRequest = new CreateLoadBalancerRequest() //
                .withLoadBalancerName(loadBalancerName) //
                .withAvailabilityZones(expectedAvailabilityZones) //
                .withListeners(expectedListener);

        elb.createLoadBalancer(createLoadBalancerRequest);
    }

    @Override
    void configureEC2InstancesForElasticLoadBalancer(String loadBalancerName, List<Instance> ec2Instances) {
        List<com.amazonaws.services.elasticloadbalancing.model.Instance> elbInstances = new ArrayList<com.amazonaws.services.elasticloadbalancing.model.Instance>();
        elbInstances.add(new com.amazonaws.services.elasticloadbalancing.model.Instance(ec2Instances.get(0).getInstanceId()));
        elbInstances.add(new com.amazonaws.services.elasticloadbalancing.model.Instance(ec2Instances.get(1).getInstanceId()));

        RegisterInstancesWithLoadBalancerRequest registerInstancesWithLoadBalancerRequest = new RegisterInstancesWithLoadBalancerRequest( //
                loadBalancerName, //
                elbInstances);
        elb.registerInstancesWithLoadBalancer(registerInstancesWithLoadBalancerRequest);
    }

}
