package fr.xebia.demo.amazon.aws.petclinic.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.google.common.base.Throwables;

/**
 * Wrapper around {@link AmazonEC2}.
 */
public class EC2Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(EC2Client.class);
    
    private final AmazonEC2 ec2;

    public EC2Client(AmazonEC2 ec2) {
        this.ec2 = ec2;
    }

    public void terminateMyAlreadyExistingEC2Instances(String trigram) {
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

    public List<Instance> displayInstancesDetails(String trigram) {
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

    public void tagInstances(List<Instance> instances, String trigram) {
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
    
    public List<Instance> waitForEc2InstancesAvailability(List<Instance> instances) {
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
        return availableInstances;
    }

    
}
