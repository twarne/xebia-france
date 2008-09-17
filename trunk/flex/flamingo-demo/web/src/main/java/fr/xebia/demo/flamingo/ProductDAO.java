package fr.xebia.demo.flamingo;

import java.util.List;

public interface ProductDAO {

    List getResultList();

    void save(Product entity);

    void update(Product entity);

    void remove(Product entity);
}