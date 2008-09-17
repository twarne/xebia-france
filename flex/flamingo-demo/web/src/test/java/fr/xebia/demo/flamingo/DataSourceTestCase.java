package fr.xebia.demo.flamingo;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.IHookCallBack;

@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public abstract class DataSourceTestCase extends AbstractTransactionalTestNGSpringContextTests {

    public void run(IHookCallBack arg0) {
	deleteFromTables(getTestedTables());
	executeSqlScript(getTestDataScript(), true);
    }
    
    protected abstract String[] getTestedTables();

    protected abstract String getTestDataScript();
}