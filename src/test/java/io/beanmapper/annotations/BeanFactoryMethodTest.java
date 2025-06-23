package io.beanmapper.annotations;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

class BeanFactoryMethodTest {

    @Test
    void testBeanFactoryMethodAnnotation() {
        Method method = null;
        
        try {
            method = TestClassWithFactory.class.getMethod("createInstance", String.class, int.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Test method not found", e);
        }
        
        assertNotNull(method);
        assertTrue(method.isAnnotationPresent(BeanFactoryMethod.class));
        
        BeanFactoryMethod annotation = method.getAnnotation(BeanFactoryMethod.class);
        assertNotNull(annotation);
        assertArrayEquals(new String[]{"name", "age"}, annotation.value());
    }
    
    @Test
    void testBeanFactoryMethodWithEmptyFields() {
        Method method = null;
        
        try {
            method = TestClassWithFactory.class.getMethod("createDefault");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Test method not found", e);
        }
        
        assertNotNull(method);
        assertTrue(method.isAnnotationPresent(BeanFactoryMethod.class));
        
        BeanFactoryMethod annotation = method.getAnnotation(BeanFactoryMethod.class);
        assertNotNull(annotation);
        assertArrayEquals(new String[]{}, annotation.value());
    }
    
    public static class TestClassWithFactory {
        private String name;
        private int age;
        
        @BeanFactoryMethod({"name", "age"})
        public static TestClassWithFactory createInstance(String name, int age) {
            TestClassWithFactory instance = new TestClassWithFactory();
            instance.name = name;
            instance.age = age;
            return instance;
        }
        
        @BeanFactoryMethod({})
        public static TestClassWithFactory createDefault() {
            TestClassWithFactory instance = new TestClassWithFactory();
            instance.name = "Default";
            instance.age = 0;
            return instance;
        }
        
        public String getName() { return name; }
        public int getAge() { return age; }
    }
}