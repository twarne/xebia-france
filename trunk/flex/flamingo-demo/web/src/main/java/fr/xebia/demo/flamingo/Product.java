package fr.xebia.demo.flamingo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import org.hibernate.validator.Length;

@Entity
public class Product implements Serializable {

	private Integer id;
	private Integer version;
	private String name;
	private String description;
	private Float price;
	private Date avaibility;
			
	@Id @GeneratedValue
	public Integer getId() {
	     return id;
	}

	public void setId(Integer id) {
	     this.id = id;
	}
	
	@Version
	public Integer getVersion() {
	     return version;
	}

	public void setVersion(Integer version) {
	     this.version = version;
	}   	
	
	@Length(max=20)
	public String getName() {
	     return name;
	}

	public void setName(String name) {
	     this.name = name;
	}    
	
	public String getDescription() {
	     return description;
	}

	public void setDescription(String description) {
	     this.description = description;
	}    

	public Float getPrice() {
	     return price;
	}

	public void setPrice(Float price) {
	     this.price = price;
	}    
	
	public Date getAvaibility() {
		return avaibility;
	}

	public void setAvaibility(Date avaibility) {
		this.avaibility = avaibility;
	}

}