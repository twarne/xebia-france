package fr.xebia.demo.amazon.aws.petclinic;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.rds.model.DBInstance;
import org.jclouds.compute.domain.NodeMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utility class for JCloud
 */
public class JCloudUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JCloudUtil.class);

    /**
     * Return a Shell script as a String<br/>
     * This script install Java & Tomcat, download and configure the Petclinic Web app
     * @param dbInstance Amazon DB Instance. Use to write the JDBC MySQL url
     * @param warUrl The URL of the petclinic war
     * @return A shell script to run at the first boot of an cloud instance
     */
    public static String bootStrapScript(DBInstance dbInstance, String warUrl){
        StringBuilder sb = new StringBuilder();
        //Install Java and Tomcat
        sb.append("/usr/bin/yum -y install java-1.6.0-openjdk tomcat6\n");
        //Start tomcat at startup
        sb.append("/sbin/chkconfig tomcat6 on\n");


        if(dbInstance != null){
            String jdbcUrl = "jdbc:mysql://" + dbInstance.getEndpoint().getAddress() + ":" + dbInstance.getEndpoint().getPort() + "/" + dbInstance.getDBName();

            sb.append("/bin/echo \"# PETCLINIC ENVIRONMENT VARIABLES\n");
            sb.append("jdbc.driverClassName=com.mysql.jdbc.Driver\n");
            sb.append("jdbc.url=").append(jdbcUrl).append("\n");
            sb.append("jdbc.username=petclinic\n");
            sb.append("jdbc.password=petclinic\n");

            sb.append("# Properties that control the population of schema and data for a new data source\n");
            sb.append("jdbc.initLocation=classpath:db/mysql/initDB.txt\n");
            sb.append("jdbc.dataLocation=classpath:db/mysql/populateDB.txt\n");

            sb.append("# Property that determines which Hibernate dialect to use\n");
            sb.append("# (only applied with \"applicationContext-hibernate.xml\")\n");
            sb.append("hibernate.dialect=org.hibernate.dialect.MySQLDialect\n");
            sb.append("# Property that determines which database to use with an AbstractJpaVendorAdapter\n");
            sb.append("jpa.database=MYSQL\n");
            sb.append("\">>/usr/share/tomcat6/conf/catalina.properties\n");

        }
        //Install petclinic
        sb.append("/usr/bin/wget http://xebia-france.googlecode.com/svn/repository/maven2/fr/xebia/demo/xebia-petclinic/1.0.2/xebia-petclinic-1.0.2.war -O /usr/share/tomcat6/webapps/petclinic.war\n");

        //Restart tomcat
        sb.append("/sbin/service tomcat6 restart\n");
        return sb.toString();
    }

    /**
     * Map Jcloud NodeMetaData to Amazon EC2 instance object
     * @param nodes A list of JCloud instances
     * @return A List of EC2 instances
     */
    public static List<Instance> nodeMetadataAsEC2Instances(Set<? extends NodeMetadata> nodes) {
        List<Instance> instances = new ArrayList<Instance>(nodes.size());
        for (NodeMetadata nodeMetadata : nodes) {
            Instance instance = createInstanceFromMetadata(nodeMetadata);
            instances.add(instance);
            LOGGER.debug("JClouds created " + nodeMetadata);
        }
        return instances;
    }

    private static Instance createInstanceFromMetadata(final NodeMetadata nodeMetadata) {
        final Instance instance = new Instance();
        instance.setState(new InstanceState());
        instance.getState().setName(InstanceStateName.Pending.name().toLowerCase());
        instance.setInstanceId(nodeMetadata.getProviderId());
        return instance;
    }
}
