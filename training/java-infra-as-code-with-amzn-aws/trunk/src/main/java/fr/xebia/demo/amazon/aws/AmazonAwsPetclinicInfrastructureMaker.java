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
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerListenersRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerPolicyRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerNotFoundException;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DBInstanceNotFoundException;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

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
 * <li>2 Tomcat / xebia-pet-clinic servers connected to the mysql database (connected via the injection of the jdbc parameters in catalina.properties via cloud-init)</li>
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

    private AmazonRDS rds;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public AmazonAwsPetclinicInfrastructureMaker() {
            
        // TODO : Get credentials
        // TODO : Declare end points
    }

    @Nullable
    DBInstance findDBInstance(String dbInstanceIdentifier) {
        try {
            DescribeDBInstancesResult describeDBInstances = rds.describeDBInstances(new DescribeDBInstancesRequest().withDBInstanceIdentifier(dbInstanceIdentifier));
            return Iterables.getFirst(describeDBInstances.getDBInstances(), null);
        } catch (DBInstanceNotFoundException e) {
            return null;
        }
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

        //TODO : Create DB Instance
        
        System.out.println("DB INSTANCE");
        System.out.println("=============");
        System.out.println("MySQL instance : " + dbInstance.getEndpoint().getAddress() + ":" + dbInstance.getEndpoint().getPort());
        return dbInstance;
    }

    List<Instance> terminateExistingAndCreateNewInstance(String trigram) {
        List<Instance> instances = new ArrayList<Instance>();

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

        return instances;

    }
    
    //TODO : Create EC2 Instances and tag them

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

        //TODO : Create Load Balancer
        
        //TODO : Set Availability Zone
        //TODO : HealthCheck
        //TODO : Cookie Stickiness
        //TODO : Policy
        
        // EC2 INSTANCES
        List<com.amazonaws.services.elasticloadbalancing.model.Instance> elbInstances = new ArrayList<com.amazonaws.services.elasticloadbalancing.model.Instance>();
        elbInstances.add(new com.amazonaws.services.elasticloadbalancing.model.Instance(ec2Instances.get(0).getInstanceId()));
        elbInstances.add(new com.amazonaws.services.elasticloadbalancing.model.Instance(ec2Instances.get(1).getInstanceId()));

        RegisterInstancesWithLoadBalancerRequest registerInstancesWithLoadBalancerRequest = new RegisterInstancesWithLoadBalancerRequest( //
                loadBalancerName, //
                elbInstances);
        elb.registerInstancesWithLoadBalancer(registerInstancesWithLoadBalancerRequest);

        LoadBalancerDescription elasticLoadBalancerDescription = elb.describeLoadBalancers(new DescribeLoadBalancersRequest(Arrays.asList(loadBalancerName))).getLoadBalancerDescriptions().get(0);

        System.out.println("LOAD BALANCER");
        System.out.println("=============");
        System.out.println("http://" + elasticLoadBalancerDescription.getDNSName() + "/petclinic");

        return elasticLoadBalancerDescription;
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
