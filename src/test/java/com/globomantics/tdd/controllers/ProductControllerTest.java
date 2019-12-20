/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.globomantics.tdd.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.globomantics.tdd.model.Product;
import com.globomantics.tdd.services.ProductService;
import java.util.Optional;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


/**
 *
 * @author martin
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {
    
    @MockBean
    private ProductService service;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @DisplayName("GET /product/1 -Found")
    void testGetProductByIdFound() throws Exception {
        Product mockproduct = new Product(1, "product name", 10, 1);
        doReturn(Optional.of(mockproduct)).when(service).findById(1);
        
        mockMvc.perform(get("/product/{id}",1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/product/1"))
                
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("product name")))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.version", is(1)));
                
    }
    
    @Test
    @DisplayName("GET /product/1 -Not Found")
    void testGetProductByIdNotFound() throws Exception {
        doReturn(Optional.empty()).when(service).findById(1);
        
        mockMvc.perform(get("/product/{id}", 1))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("POST /product -Success")
    void testCreateProduct() throws Exception {
        Product postProduct = Product.builder().name("product name").quantity(10).build();
        Product mockProduct = new Product(1, "product name", 10, 1);
        
        doReturn(mockProduct).when(service).save(any());
        
        mockMvc.perform(post("/product")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(postProduct)))
                
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                
        .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
        .andExpect(header().string(HttpHeaders.LOCATION, "/product/1"))

        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is("product name")))
        .andExpect(jsonPath("$.quantity", is(10)))
        .andExpect(jsonPath("$.version", is(1)));
                
    }
    
    @Test
    @DisplayName("PUT /product/1 -Mismatch")
    void testProductPutSuccess() throws Exception {
        Product putProduct = Product.builder().name("product name").quantity(10).build();
        Product mockproduct = new Product(1, "product name", 10, 2);
        doReturn(Optional.of(mockproduct)).when(service).findById(1);
        doReturn(true).when(service).update(any());
        
        mockMvc.perform(put("/product/{id}",1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.IF_MATCH, 1)
                .content(asJsonString(putProduct)))
                
                .andExpect(status().isConflict());
                
                
    }
    
    @Test
    @DisplayName("PUT /product/1 -Mismatch")
    void testProductPutNotFound() throws Exception {
        Product putProduct = Product.builder().name("product name").quantity(10).build();
        doReturn(Optional.empty()).when(service).findById(1);
        doReturn(true).when(service).update(any());
        
        mockMvc.perform(put("/product/{id}",1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.IF_MATCH, 1)
                .content(asJsonString(putProduct)))
                
                .andExpect(status().isNotFound());
                
                
    }
    
    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
