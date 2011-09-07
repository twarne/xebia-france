/*
 * Copyright 2008-2010 Xebia and the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package fr.xebia.cloud.cloudinit;

import java.io.InputStreamReader;

import org.junit.Test;

public class CloudInitUserDataBuilderTest {

    @Test
    public void generate_user_data_for_tomcat6_amzn_linux() {
        generateUserDataFor("tomcat6-amzn-linux", //
                "cloud-config-amzn-linux.txt", //
                "provision_petclinic_tomcat6-amzn-linux.py");
    }

    @Test
    public void generate_user_data_for_tomcat6_ubuntu() {
        generateUserDataFor("tomcat6-ubuntu", //
                "cloud-config-ubuntu-11.04.txt", //
                "provision_petclinic_tomcat6-ubuntu.py");
    }

    public void generateUserDataFor(final String name,
            final String cloudConfig, final String shellScript) {
        System.out.println("\n\n");
        System.out.println(name);
        System.out.println("###############");
        
        InputStreamReader cloudConfigReader = getResourceAsInputStream(cloudConfig);
        InputStreamReader shellScriptReader = getResourceAsInputStream(shellScript);

        String userData = CloudInitUserDataBuilder.start() //
                .addCloudConfig(cloudConfigReader) //
                .addShellScript(shellScriptReader) //
                .buildUserData();
        
        System.out.println(userData);
    }

    private InputStreamReader getResourceAsInputStream(String string) {
        return new InputStreamReader(Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(string));
    }

}
