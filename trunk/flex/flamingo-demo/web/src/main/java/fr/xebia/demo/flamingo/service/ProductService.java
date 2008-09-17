package fr.xebia.demo.flamingo.service;

import java.util.List;
import fr.xebia.demo.flamingo.ProductDAO;
import fr.xebia.demo.flamingo.Product;

public class ProductService {

    private ProductDAO daoProduct;
	
    public void setDaoProduct(ProductDAO daoProduct) {
        this.daoProduct = daoProduct;
    }
	
    public List getResultList() {
        return this.daoProduct.getResultList();
    }

    public void save(Product entity) {
        this.daoProduct.save(entity);
    }

    public void update(Product entity) {
        this.daoProduct.update(entity);
    }

    public void remove(Product entity) {
        this.daoProduct.remove(entity);
    }
}