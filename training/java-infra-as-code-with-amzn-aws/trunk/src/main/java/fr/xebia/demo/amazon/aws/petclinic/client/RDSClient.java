package fr.xebia.demo.amazon.aws.petclinic.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DBInstanceNotFoundException;
import com.amazonaws.services.rds.model.DeleteDBInstanceRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.google.common.collect.Iterables;

import fr.xebia.demo.amazon.aws.petclinic.PetclinicInfrastructureMaker;

/**
 * Wrapper around {@link AmazonRDS}.
 */
public class RDSClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetclinicInfrastructureMaker.class);
    
    private final AmazonRDS rds;

    public RDSClient(AmazonRDS rds) {
        this.rds = rds;
    }
    
    @Nullable
    public DBInstance findDBInstance(String dbInstanceIdentifier) {
       LOGGER.debug("Request description for db instance {}.", dbInstanceIdentifier);
        try {
            DescribeDBInstancesResult describeDBInstances = rds.describeDBInstances(new DescribeDBInstancesRequest().withDBInstanceIdentifier(dbInstanceIdentifier));
            return Iterables.getFirst(describeDBInstances.getDBInstances(), null);
        } catch (DBInstanceNotFoundException e) {
            LOGGER.trace("Db instance {} not found.", dbInstanceIdentifier);
            return null;
        }
    }
    
    public void deleteDBInstance(String dbInstanceIdentifier) {
        LOGGER.debug("Request deletion of db instance {}.", dbInstanceIdentifier);
        rds.deleteDBInstance(new DeleteDBInstanceRequest() //
        .withDBInstanceIdentifier(dbInstanceIdentifier) //
        .withSkipFinalSnapshot(true));
    }

    @Nonnull
    public DBInstance waitForDBInstanceAvailability(String dbInstanceIdentifier) {
        LOGGER.debug("Wait for availability of db instance {}.", dbInstanceIdentifier);
        while (true) {
            DBInstance dbInstance = findDBInstance(dbInstanceIdentifier);
            if (dbInstance == null) {
                throw new DBInstanceNotFoundException("No DBInstance " + dbInstanceIdentifier + " exists");
            };
            LOGGER.trace("Db instance {} status : {}", dbInstanceIdentifier, dbInstance.getDBInstanceStatus());
            if ("available".equals(dbInstance.getDBInstanceStatus())) {
                return dbInstance;
            } else {
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    

}
