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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jclouds.ec2.domain.InstanceType;
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
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DBInstanceNotFoundException;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

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
    public static void main(String[] args) throws Exception {
        AmazonAwsPetclinicInfrastructureMaker infrastructureMaker = new AmazonAwsPetclinicInfrastructureMaker();

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

    @Nullable
    DBInstance findDBInstance(String dbInstanceIdentifier) {
        try {
            DescribeDBInstancesResult describeDBInstances = rds.describeDBInstances(new DescribeDBInstancesRequest()
                    .withDBInstanceIdentifier(dbInstanceIdentifier));
            return Iterables.getFirst(describeDBInstances.getDBInstances(), null);
        } catch (DBInstanceNotFoundException e) {
            return null;
        }
    }

    @Nonnull
    DBInstance createDBInstance(String dbInstanceIdentifier) {
        DBInstance createDBInstance = rds.createDBInstance( //
                new CreateDBInstanceRequest() //
                        .withDBInstanceIdentifier(dbInstanceIdentifier) //
                        .withDBName("petclinic") //
                        .withDBInstanceClass("db.m1.small") //
                        .withEngine("MySQL") //
                        .withMasterUsername("petclinic") //
                        .withMasterUserPassword("petclinic") //
                        .withDBSecurityGroups("default") //
                        .withAllocatedStorage(5 /* Go */) //
                        .withBackupRetentionPeriod(0));
        return createDBInstance;
    }

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

        return waitForDBInstanceAvailability(dbInstanceIdentifier);
    }

    List<Instance> terminateExistingAndCreateNewInstance(String trigram) {
        terminateMyAlreadyExistingEC2Instances(trigram);
        List<Instance> instances = createTwoEC2Instances();
        tagInstances(instances, trigram);
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
    List<Instance> createTwoEC2Instances() {

        RunInstancesResult runInstances = ec2.runInstances( //
                new RunInstancesRequest() //
                        .withImageId("ami-47cefa33") //
                        .withMinCount(2) //
                        .withMaxCount(2) //
                        .withSecurityGroups("tomcat") //
                        .withKeyName("xebia-france") //
                        .withInstanceType(InstanceType.T1_MICRO) //
                );
        Reservation reservation = runInstances.getReservation();
        return reservation.getInstances();
    }

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

}
