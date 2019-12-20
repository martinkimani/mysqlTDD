package com.globomantics.tdd.controllers;

import com.globomantics.tdd.model.Product;
import com.globomantics.tdd.services.ProductService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author martin
 */
@RestController
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping("/product/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Integer id) {
        return productService.findById(id)
                .map(product -> {
                    try {
                        return ResponseEntity
                                .ok()
                                .eTag(Integer.toString(product.getVersion()))
                                .location(new URI("/product/"+product.getId()))
                                .body(product);
            } catch (URISyntaxException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/products")
    public Iterable<Product> getProducts(){
     return productService.findAll();
    }
    
    @PostMapping("/product")
    public ResponseEntity createProduct(@RequestBody Product product) {
        Product newprod = productService.save(product);
        try {
            return ResponseEntity
                    .created(new URI("/product/"+newprod.getId()))
                    .eTag(Integer.toString(newprod.getId()))
                    .body(newprod);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/product/{id}")
    public ResponseEntity<?> updateProduct(@RequestBody Product product,
                                           @PathVariable Integer id,
                                           @RequestHeader("If-Match") Integer ifMatch) {

        // Get the existing product
        Optional<Product> existingProduct = productService.findById(id);

        return existingProduct.map(p -> {
            // Compare the etags
            if (p.getVersion() != ifMatch) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // Update the product
            p.setName(product.getName());
            p.setQuantity(product.getQuantity());
            p.setVersion(p.getVersion() + 1);
            
            try {
                // Update the product and return an ok response
                if (productService.update(p)) {
                    return ResponseEntity.ok()
                            .location(new URI("/product/" + p.getId()))
                            .eTag(Integer.toString(p.getVersion()))
                            .body(p);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (URISyntaxException e) {
                // An error occurred trying to create the location URI, return an error
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        }).orElse(ResponseEntity.notFound().build());
    }
}
