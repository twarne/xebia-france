package fr.xebia.demo.amazon.aws.petclinic.challenge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Nonnull;

import org.jclouds.aws.ec2.reference.AWSEC2Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions.Builder;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.rds.model.DBInstance;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import com.google.inject.Module;

import fr.xebia.demo.amazon.aws.petclinic.CloudInit;

/**
 * @see http://code.google.com/p/jclouds/wiki/ComputeGuide
 */
public class MakerJCloudsChallengeAnswer extends MakerChallengeAnswer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MakerJCloudsChallengeAnswer.class);
    
    @Nonnull
    @Override
    public List<Instance> createTwoEC2Instances(CloudInit cloudInit, DBInstance dbInstance, String warUrl) {
        ComputeServiceContext context = null;
        try {
            context = createComputeServiceContext();
            Template template = createDefaultTemplate(context, cloudInit, dbInstance, warUrl);
            LOGGER.debug("Request creation of 2 Ec2 instances for group {}",getGroup());
            Set<? extends NodeMetadata> nodes = context.getComputeService()
                    .createNodesInGroup(getGroup(), 2, template);
            return nodeMetadataAsInstances(nodes);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    private String getGroup() {
        return "JClouds-" + getTrigram();
    }

    private ComputeServiceContext createComputeServiceContext()
            throws IOException {
        LOGGER.debug("Create JClouds compute service for AWS.");
        AWSCredentials credentials = getCredentials();
        Set<? extends Module> modules = Sets.newHashSet(new SLF4JLoggingModule());
        Properties overrides = new Properties();
        // This filter is mandatory if you want to use
        // Template.imageId("eu-west-1/ami-47cefa33")
        overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY, "virtualization-type=paravirtual;architecture=i386;owner-id=137112412989;state=available;image-type=machine;root-device-type=ebs");
        overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_CC_REGIONS, "eu-west-1");
        return new ComputeServiceContextFactory().createContext("aws-ec2",
                credentials.getAWSAccessKeyId(), credentials.getAWSSecretKey(),
                modules, overrides);
    }

    private Template createDefaultTemplate(ComputeServiceContext context, CloudInit cloudInit,
            DBInstance dbInstance, String warUrl) {
        Template template = context.getComputeService().templateBuilder()//
                .hardwareId(InstanceType.T1_MICRO) //
                .os64Bit(false) //
                .osFamily(OsFamily.AMZN_LINUX)//
                .imageId("eu-west-1/ami-47cefa33") //
                .locationId("eu-west-1")//
                .options(Builder.inboundPorts(22, 8080)) //
                .build();
        template.getOptions().blockUntilRunning(true);
        template.getOptions().as(EC2TemplateOptions.class).userData(cloudInit.createUserDataBuilder(dbInstance, warUrl).buildUserData().getBytes());
        return template;
    }

    private List<Instance> nodeMetadataAsInstances(Set<? extends NodeMetadata> nodes) {
        List<Instance> instances = new ArrayList<Instance>(nodes.size());
        for (NodeMetadata nodeMetadata : nodes) {
            Instance instance = createInstanceFromMetadata(nodeMetadata);
            instances.add(instance);
            LOGGER.debug("JClouds created " + nodeMetadata);
        }
        return instances;
    }

    private Instance createInstanceFromMetadata(final NodeMetadata nodeMetadata) {
        final Instance instance = new Instance();
        instance.setState(new InstanceState());
        instance.getState().setName(InstanceStateName.Pending.name().toLowerCase());
        instance.setInstanceId(nodeMetadata.getProviderId());
        return instance;
    }
    
    public void destroyEC2InstancesByGroup() {
        ComputeServiceContext context = null;
        try {
            context = createComputeServiceContext();
            LOGGER.debug("Request destruction of Ec2 instances for group {}",getGroup());
            context.getComputeService().destroyNodesMatching(NodePredicates.inGroup(getGroup()));
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

}
