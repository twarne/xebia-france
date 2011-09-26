package fr.xebia.demo.amazon.aws.petclinic.challenge.jclouds;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.rds.model.DBInstance;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import com.google.inject.Module;
import fr.xebia.demo.amazon.aws.petclinic.CloudInit;
import fr.xebia.demo.amazon.aws.petclinic.JCloudUtil;
import fr.xebia.demo.amazon.aws.petclinic.challenge.MakerChallengeAnswer;

import org.jclouds.aws.ec2.reference.AWSEC2Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions.Builder;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.io.Payloads;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * see http://code.google.com/p/jclouds/wiki/ComputeGuide
 */
public class MakerJCloudsChallengeAnswer extends MakerChallengeAnswer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MakerJCloudsChallengeAnswer.class);

    @Nonnull
    @Override
    /**
     * Create two EC2 instances using JClouds without using AWS Template class
     */
    public List<Instance> createTwoEC2Instances(CloudInit cloudInit, DBInstance dbInstance, String warUrl) {
        ComputeServiceContext context = null;
        try {
            context = createComputeServiceContext();
            Template template = createDefaultTemplate(context, dbInstance, warUrl);
            LOGGER.debug("Request creation of 2 Ec2 instances for group {}",getGroup());
            Set<? extends NodeMetadata> nodes = context.getComputeService()
                    .createNodesInGroup(getGroup(), 2, template);

            writeSSHKey(nodes);
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
     * Write SSH Keys on file system
     * @param nodes The instances created
     * @throws IOException
     */
    private void writeSSHKey(Set<? extends NodeMetadata> nodes) throws IOException {
        int i = 1;
        for(NodeMetadata node : nodes){
            BufferedWriter out = new BufferedWriter(new FileWriter("~/"+getGroup()+"-"+i+".pem"));
            out.write(node.getCredentials().credential);
            out.close();
            i++;
        }
    }

    /**
     * @return The group of the instances
     */
    private String getGroup() {
        return "JClouds-" + getTrigram();
    }

    /**
     * Create an Amazon Context using Jclouds. We have to add the JschSshClientModule to be able to run bootstrap script
     * @return An Amazon eu-west-1 context
     * @throws IOException
     */
    private ComputeServiceContext createComputeServiceContext()
            throws IOException {
        LOGGER.debug("Create JClouds compute service for AWS.");
        AWSCredentials credentials = getCredentials();
        Set<? extends Module> modules = Sets.newHashSet(new JschSshClientModule(),new SLF4JLoggingModule());
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
     * Create a template for creating instances.<br/>
     * We are opening ports 22 and 80.<br/>
     * We use a shell script to bootstrap the instance and install Petclinic web app
     * @param context The Amazon context
     * @param dbInstance The Amazon RDS instance to configure the Shell script
     * @param warUrl The URL of the Petclinic WAR file
     * @return A template for creating Tomcat Amazon instances
     */
    private Template createDefaultTemplate(ComputeServiceContext context,
            DBInstance dbInstance, String warUrl) {
        Template template = context.getComputeService().templateBuilder()//
                .hardwareId(InstanceType.T1_MICRO) //
                .os64Bit(false) //
                .osFamily(OsFamily.AMZN_LINUX)//
                .imageId("eu-west-1/ami-47cefa33") //
                .locationId("eu-west-1")//
                .options(Builder.inboundPorts(22, 8080)) //
                .options(Builder.runScript(Payloads.newStringPayload(JCloudUtil.bootStrapScript(dbInstance, warUrl))))
                .build();
        template.getOptions().blockUntilRunning(true);
        return template;
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
