/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.constructor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.beanmapper.annotations.BeanFactoryMethod;
import io.beanmapper.strategy.ConstructorArguments;
import io.beanmapper.utils.BeanMapperTraceLogger;

/**
 * A BeanInitializer that is aware of factory methods but integrates with the existing
 * strategy framework. This initializer extends DefaultBeanInitializer but first checks
 * for factory methods when no ConstructorArguments are provided.
 */
public class FactoryMethodAwareBeanInitializer extends DefaultBeanInitializer {
    
    private final FactoryMethodBeanInitializer factoryMethodInitializer;
    
    public FactoryMethodAwareBeanInitializer() {
        this.factoryMethodInitializer = new FactoryMethodBeanInitializer(this);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T instantiate(Class<T> beanClass, ConstructorArguments arguments) {
        // If we have constructor arguments, use the factory method initializer 
        // which can handle both factory methods and fallback to default behavior
        if (arguments != null) {
            return factoryMethodInitializer.instantiate(beanClass, arguments);
        }
        
        // If no constructor arguments, check if there's a factory method that requires no arguments
        Method factoryMethod = findNoArgFactoryMethod(beanClass);
        if (factoryMethod != null) {
            return factoryMethodInitializer.instantiate(beanClass, null);
        }
        
        // Fall back to default behavior (no-arg constructor)
        return super.instantiate(beanClass, null);
    }
    
    /**
     * Finds a factory method that requires no arguments.
     * 
     * @param beanClass the target class
     * @return a no-arg factory method, or null if none found
     */
    private Method findNoArgFactoryMethod(Class<?> beanClass) {
        for (Method method : beanClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(BeanFactoryMethod.class) && 
                Modifier.isStatic(method.getModifiers()) &&
                beanClass.isAssignableFrom(method.getReturnType()) &&
                method.getParameterCount() == 0) {
                
                BeanFactoryMethod annotation = method.getAnnotation(BeanFactoryMethod.class);
                if (annotation.value().length == 0) {
                    BeanMapperTraceLogger.log("Found no-arg factory method {} for class {}", 
                            method.getName(), beanClass.getName());
                    return method;
                }
            }
        }
        return null;
    }
}