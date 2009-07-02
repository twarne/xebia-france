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
package fr.xebia.demo.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import fr.xebia.demo.model.Employee;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class EmployeeDaoJdbcImpl implements EmployeeDao, InitializingBean {
    
    private Random random = new Random();
    
    private Cache cache;
    private DataSource dataSource;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.cache, "cache can not be null");
        Assert.notNull(this.dataSource, "dataSource can not be null");
    }
    
    public void setCache(Cache cache) {
        this.cache = cache;
    }
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public Employee getEmployee(long id) {
        Element cacheElement = cache.get(id);
        if (cacheElement == null) {
            Employee employee = loadEmployeeFromDatabase(id);
            cacheElement = new Element(employee.getId(), employee);
            cache.put(cacheElement);
        }
        
        return (Employee)cacheElement.getValue();        
    }
    
    private Employee loadEmployeeFromDatabase(long id) {
        
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            statement.execute("select 1");
            
            Thread.sleep(random.nextInt(400));
            
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
        
        Employee employee = new Employee();
        employee.setId(id);
        employee.setFirstName("Joe");
        employee.setLastName("Johnson-" + id);
        
        return employee;
    }
}
