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
import org.springframework.stereotype.Service;

/**
 *
 * @author martin
 */
@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Optional<Product> findById(Integer id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public boolean update(Product product) {
        return productRepository.update(product);
    }

    @Override
    public Product save(Product product) {
        product.setVersion(1);
        return productRepository.save(product);
    }

    @Override
    public boolean delete(Integer id) {
        return productRepository.delete(id);
    }
    
}
