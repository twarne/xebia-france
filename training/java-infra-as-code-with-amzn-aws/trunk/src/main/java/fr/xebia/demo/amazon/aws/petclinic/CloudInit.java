package fr.xebia.demo.amazon.aws.petclinic;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.rds.model.DBInstance;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import fr.xebia.cloud.cloudinit.CloudInitUserDataBuilder;
import fr.xebia.cloud.cloudinit.FreemarkerUtils;

public class CloudInit {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudInit.class);
    
    /**
     * Returns the builder that can produces a (base-64) version of the mime-multi-part cloud-init file to put in the user-data attribute of the ec2 instance.
     */
    @Nonnull
    public CloudInitUserDataBuilder createUserDataBuilder(DBInstance dbInstance, String warUrl) {
        Preconditions.checkNotNull(dbInstance, "DbInstance should not be null.");
        Preconditions.checkNotNull(warUrl, "WarUrl should not be null.");
        
        LOGGER.trace("Generating shell script for cloud init.");
        Map<String, Object> rootMap = Maps.newHashMap();
        rootMap.put("catalinaBase", "/usr/share/tomcat6");
        rootMap.put("warUrl", warUrl);
        rootMap.put("warName", "/petclinic.war");
        Map<String, String> systemProperties = Maps.newHashMap();
        rootMap.put("systemProperties", systemProperties);
        
        String jdbcUrl = "jdbc:mysql://" + dbInstance.getEndpoint().getAddress() + ":" + dbInstance.getEndpoint().getPort() + "/" + dbInstance.getDBName();
        systemProperties.put("jdbc.url", jdbcUrl);
        systemProperties.put("jdbc.username", "petclinic");
        systemProperties.put("jdbc.password", "petclinic");

        String shellScript = FreemarkerUtils.generate(rootMap, "/provision_tomcat.py.fmt");

        LOGGER.trace("Configuring cloud init generation with script.");
        InputStream cloudConfigAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("cloud-config-amzn-linux.txt");
        Preconditions.checkNotNull(cloudConfigAsStream, "'" + "cloud-config-amzn-linux.txt" + "' not found in path");
        Readable cloudConfig = new InputStreamReader(cloudConfigAsStream);

        return CloudInitUserDataBuilder.start() //
                .addShellScript(shellScript) //
                .addCloudConfig(cloudConfig);
    }

}
