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

import java.util.Set;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Location;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.ssh.jsch.config.JschSshClientModule;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

public class AmazonAwsInfraBuilderJcloudsImpl {

    private static String group1;
    private static String keyPair;
    private static String secretkey;
    private static String accesskeyid;

    public static void main(String[] args) throws RunNodesException {
        // get a context with ec2 that offers the portable ComputeService api
        ComputeServiceContext context = new ComputeServiceContextFactory().createContext("aws-ec2", accesskeyid ,secretkey,
                                  ImmutableSet.<Module> of(new SLF4JLoggingModule(), new JschSshClientModule()));

        // here's an example of the portable api
        Set<? extends Location> locations = context.getComputeService().listAssignableLocations();

        Set<? extends Image> images = context.getComputeService().listImages();

        // pick the highest version of the RightScale CentOs template
        Template template = context.getComputeService().templateBuilder().osFamily(OsFamily.AMZN_LINUX).build();

        // specify your own groups which already have the correct rules applied
        template.getOptions().as(EC2TemplateOptions.class).securityGroups(group1);

        // specify your own keypair for use in creating nodes
        template.getOptions().as(EC2TemplateOptions.class).keyPair(keyPair);

        // run a couple nodes accessible via group
        Set<? extends NodeMetadata> nodes = context.getComputeService().createNodesInGroup("webserver", 2, template);

        // when you need access to very ec2-specific features, use the provider-specific context
        EC2Client ec2Client = EC2Client.class.cast(context.getProviderSpecificContext().getApi());

        // ex. to get an ip and associate it with a node
        NodeMetadata node = Iterables.get(nodes, 0);
        String ip = ec2Client.getElasticIPAddressServices().allocateAddressInRegion(node.getLocation().getId());
        ec2Client.getElasticIPAddressServices().associateAddressInRegion(node.getLocation().getId(),ip, node.getProviderId());

        context.close();
    }
}
