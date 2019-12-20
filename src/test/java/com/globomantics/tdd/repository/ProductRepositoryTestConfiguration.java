/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.globomantics.tdd.repository;

import javax.activation.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 *
 * @author martin
 */
@Configuration
@Profile("test")
public class ProductRepositoryTestConfiguration {
    
    @Primary
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource datasource = new DriverManagerDataSource();
        datasource.setDriverClassName("org.h2.Driver");
        datasource.setUrl("jdbc:h2:memdb;DB_CLOSE_DELAY=-1");
        datasource.setUsername("sa");
        datasource.setPassword("");
        return (DataSource) datasource;
    }
    
}
