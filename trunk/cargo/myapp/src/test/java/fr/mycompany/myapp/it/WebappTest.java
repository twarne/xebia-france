package fr.mycompany.myapp.it;


import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

@Test
public class WebappTest 
{
	
	private Selenium selenium;
	
	@BeforeTest
	public void beforeTests(){
		selenium = new DefaultSelenium("localhost",4444, "*firefox", "http://localhost:9999/myapp");
		selenium.start();
	}
	
	@AfterTest
	public void afterTests(){
		selenium.stop();
		selenium = null;
	}
	
	@Test
    public void testCallIndexPage() throws Exception
    {
    	selenium.open("http://localhost:9999/myapp/index.jsp");
    	selenium.waitForPageToLoad("5000");
    	Assert.assertTrue(selenium.isTextPresent("Hello World!"));
    	Assert.assertEquals("Hello", selenium.getTitle());
    }
}
