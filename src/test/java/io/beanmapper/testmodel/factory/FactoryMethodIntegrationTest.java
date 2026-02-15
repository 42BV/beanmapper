package io.beanmapper.testmodel.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.BeanFactoryMethod;
import io.beanmapper.config.BeanMapperBuilder;

import org.junit.jupiter.api.Test;

class FactoryMethodIntegrationTest {

    @Test
    void testComplexObjectWithFactoryMethod() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .withFactoryMethodSupport()
                .build();
        
        ProductForm form = new ProductForm();
        form.name = "Laptop";
        form.price = 1299.99;
        form.categoryId = 1L;
        
        Product result = beanMapper.map(form, Product.class);
        
        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(1299.99, result.getPrice());
        assertEquals(1L, result.getCategoryId());
        assertEquals("FACTORY_CREATED", result.getStatus());
        assertNotNull(result.getCreatedAt());
    }
    
    @Test
    void testImmutableObjectWithFactoryMethod() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .withFactoryMethodSupport()
                .build();
        
        PersonForm form = new PersonForm();
        form.firstName = "John";
        form.lastName = "Doe";
        form.email = "john.doe@example.com";
        
        ImmutablePerson result = beanMapper.map(form, ImmutablePerson.class);
        
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("John Doe", result.getFullName());
    }
    
    @Test
    void testSingletonPatternWithFactoryMethod() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .withFactoryMethodSupport()
                .build();
        
        ConfigForm form = new ConfigForm();
        form.environment = "production";
        form.debugEnabled = false;
        
        AppConfig result = beanMapper.map(form, AppConfig.class);
        
        assertNotNull(result);
        assertEquals("production", result.getEnvironment());
        assertEquals(false, result.isDebugEnabled());
        assertEquals("FACTORY_CONFIGURED", result.getConfigSource());
    }

    // Test classes
    public static class ProductForm {
        public String name;
        public double price;
        public Long categoryId;
    }
    
    public static class Product {
        private String name;
        private double price;
        private Long categoryId;
        private String status;
        private java.time.LocalDateTime createdAt;
        
        // Private constructor to force use of factory method
        private Product(String name, double price, Long categoryId) {
            this.name = name;
            this.price = price;
            this.categoryId = categoryId;
            this.status = "FACTORY_CREATED";
            this.createdAt = java.time.LocalDateTime.now();
        }
        
        @BeanFactoryMethod({"name", "price", "categoryId"})
        public static Product create(String name, double price, Long categoryId) {
            // Factory method can perform validation, initialization, etc.
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be empty");
            }
            if (price < 0) {
                throw new IllegalArgumentException("Product price cannot be negative");
            }
            return new Product(name, price, categoryId);
        }
        
        public String getName() { return name; }
        public double getPrice() { return price; }
        public Long getCategoryId() { return categoryId; }
        public String getStatus() { return status; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    }
    
    public static class PersonForm {
        public String firstName;
        public String lastName;
        public String email;
    }
    
    public static class ImmutablePerson {
        private final String firstName;
        private final String lastName;
        private final String email;
        private final String fullName;
        
        // Private constructor
        private ImmutablePerson(String firstName, String lastName, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.fullName = firstName + " " + lastName;
        }
        
        @BeanFactoryMethod({"firstName", "lastName", "email"})
        public static ImmutablePerson of(String firstName, String lastName, String email) {
            // Validation and normalization can be done here
            if (firstName == null) firstName = "";
            if (lastName == null) lastName = "";
            if (email == null) email = "";
            
            return new ImmutablePerson(
                firstName.trim(), 
                lastName.trim(), 
                email.toLowerCase().trim()
            );
        }
        
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getEmail() { return email; }
        public String getFullName() { return fullName; }
    }
    
    public static class ConfigForm {
        public String environment;
        public boolean debugEnabled;
    }
    
    public static class AppConfig {
        private String environment;
        private boolean debugEnabled;
        private String configSource;
        
        // Private constructor
        private AppConfig() {}
        
        @BeanFactoryMethod({"environment", "debugEnabled"})
        public static AppConfig createConfig(String environment, boolean debugEnabled) {
            AppConfig config = new AppConfig();
            config.environment = environment != null ? environment : "development";
            config.debugEnabled = debugEnabled;
            config.configSource = "FACTORY_CONFIGURED";
            
            // Could load additional configuration from files, environment variables, etc.
            return config;
        }
        
        public String getEnvironment() { return environment; }
        public boolean isDebugEnabled() { return debugEnabled; }
        public String getConfigSource() { return configSource; }
    }
}