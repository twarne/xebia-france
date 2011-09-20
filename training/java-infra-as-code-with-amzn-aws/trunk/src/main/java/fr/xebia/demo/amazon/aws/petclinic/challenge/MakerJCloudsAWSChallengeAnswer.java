package fr.xebia.demo.amazon.aws.petclinic.challenge;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.rds.model.DBInstance;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import com.google.inject.Module;
import fr.xebia.demo.amazon.aws.petclinic.CloudInit;
import fr.xebia.demo.amazon.aws.petclinic.JCloudUtil;
import org.jclouds.aws.ec2.reference.AWSEC2Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Use JCloud with Amazon AWS template Option class
 * see http://code.google.com/p/jclouds/wiki/ComputeGuide
 */
public class MakerJCloudsAWSChallengeAnswer extends MakerChallengeAnswer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MakerJCloudsAWSChallengeAnswer.class);
    
    @Nonnull
    @Override
    /**
     * Create two EC2 instances using JClouds and AWS Template class
     */
    public List<Instance> createTwoEC2Instances(CloudInit cloudInit, DBInstance dbInstance, String warUrl) {
        ComputeServiceContext context = null;
        try {
            context = createComputeServiceContext();
            Template template = createDefaultTemplate(context, cloudInit, dbInstance, warUrl);
            LOGGER.debug("Request creation of 2 Ec2 instances for group {}",getGroup());
            Set<? extends NodeMetadata> nodes = context.getComputeService()
                    .createNodesInGroup(getGroup(), 2, template);
            return JCloudUtil.nodeMetadataAsEC2Instances(nodes);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    /**
     * @return The name of the instances
     */
    private String getGroup() {
        return "JClouds-" + getTrigram();
    }

    /**
     * Create an Amazon Context using Jclouds
     * @return An Amazon eu-west-1 context
     * @throws IOException
     */
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

    /**
     * Create a template for creating instances. We are using EC2TemplateOptions class to help us using
     * Amazon specific features
     * @param context The Amazon context
     * @param cloudInit The cloudInit class tobootstrap the instances
     * @param dbInstance The Amazon RDS instance to configure Tomcat in the CloudInit
     * @param warUrl The URL of the Petclinic WAR file
     * @return A template for creating Tomcat Amazon instances
     */
    private Template createDefaultTemplate(ComputeServiceContext context, CloudInit cloudInit,
            DBInstance dbInstance, String warUrl) {
        Template template = context.getComputeService().templateBuilder()//
                .hardwareId(InstanceType.T1_MICRO) //
                .os64Bit(false) //
                .osFamily(OsFamily.AMZN_LINUX)//
                .imageId("eu-west-1/ami-47cefa33") //
                .locationId("eu-west-1")//
                .build();
        template.getOptions().blockUntilRunning(true);
        template.getOptions().as(EC2TemplateOptions.class).keyPair("xebia-france");
        template.getOptions().as(EC2TemplateOptions.class).securityGroups("tomcat");
        template.getOptions().as(EC2TemplateOptions.class).userData(cloudInit.createUserDataBuilder(dbInstance, warUrl).buildUserData().getBytes());
        return template;
    }
}
