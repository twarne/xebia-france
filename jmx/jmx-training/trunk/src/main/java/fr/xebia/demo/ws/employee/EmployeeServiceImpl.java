/*
 * Copyright 2002-2008 the original author or authors.
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
package fr.xebia.demo.ws.employee;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import javax.sql.DataSource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import fr.xebia.demo.backend.zebuggyservice.ZeBuggyPerson;
import fr.xebia.demo.backend.zebuggyservice.ZeBuggyService;
import fr.xebia.demo.backend.zebuggyservice.ZeBuggyServiceException;
import fr.xebia.demo.xml.employee.Employee;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class EmployeeServiceImpl implements EmployeeService, InitializingBean {
    
    private final static Logger logger = Logger.getLogger(EmployeeServiceImpl.class);
    
    private Cache cache;
    
    private DataSource dataSource;
    
    private ExecutorService executorService;
    
    private Random random = new Random();
    
    private ZeBuggyService zeBuggyService;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.cache, "cache can not be null");
        Assert.notNull(this.dataSource, "dataSource can not be null");
        Assert.notNull(this.executorService, "executorService can not be null");
        Assert.notNull(this.zeBuggyService, "zeBuggService can not be null");
    }
    
    public Cache getCache() {
        return cache;
    }
    
    public DataSource getDataSource() {
        return dataSource;
    }
    
    @Override
    public Employee getEmployee(long id) throws EmployeeNotFoundException {
        
        Employee employee = new Employee();
        employee.setId(id);
        
        simulateZeBuggyServiceCalls(employee);
        
        simulateDatabaseCalls(employee);
        
        simulateCacheCalls(employee);
        
        simulateAsynchronousActivity(employee);
        
        return employee;
    }
    
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
    
    public ZeBuggyService getZeBuggyService() {
        return zeBuggyService;
    }
    
    @Override
    public Employee putEmployee(Employee employee) {
        return null;
    }
    
    public void setCache(Cache cache) {
        this.cache = cache;
    }
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void setZeBuggyService(ZeBuggyService zeBuggyService) {
        this.zeBuggyService = zeBuggyService;
    }
    
    private void simulateAsynchronousActivity(Employee employee) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(random.nextInt(200));
                } catch (InterruptedException e) {
                    logger.warn("non blocking " + e);
                }
            }
        };
        try {
            executorService.execute(runnable);
        } catch (RejectedExecutionException e) {
            logger.warn("Non blocking " + e);
        }
    }
    
    private void simulateCacheCalls(Employee employee) {
        Element cacheElement = cache.get(employee.getId());
        if (cacheElement == null) {
            cache.put(new Element(employee.getId(), employee));
        }
    }
    
    private void simulateDatabaseCalls(Employee employee) {
        
        // REGULAR JDBC
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            statement.execute("select 1");
            
            Thread.sleep(random.nextInt(200));
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(connection, statement, null);
        }
        
        // RANDMOLY GENERATE DATASOURCE CONNECTION EXHAUSTION
        if (random.nextInt(50) == 1) {
            List<Connection> connections = new ArrayList<Connection>();
            try {
                connections.add(dataSource.getConnection());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                for (Connection conn : connections) {
                    DbUtils.closeQuietly(conn);
                }
            }
        }
        
    }
    
    private void simulateZeBuggyServiceCalls(Employee employee) throws EmployeeNotFoundException {
        ZeBuggyPerson zeBuggyPerson;
        try {
            zeBuggyPerson = zeBuggyService.find(employee.getId());
        } catch (ZeBuggyServiceException e) {
            throw new RuntimeException(e);
        }
        if (zeBuggyPerson == null) {
            String msg = "No employee found for id ";
            EmployeeNotFoundFault fault = new EmployeeNotFoundFault();
            fault.setDescription(msg);
            throw new EmployeeNotFoundException(msg, fault);
        }
        employee.setLastName(zeBuggyPerson.getLastName());
        employee.setFirstName(zeBuggyPerson.getFirstName());
    }
    
}
