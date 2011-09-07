package fr.xebia.demo.amazon.aws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Nonnull;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.rds.model.DBInstance;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import com.google.inject.Module;

public class AmazonAwsPetclinicInfrastructureJCloudsMakerAnswer
        extends
            AmazonAwsPetclinicInfrastructureMakerAnswer {

    @Nonnull
    @Override
    List<Instance> createTwoEC2Instances(DBInstance dbInstance, String warUrl) {
        ComputeServiceContext context = null;
        try {
            context = createComputeServiceContext();
            final Template template = createDefaultTemplate(context,
                    dbInstance, warUrl);
            final Set<? extends NodeMetadata> nodes = context
                    .getComputeService().createNodesInGroup(
                            "PetclinicJCloudsAnswer", 2, template);
            return nodeMetadataAsInstances(nodes);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    private ComputeServiceContext createComputeServiceContext()
            throws IOException {
        final AWSCredentials credentials = getCredentials();
        final Set<? extends Module> modules = Sets
                .newHashSet(new SLF4JLoggingModule());
        Properties overrides = new Properties();
        return new ComputeServiceContextFactory().createContext("aws-ec2",
                credentials.getAWSAccessKeyId(), credentials.getAWSSecretKey(),
                modules, overrides);
    }

    private Template createDefaultTemplate(final ComputeServiceContext context,
            final DBInstance dbInstance, final String warUrl) {
        final Template template = context.getComputeService().templateBuilder()//
                .hardwareId(InstanceType.T1_MICRO) //
                .osFamily(OsFamily.UBUNTU).locationId("eu-west-1")//
                .build();
        template.getOptions().blockUntilRunning(true);
        template.getOptions().as(EC2TemplateOptions.class).keyPair(KEY_PAIR);
        template.getOptions().as(EC2TemplateOptions.class)
                .securityGroups("tomcat");
        template.getOptions()
                .as(EC2TemplateOptions.class)
                .userData(buildCloudInitUserData(dbInstance, warUrl).getBytes());
        return template;
    }

    private List<Instance> nodeMetadataAsInstances(
            Set<? extends NodeMetadata> nodes) {
        final List<Instance> instances = new ArrayList<Instance>(nodes.size());
        for (final NodeMetadata nodeMetadata : nodes) {
            final Instance instance = createInstanceFromMetadata(nodeMetadata);
            instances.add(instance);
            System.out.println("created " + instance.getInstanceId() + " : "
                    + nodeMetadata);
        }
        return instances;
    }

    private Instance createInstanceFromMetadata(final NodeMetadata nodeMetadata) {
        final Instance instance = new Instance();
        instance.setState(new InstanceState());
        instance.getState().setName(
                InstanceStateName.Pending.name().toLowerCase());
        instance.setInstanceId(nodeMetadata.getProviderId());
        return instance;
    }

}
