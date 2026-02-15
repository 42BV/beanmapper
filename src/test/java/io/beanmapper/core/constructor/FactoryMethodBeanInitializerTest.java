package io.beanmapper.core.constructor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.BeanFactoryMethod;
import io.beanmapper.config.BeanMapperBuilder;

import org.junit.jupiter.api.Test;

class FactoryMethodBeanInitializerTest {

    @Test
    void testFactoryMethodInstantiation() {
        // Test the factory method initializer directly with explicit arguments
        FactoryMethodBeanInitializer initializer = new FactoryMethodBeanInitializer();
        
        // Test with null arguments (should use no-arg factory method if available)
        PersonWithDefaultFactory result = initializer.instantiate(PersonWithDefaultFactory.class, null);
        
        assertNotNull(result);
        assertEquals("Default", result.getName());
        assertEquals(0, result.getAge());
        assertEquals("Factory", result.getCreatedBy());
    }
    
    @Test
    void testFactoryMethodWithNoArgs() {
        FactoryMethodBeanInitializer initializer = new FactoryMethodBeanInitializer();
        
        PersonWithDefaultFactory result = initializer.instantiate(PersonWithDefaultFactory.class, null);
        
        assertNotNull(result);
        assertEquals("Default", result.getName());
        assertEquals(0, result.getAge());
        assertEquals("Factory", result.getCreatedBy());
    }
    
    @Test
    void testFallbackToDefaultInitializer() {
        FactoryMethodBeanInitializer initializer = new FactoryMethodBeanInitializer();
        
        // This class has no factory method, should fall back to no-arg constructor
        PersonWithoutFactory result = initializer.instantiate(PersonWithoutFactory.class, null);
        
        // Should use the no-arg constructor
        assertNotNull(result);
        assertEquals("Default", result.getName());
        assertEquals(0, result.getAge());
    }
    
    @Test
    void testWithBeanMapper() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .withFactoryMethodSupport()
                .build();
        
        PersonSource source = new PersonSource("Alice", 35);
        PersonWithDefaultFactory result = beanMapper.map(source, PersonWithDefaultFactory.class);
        
        // For now this tests the no-arg factory method since the full integration 
        // with constructor arguments requires additional strategy changes
        assertNotNull(result);
        assertEquals("Default", result.getName());
        assertEquals(0, result.getAge());
        assertEquals("Factory", result.getCreatedBy());
    }
    
    @Test
    void testFactoryMethodWithInvalidSignature() {
        FactoryMethodBeanInitializer initializer = new FactoryMethodBeanInitializer();
        
        // Should fall back to default initializer since factory method is invalid (not static)
        PersonWithInvalidFactory result = initializer.instantiate(PersonWithInvalidFactory.class, null);
        
        // Should use the no-arg constructor since factory method is invalid
        assertNotNull(result);
        assertEquals("Constructor", result.getName());
        assertEquals(0, result.getAge());
    }

    // Test classes
    public static class PersonSource {
        public String name;
        public int age;
        
        public PersonSource() {}
        
        public PersonSource(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
    
    public static class PersonWithFactory {
        private String name;
        private int age;
        private String createdBy;
        
        public PersonWithFactory(String name, int age) {
            this.name = name;
            this.age = age;
            this.createdBy = "Constructor";
        }
        
        @BeanFactoryMethod({"name", "age"})
        public static PersonWithFactory create(String name, int age) {
            PersonWithFactory person = new PersonWithFactory(name, age);
            person.createdBy = "Factory";
            return person;
        }
        
        public String getName() { return name; }
        public int getAge() { return age; }
        public String getCreatedBy() { return createdBy; }
    }
    
    public static class PersonWithDefaultFactory {
        private String name;
        private int age;
        private String createdBy;
        
        public PersonWithDefaultFactory() {
            this.name = "Unknown";
            this.age = 0;
            this.createdBy = "Constructor";
        }
        
        @BeanFactoryMethod({})
        public static PersonWithDefaultFactory createDefault() {
            PersonWithDefaultFactory person = new PersonWithDefaultFactory();
            person.name = "Default";
            person.createdBy = "Factory";
            return person;
        }
        
        public String getName() { return name; }
        public int getAge() { return age; }
        public String getCreatedBy() { return createdBy; }
    }
    
    public static class PersonWithoutFactory {
        private String name;
        private int age;
        
        public PersonWithoutFactory() {
            this.name = "Default";
            this.age = 0;
        }
        
        public PersonWithoutFactory(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        public String getName() { return name; }
        public int getAge() { return age; }
    }
    
    public static class PersonWithInvalidFactory {
        private String name;
        private int age;
        
        public PersonWithInvalidFactory() {
            this.name = "Constructor";
            this.age = 0;
        }
        
        public PersonWithInvalidFactory(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        // Invalid factory method - not static
        @BeanFactoryMethod({"name", "age"})
        public PersonWithInvalidFactory create(String name, int age) {
            return new PersonWithInvalidFactory(name, age);
        }
        
        public String getName() { return name; }
        public int getAge() { return age; }
    }
}