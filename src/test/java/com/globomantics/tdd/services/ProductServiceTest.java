/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.globomantics.tdd.services;

import com.globomantics.tdd.model.Product;
import com.globomantics.tdd.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.AssertTrue;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;


/**
 *
 * @author martin
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ProductServiceTest {
    
    @Autowired
    private ProductService service;
    
    @MockBean
    private ProductRepository repository;
    
    @Test
    @DisplayName("Test findById Success")
    void findByIdTest() throws Exception {
        Product mockProduct = new Product(1, "new product", 10, 1);
        doReturn(Optional.of(mockProduct)).when(repository).findById(1);
        
        Optional<Product> returnedProduct = repository.findById(1);
        
        Assertions.assertTrue(returnedProduct.isPresent(), "product not found");
        Assertions.assertSame(returnedProduct.get(), mockProduct, "Products should be same");
    }
    
    @Test
    @DisplayName("Test findById Not Found")
    void findByIdNotFoundTest() throws Exception {
        Product mockProduct = new Product(1, "new product", 10, 1);
        doReturn(Optional.empty()).when(repository).findById(1);
        
        Optional<Product> returnedProduct = repository.findById(1);
        
        Assertions.assertFalse(returnedProduct.isPresent(), "product was found when it shouldnt be");
    }
    
    @Test
    @DisplayName("Test findAll")
    void testFindAll() {
        // Setup our mock
        Product mockProduct = new Product(1, "Product Name", 10, 1);
        Product mockProduct2 = new Product(2, "Product Name 2", 15, 3);
        doReturn(Arrays.asList(new Product[]{mockProduct,mockProduct2})).when(repository).findAll();

        // Execute the service call
        List<Product> products = service.findAll();

        Assertions.assertEquals(2, products.size(), "findAll should return 2 products");
    }
    
    @Test
    @DisplayName("Test save product")
    void saveProductTest() throws Exception {
        Product mockProduct = new Product(1, "new product", 10, 1);
        doReturn(mockProduct).when(repository).save(any());
        
        Product returnedProduct = repository.save(mockProduct);
        
        Assertions.assertNotNull(returnedProduct, "product was found when it shouldnt be");
    }
    
   @Test
    @DisplayName("Test update product")
    void updateProductTest() throws Exception {
        Product mockProduct = new Product(1, "new product", 12, 2);
        doReturn(true).when(repository).update(any());
        doReturn(Optional.of(mockProduct)).when(repository).findById(1);
        
        boolean returnedProduct = repository.update(mockProduct);
        Assertions.assertTrue(returnedProduct, "product was not updated");
        
        Optional<Product> loadedProduct = repository.findById(1);
        
        Assertions.assertTrue(loadedProduct.isPresent(), "product not found");
        Assertions.assertSame(loadedProduct.get(), mockProduct, "Products should be same");
    } 
    
}
