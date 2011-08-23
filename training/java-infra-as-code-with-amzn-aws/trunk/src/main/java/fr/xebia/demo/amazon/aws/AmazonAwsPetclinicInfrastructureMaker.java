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

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.rds.model.DBInstance;

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
public class AmazonAwsPetclinicInfrastructureMaker extends AmazonAwsPetclinicInfrastructureMakerAbstract {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public AmazonAwsPetclinicInfrastructureMaker() {
        // TODO : Get credentials
        // TODO : Declare end points (rds, ec2, elb)
    }

    @Override
    DBInstance createDBInstance(String dbInstanceIdentifier) {
        // TODO Auto-generated method stub
        throw new NotImplementedException("TODO");
    }

    @Override
    List<Instance> createTwoEC2Instances(DBInstance dbInstance, String warUrl) {
        // TODO Auto-generated method stub
        throw new NotImplementedException("TODO");
    }

    @Override
    void createLoadBalancerWithListeners(String loadBalancerName, Listener expectedListener, List<String> expectedAvailabilityZones) {
        // TODO Auto-generated method stub
        throw new NotImplementedException("TODO");
    }

    @Override
    void configureEC2InstancesForElasticLoadBalancer(String loadBalancerName, List<Instance> ec2Instances) {
        // TODO Auto-generated method stub
        throw new NotImplementedException("TODO");
    }

}
