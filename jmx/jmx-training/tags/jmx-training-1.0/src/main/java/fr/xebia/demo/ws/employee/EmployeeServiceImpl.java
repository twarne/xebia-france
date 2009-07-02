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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import fr.xebia.demo.backend.zeasyncservice.ZeAsyncService;
import fr.xebia.demo.backend.zebuggyservice.ZeBuggyPerson;
import fr.xebia.demo.backend.zebuggyservice.ZeBuggyService;
import fr.xebia.demo.backend.zebuggyservice.ZeBuggyServiceException;
import fr.xebia.demo.dao.EmployeeDao;
import fr.xebia.demo.xml.employee.Employee;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class EmployeeServiceImpl implements EmployeeService, InitializingBean {
    
    private final static Logger logger = Logger.getLogger(EmployeeServiceImpl.class);
    
    private EmployeeDao employeeDao;
    
    private ZeAsyncService zeAsyncService;
    
    private ZeBuggyService zeBuggyService;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.employeeDao, "employeeDao can not be null");
        Assert.notNull(this.zeAsyncService, "zeAsyncService can not be null");
        Assert.notNull(this.zeBuggyService, "zeBuggService can not be null");
    }
    
    @Override
    public Employee getEmployee(long id) throws EmployeeNotFoundException {
        
        Employee employee = new Employee();
        employee.setId(id);
        
        simulateZeBuggyServiceCalls(employee);
        
        employeeDao.getEmployee(id);
        
        zeAsyncService.doAsyncWork(id);
        
        return employee;
    }
    
    public ZeBuggyService getZeBuggyService() {
        return zeBuggyService;
    }
    
    @Override
    public Employee putEmployee(Employee employee) {
        return null;
    }
    
    public void setEmployeeDao(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }
    
    public void setZeAsyncService(ZeAsyncService zeAsyncService) {
        this.zeAsyncService = zeAsyncService;
    }
    
    public void setZeBuggyService(ZeBuggyService zeBuggyService) {
        this.zeBuggyService = zeBuggyService;
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
