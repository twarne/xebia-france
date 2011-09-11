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
package fr.xebia.demo.amazon.aws.petclinic;

import org.junit.Before;
import org.junit.Test;

import fr.xebia.demo.amazon.aws.petclinic.challenge.MakerChallenge;
import fr.xebia.demo.amazon.aws.petclinic.challenge.MakerChallengeAnswer;
import fr.xebia.demo.amazon.aws.petclinic.challenge.MakerJCloudsChallengeAnswer;

public class InfrastructureMakerDeleteTest {
    private MakerChallenge makerChallenge;
    private InfrastructureMaker maker;
    
    @Before
    public void setup() {
        System.out.println();
        makerChallenge = new MakerChallengeAnswer();
        maker = new InfrastructureMaker(new MakerChallengeAnswer());
    }

    @Test
    public void test_delete_elb() {
        maker.deleteExistingElasticLoadBalancer(makerChallenge.getTrigram());
    }

    @Test
    public void test_terminate_ec2instances() {
        maker.terminateMyAlreadyExistingEC2Instances(makerChallenge.getTrigram());
    }
    
    @Test
    public void test_delete_dbinstances() {
        maker.deleteDBInstance("petclinic-"+makerChallenge.getTrigram());
    }
    
    @Test
    public void test_destroy_ec2_instances_by_group() {
        MakerJCloudsChallengeAnswer jCloudsMaker = new MakerJCloudsChallengeAnswer();
        jCloudsMaker.destroyEC2InstancesByGroup();
    }

}
