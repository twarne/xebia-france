package fr.xebia.demo.ws.springws.employee;

import javax.xml.bind.annotation.XmlRootElement;

import fr.xebia.demo.xml.employee.Employee;

@XmlRootElement(namespace = "http://demo.xebia.fr/rest/employee", name = "element")
public class EmployeeElement {
    
    private fr.xebia.demo.xml.employee.Employee employee;
    
    public EmployeeElement() {
        super();
    }
    
    public EmployeeElement(Employee employee) {
        this();
        this.employee = employee;
    }
    
    public fr.xebia.demo.xml.employee.Employee getEmployee() {
        return employee;
    }
    
    public void setEmployee(fr.xebia.demo.xml.employee.Employee employee) {
        this.employee = employee;
    }
}
