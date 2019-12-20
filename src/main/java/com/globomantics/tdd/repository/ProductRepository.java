/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.globomantics.tdd.repository;

import com.globomantics.tdd.model.Product;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author martin
 */
public interface ProductRepository {
    
    Optional<Product> findById(Integer id);
    
    List<Product> findAll();
    
    boolean update(Product product);
    
    Product save(Product product);
    
    boolean delete(Integer id);
}
