/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.globomantics.tdd.integrations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import com.globomantics.tdd.model.Product;
import java.util.Optional;
import javax.sql.DataSource;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 *
 * @author martin
 */
@ExtendWith({SpringExtension.class, DBUnitExtension.class})
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ProductIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private DataSource dataSource;
    
    public ConnectionHolder getConnectionHolder() {
        return () -> dataSource.getConnection();
    }
    
    @Test
    @DisplayName("GET /product/1 -Found")
    @DataSet("products.yml")
    void getProductByIdFoundIT() throws Exception {
        
        mockMvc.perform(get("/product/{id}",1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/product/1"))
                
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Product 1")))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.version", is(1)));            
    }
    
    @Test
    @DisplayName("GET /product/99 - Not Found")
    @DataSet("products.yml")
    void getProductByIdNotFoundIT() throws Exception {
        
        mockMvc.perform(get("/product/{id}",99))
                .andExpect(status().isNotFound());           
    }
    
    @Test
    @DisplayName("POST /product -Success")
    @DataSet("products.yml")
    void testCreateProduct() throws Exception {
        Product postProduct = Product.builder().name("product name").quantity(10).build();
        
        mockMvc.perform(post("/product")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(postProduct)))
                
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                
        .andExpect(header().string(HttpHeaders.ETAG, "\"3\""))
        .andExpect(header().string(HttpHeaders.LOCATION, "/product/3"))

        .andExpect(jsonPath("$.id", is(3)))
        .andExpect(jsonPath("$.name", is("product name")))
        .andExpect(jsonPath("$.quantity", is(10)))
        .andExpect(jsonPath("$.version", is(1)));
                
    }
    
    @Test
    @DisplayName("PUT /product/1 -success")
    void testProductPutSuccess() throws Exception {
        Product putProduct = Product.builder().name("product name changed").quantity(15).build();
        
        mockMvc.perform(put("/product/{id}",1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.IF_MATCH, 1)
                .content(asJsonString(putProduct)))
                
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                
                .andExpect(header().string(HttpHeaders.ETAG, "\"2\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/product/1"))

                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("product name changed")))
                .andExpect(jsonPath("$.quantity", is(15)))
                .andExpect(jsonPath("$.version", is(2)));
                
                
    }
    
    @Test
    @DisplayName("PUT /product/1 -Mismatch")
    void testProductPutMismatch() throws Exception {
        Product putProduct = Product.builder().name("product name").quantity(15).build();
        
        mockMvc.perform(put("/product/{id}",1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.IF_MATCH, 6)
                .content(asJsonString(putProduct)))
                
                .andExpect(status().isConflict());
                
                
    }
    
    @Test
    @DisplayName("PUT /product/99 -Mismatch")
    void testProductPutNotFound() throws Exception {
        Product putProduct = Product.builder().name("product name").quantity(15).build();
        
        mockMvc.perform(put("/product/{id}",99)
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
