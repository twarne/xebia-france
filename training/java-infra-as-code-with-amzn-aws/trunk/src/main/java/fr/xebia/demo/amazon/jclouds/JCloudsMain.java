package fr.xebia.demo.amazon.jclouds;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;

import com.google.common.base.Throwables;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @see http://code.google.com/p/jclouds/wiki/QuickStartAWS
 * @see https
 *      ://github.com/jclouds/jclouds-examples/blob/master/ec2-createlamp/src
 *      /main/java/org/jclouds/examples/ec2/createlamp/MainApp.java
 */
public class JCloudsMain {
    private static final String PROVIDER = "aws-ec2";
    private static final String IMAGE_ID = "ami-47cefa33";
    private static final String LOCATION_ID = "eu-west-1";

    public static void main(final String[] args) {
        new JCloudsMain();
    }

    private String accessKey;
    private String secretKey;
    private String keyPair;

    public JCloudsMain() {
        ComputeServiceContext context = null;
        try {
            loadProperties();
            context = createComputeServiceContext();
            createAndDestroyPetclinic(context);
        } catch (Exception e) {
            Throwables.propagate(e);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    private void createAndDestroyPetclinic(final ComputeServiceContext context)
            throws RunNodesException {
        final ComputeService computeService = context.getComputeService();
        final Template template = createDefaultTemplate(computeService);
        final Set<? extends NodeMetadata> nodes = computeService
                .createNodesInGroup("jclouds-test", 2, template);

        for (final NodeMetadata nodeMetadata : nodes) {
            System.out.println("created node : " + nodeMetadata.getId());
        }

        destroyNodesWithReferences(computeService, nodes);
    }

    /**
     * Load personal information from file.
     */
    private void loadProperties() throws IOException {
        final Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("jclouds-credentials.properties"));
        accessKey = checkNotNull(properties.getProperty("accessKey"));
        secretKey = checkNotNull(properties.getProperty("secretKey"));
        keyPair = checkNotNull(properties.getProperty("keyPair"));
    }

    /**
     * TODO what modules do we need to define?
     */
    private ComputeServiceContext createComputeServiceContext() {
        return new ComputeServiceContextFactory().createContext(PROVIDER,
                accessKey, secretKey);
    }

    /**
     * TODO do we want to use the same image as for the others demos?
     * TODO use cloudinit through userdata
     */
    private Template createDefaultTemplate(final ComputeService computeService) {
        Template template = computeService.templateBuilder().smallest()//
                .osFamily(OsFamily.UBUNTU)
                // .fromImage(new ImageBuilder().id(IMAGE_ID).build())//
                .locationId(LOCATION_ID)//
                .build();
        template.getOptions().as(EC2TemplateOptions.class).keyPair(keyPair);
        template.getOptions().blockUntilRunning(true);
        template.getOptions().tags(Arrays.asList("jclouds-tag"));
        // template.getOptions().as(EC2TemplateOptions.class).userData(unencodedData);
        return template;
    }

    /**
     * Destroy all the provided nodes.
     */
    private void destroyNodesWithReferences(
            final ComputeService computeService,
            final Set<? extends NodeMetadata> nodes) {
        for (final NodeMetadata nodeMetadata : nodes) {
            computeService.destroyNode(nodeMetadata.getId());
            System.out.println("destroying node : " + nodeMetadata.getId());
        }
    }

}
