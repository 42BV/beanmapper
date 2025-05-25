package io.beanmapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark static factory methods for bean instantiation.
 * 
 * <p>This annotation allows specifying static factory methods that can be used
 * to instantiate objects during the bean mapping process. The factory method
 * must be static since it will be called before any instance of the target
 * type is available.</p>
 * 
 * <p>The value array specifies the names of the fields that should be provided
 * to the factory method as arguments, in the correct order.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * public class Person {
 *     private String name;
 *     private int age;
 *     
 *     &#64;BeanFactoryMethod({"name", "age"})
 *     public static Person create(String name, int age) {
 *         return new Person(name, age);
 *     }
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanFactoryMethod {
    
    /**
     * The names of the fields that should be provided to the factory method
     * as arguments, in the correct order.
     * 
     * @return array of field names
     */
    String[] value();
}