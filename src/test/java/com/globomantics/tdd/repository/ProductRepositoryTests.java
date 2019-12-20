/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.globomantics.tdd.repository;

import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import com.globomantics.tdd.model.Product;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 *
 * @author martin
 */
@ExtendWith({SpringExtension.class, DBUnitExtension.class})
@ActiveProfiles("test")
@SpringBootTest

public class ProductRepositoryTests {
    
    @Autowired
    private DataSource dataSource;

    @Autowired
    private ProductRepository repository;

    public ConnectionHolder getConnectionHolder() {
        // Return a function that retrieves a connection from our data source
        return () -> dataSource.getConnection();
    }
    
    @Test
    @DataSet("products.yml")
    void testFindAll() {
        List<Product> products = repository.findAll();
        
        Assertions.assertEquals(2,products.size(), "we should get 2 products in our database");
    }
    
    @Test
    @DataSet("products.yml")
    void testFindByIdSuccess() {
        Optional<Product> returnedProduct = repository.findById(1);
        
        Assertions.assertTrue(returnedProduct.isPresent(), "the product with id 1 should be found");
    }
    
    @Test
    @DataSet("products.yml")
    void testFindByIdNotFound() {
        Optional<Product> returnedProduct = repository.findById(3);
        
        Assertions.assertFalse(returnedProduct.isPresent(), "the product with id 3 should not be found");
    }
    
    @Test
    @DataSet("products.yml")
    void testSaveProduct() {
        Product mockProduct = new Product(3, "product name 3", 5, 1);
        Product returnedProduct = repository.save(mockProduct);
        
        Assertions.assertEquals("product name 3", returnedProduct.getName(), "the product name should be product name 3");
        Assertions.assertEquals(5, returnedProduct.getQuantity(), "the product quantity should be 5");
        
        Optional<Product> loadedProduct = repository.findById(returnedProduct.getId());
        Assertions.assertTrue(loadedProduct.isPresent(), "could not reload the product rom the database");
        Assertions.assertEquals("product name 3", loadedProduct.get().getName(), "the product id should be product name 3");
        Assertions.assertEquals(5, loadedProduct.get().getQuantity(), "the product quantity should be 5");
    }
    
    @Test
    @DataSet("products.yml")
    void testUpdateProductSuccess() {
        Product mockProduct = new Product(2, "product name 2", 34, 2);
        boolean returnedProduct = repository.update(mockProduct);
        
        Assertions.assertTrue(returnedProduct, "The product should have been updated successfully");
        
        Optional<Product> loadedProduct = repository.findById(2);
        Assertions.assertTrue(loadedProduct.isPresent(), "could not reload the product rom the database");
        Assertions.assertEquals("product name 2", loadedProduct.get().getName(), "the product id should be product name 2");
        Assertions.assertEquals(34, loadedProduct.get().getQuantity(), "the product quantity should be 34");
        Assertions.assertEquals(2, loadedProduct.get().getVersion(), "the product version should be 2");
    }
    
    @Test
    @DataSet("products.yml")
    void testUpdateProductFailed() {
        Product mockProduct = new Product(5, "product name 5", 20, 2);
        boolean returnedProduct = repository.update(mockProduct);
        
        Assertions.assertFalse(returnedProduct, "The product should not have been updated successfully");
    }
}
