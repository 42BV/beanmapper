package io.beanmapper.strategy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.BeanFactoryMethod;
import io.beanmapper.config.Configuration;
import io.beanmapper.core.BeanMatch;

/**
 * A MapToClassStrategy that is aware of factory methods. This strategy extends
 * the standard MapToClassStrategy but overrides getConstructorArguments to also
 * check for factory method annotations when no BeanConstruct annotation is present.
 */
public class FactoryMethodAwareMapToClassStrategy extends MapToClassStrategy {

    public FactoryMethodAwareMapToClassStrategy(BeanMapper beanMapper, Configuration configuration) {
        super(beanMapper, configuration);
    }

    @Override
    public <S> ConstructorArguments getConstructorArguments(S source, BeanMatch beanMatch) {
        // First try the standard approach (BeanConstruct annotation)
        ConstructorArguments standardArguments = super.getConstructorArguments(source, beanMatch);
        if (standardArguments != null) {
            return standardArguments;
        }
        
        // If no BeanConstruct, check for factory method
        Class<?> targetClass = beanMatch.getTargetClass();
        Method factoryMethod = findFactoryMethod(targetClass);
        
        if (factoryMethod != null) {
            BeanFactoryMethod annotation = factoryMethod.getAnnotation(BeanFactoryMethod.class);
            String[] fieldNames = annotation.value();
            
            if (fieldNames.length > 0) {
                return new ConstructorArguments(source, beanMatch, fieldNames);
            }
        }
        
        // No factory method or no-arg factory method, return null for no-arg constructor
        return null;
    }
    
    /**
     * Finds a factory method in the target class.
     * 
     * @param targetClass the target class
     * @return the first valid factory method found, or null
     */
    private Method findFactoryMethod(Class<?> targetClass) {
        for (Method method : targetClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(BeanFactoryMethod.class) && 
                Modifier.isStatic(method.getModifiers()) &&
                targetClass.isAssignableFrom(method.getReturnType())) {
                return method;
            }
        }
        return null;
    }
}