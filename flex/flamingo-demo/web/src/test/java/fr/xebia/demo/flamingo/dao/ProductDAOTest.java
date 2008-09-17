package fr.xebia.demo.flamingo.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.xebia.demo.flamingo.DataSourceTestCase;
import fr.xebia.demo.flamingo.Product;
import fr.xebia.demo.flamingo.ProductDAO;

public class ProductDAOTest extends DataSourceTestCase {

    @Autowired
    private ProductDAO entityDAO;

    private static String[] TEST_TABLES = {"Product"};

    @Test
    public void testFindAll() {
	Product entity = new Product();
	entity.setName("test");
	entityDAO.save(entity);

	List<Product> allEntitys = entityDAO.getResultList();
	Assert.assertTrue(allEntitys.contains(entity), "Product 'test' should be returned by getResultList method");
    }

    protected String[] getTestedTables() {
	return TEST_TABLES;
    }

    protected String getTestDataScript() {
	return "test-data-person.sql";
    }
}