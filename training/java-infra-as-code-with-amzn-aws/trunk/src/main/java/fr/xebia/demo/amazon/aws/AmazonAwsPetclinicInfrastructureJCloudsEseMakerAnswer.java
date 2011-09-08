package fr.xebia.demo.amazon.aws;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Nonnull;

import org.jclouds.aws.ec2.reference.AWSEC2Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ComputeMetadataBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.rds.model.DBInstance;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Module;

import fr.xebia.cloud.cloudinit.CloudInitUserDataBuilder;
import fr.xebia.cloud.cloudinit.FreemarkerUtils;

public class AmazonAwsPetclinicInfrastructureJCloudsEseMakerAnswer {

    private static String KEY_PAIR = "eservent";
    private static String secretkey = "qi/I+uRF55FGsXsvlX63U+AdvZE/28aZTazg6TzR";
    private static String accesskeyid = "AKIAILUOOADHXTKEYILQ";

    private static final String PROVIDER_EC2 = "aws-ec2";

    public AmazonAwsPetclinicInfrastructureJCloudsEseMakerAnswer() {

        //http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-query-DescribeImages.html
        Properties overrides = new Properties(); // set owners to nothing
        overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY, "architecture=i386;"); // This filter is mandatory if you want to use Template.imageId("us-east-1/ami-8c1fece5")
        overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_CC_REGIONS, "us-east-1");
        ComputeServiceContext context = new ComputeServiceContextFactory() //
                .createContext(PROVIDER_EC2, //
                        accesskeyid, //
                        secretkey, //
                        ImmutableSet.of(new SLF4JLoggingModule()), //
                        overrides);

        
        // when you need access to very ec2-specific features, use the provider-specific context
        EC2Client ec2Client = EC2Client.class.cast(context.getProviderSpecificContext().getApi());

        Set<? extends Image> images = new HashSet();
        //Set<? extends Image> images = context.getComputeService().listImages();
        System.out.println("EC2 IMAGES");
        System.out.println("=============");

        for (Image image : images) {
            System.out.println("Id :" + image.getId() + " Platform : " + image.getOperatingSystem());
        }

        System.out.println("=============");


        System.out.println("TEMPLATE");
        System.out.println("=============");
        Template template = context.getComputeService().templateBuilder() //
                .hardwareId(InstanceType.T1_MICRO) //
                .imageId("us-east-1/ami-8c1fece5") //
                .osFamily(OsFamily.AMZN_LINUX) //
                .os64Bit(false) //
                .imageNameMatches("petclinic-" + "ese") //
                .build();

        System.out.println("=============");

        //template.getOptions().blockUntilRunning(true);
        template.getOptions().as(EC2TemplateOptions.class).securityGroups("tomcat");
        template.getOptions().as(EC2TemplateOptions.class).keyPair(KEY_PAIR);
        List<String> tags = new ArrayList<String>();
        tags.add("Name=petclinic-" + "ese" + "-" + 1);  // Doesn't work
        template.getOptions().as(EC2TemplateOptions.class).tags(tags);
        template.getOptions().as(EC2TemplateOptions.class).userData(buildCloudInitUserData().getBytes());
        


        System.out.println("NODES");
        System.out.println("=============");
        try {
            final Set<? extends NodeMetadata> nodes = context.getComputeService().createNodesInGroup("jclouds", 2, template);

            System.out.println("EC2 INSTANCES");
            System.out.println("=============");

            int i =1;
            for (NodeMetadata node : nodes) {
                Map<String, String> userMetadata = node.getUserMetadata();
                userMetadata.put("Name", "petclinic-" + "ese" + "-" + i++);  // Doesn't work
                String name = node.getName();
                System.out.println("Name : " + name + " http://" + node.getPublicAddresses() + ":8080" + "/petclinic");
            }

            System.out.println("=============");
        } catch (RunNodesException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        context.getComputeService().destroyNodesMatching(NodePredicates.inGroup("jclouds"));

        context.close();
    }

    @Nonnull
    List<Instance> createTwoEC2Instances(DBInstance dbInstance, String warUrl) {
        ComputeServiceContext context = null;
        try {
            context = createComputeServiceContext();
            final Template template = createDefaultTemplate(context, dbInstance, warUrl);
            final Set<? extends NodeMetadata> nodes = context.getComputeService().createNodesInGroup("PetclinicJCloudsAnswer", 2, template);
            return nodeMetadataAsInstances(nodes);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    private ComputeServiceContext createComputeServiceContext() throws IOException {
        final AWSCredentials credentials = getCredentials();
        final Set<? extends Module> modules = Sets.newHashSet(new SLF4JLoggingModule());
        Properties overrides = new Properties();
        return new ComputeServiceContextFactory().createContext("aws-ec2", credentials.getAWSAccessKeyId(), credentials.getAWSSecretKey(), modules, overrides);
    }

    private AWSCredentials getCredentials() {
        // TODO Auto-generated method stub
        return null;
    }

    private Template createDefaultTemplate(final ComputeServiceContext context, final DBInstance dbInstance, final String warUrl) {
        final Template template = context.getComputeService().templateBuilder()//
                .hardwareId(InstanceType.T1_MICRO) //
                .osFamily(OsFamily.UBUNTU).locationId("eu-west-1")//
                .build();
        template.getOptions().blockUntilRunning(true);
        template.getOptions().as(EC2TemplateOptions.class).keyPair(KEY_PAIR);
        template.getOptions().as(EC2TemplateOptions.class).securityGroups("tomcat");
        template.getOptions().as(EC2TemplateOptions.class).userData(buildCloudInitUserData().getBytes());
        return template;
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
    String buildCloudInitUserData() {

        String warUrl ="/petclinic";
        // SHELL SCRIPT
        Map<String, Object> rootMap = Maps.newHashMap();
        rootMap.put("catalinaBase", "/usr/share/tomcat6");
        rootMap.put("warUrl", warUrl);
        rootMap.put("warName", "/petclinic.war");

        Map<String, String> systemProperties = Maps.newHashMap();
        rootMap.put("systemProperties", systemProperties);
        String jdbcUrl = "jdbc:mysql://"; //+ dbInstance.getEndpoint().getAddress() + ":" + dbInstance.getEndpoint().getPort() + "/" + dbInstance.getDBName();
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
                .buildUserData();
    }

    private List<Instance> nodeMetadataAsInstances(Set<? extends NodeMetadata> nodes) {
        final List<Instance> instances = new ArrayList<Instance>(nodes.size());
        for (final NodeMetadata nodeMetadata : nodes) {
            final Instance instance = createInstanceFromMetadata(nodeMetadata);
            instances.add(instance);
            System.out.println("created " + instance.getInstanceId() + " : " + nodeMetadata);
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

}
