/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.constructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.BeanFactoryMethod;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.core.BeanMatch;
import io.beanmapper.strategy.ConstructorArguments;
import io.beanmapper.utils.BeanMapperTraceLogger;
import io.beanmapper.utils.DefaultValues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BeanInitializer implementation that uses static factory methods annotated with
 * {@link BeanFactoryMethod} to instantiate objects.
 * 
 * <p>This initializer looks for static methods in the target class that are annotated
 * with {@code @BeanFactoryMethod}. If found, it uses the factory method instead of
 * calling the constructor directly.</p>
 * 
 * <p>The factory method must be static and the annotation must specify the field names
 * that correspond to the method parameters in the correct order.</p>
 */
public class FactoryMethodBeanInitializer implements BeanInitializer {

    private static final Logger log = LoggerFactory.getLogger(FactoryMethodBeanInitializer.class);
    
    private final BeanInitializer fallbackInitializer;

    /**
     * Creates a new FactoryMethodBeanInitializer with a fallback initializer.
     * 
     * @param fallbackInitializer the initializer to use when no factory method is found
     */
    public FactoryMethodBeanInitializer(BeanInitializer fallbackInitializer) {
        this.fallbackInitializer = fallbackInitializer != null ? fallbackInitializer : new DefaultBeanInitializer();
    }
    
    /**
     * Creates a new FactoryMethodBeanInitializer with DefaultBeanInitializer as fallback.
     */
    public FactoryMethodBeanInitializer() {
        this(new DefaultBeanInitializer());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T instantiate(Class<T> beanClass, ConstructorArguments arguments) {
        Method factoryMethod = findFactoryMethod(beanClass, arguments);
        
        if (factoryMethod != null) {
            return instantiateUsingFactoryMethod(beanClass, factoryMethod, arguments);
        } else {
            return fallbackInitializer.instantiate(beanClass, arguments);
        }
    }
    
    /**
     * Instantiates an object using a factory method if available, with a source object and BeanMatch.
     * This method tries to find a suitable factory method and creates ConstructorArguments from
     * the factory method annotation if needed.
     * 
     * @param beanClass the target class
     * @param source the source object
     * @param beanMatch the bean match containing field mappings
     * @return the instantiated object
     */
    public <T, S> T instantiateWithSourceAndBeanMatch(Class<T> beanClass, S source, BeanMatch beanMatch) {
        Method factoryMethod = findFactoryMethodWithAnnotation(beanClass);
        
        if (factoryMethod != null) {
            BeanFactoryMethod annotation = factoryMethod.getAnnotation(BeanFactoryMethod.class);
            ConstructorArguments arguments = null;
            
            if (annotation.value().length > 0) {
                // Create ConstructorArguments from the factory method annotation
                arguments = new ConstructorArguments(source, beanMatch, annotation.value());
            }
            
            return instantiateUsingFactoryMethod(beanClass, factoryMethod, arguments);
        } else {
            return fallbackInitializer.instantiate(beanClass, null);
        }
    }
    
    /**
     * Finds a static factory method annotated with {@link BeanFactoryMethod} that matches
     * the provided constructor arguments.
     * 
     * @param beanClass the target class
     * @param arguments the constructor arguments
     * @return the matching factory method, or null if none found
     */
    private Method findFactoryMethod(Class<?> beanClass, ConstructorArguments arguments) {
        for (Method method : beanClass.getDeclaredMethods()) {
            if (isValidFactoryMethod(method, beanClass, arguments)) {
                return method;
            }
        }
        return null;
    }
    
    /**
     * Finds a static factory method annotated with {@link BeanFactoryMethod}.
     * 
     * @param beanClass the target class
     * @return the first valid factory method found, or null if none found
     */
    private Method findFactoryMethodWithAnnotation(Class<?> beanClass) {
        for (Method method : beanClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(BeanFactoryMethod.class) && 
                Modifier.isStatic(method.getModifiers()) &&
                beanClass.isAssignableFrom(method.getReturnType())) {
                return method;
            }
        }
        return null;
    }
    
    /**
     * Checks if a method is a valid factory method for the given arguments.
     * 
     * @param method the method to check
     * @param beanClass the target class
     * @param arguments the constructor arguments
     * @return true if the method is a valid factory method
     */
    private boolean isValidFactoryMethod(Method method, Class<?> beanClass, ConstructorArguments arguments) {
        // Must be annotated with @BeanFactoryMethod
        BeanFactoryMethod annotation = method.getAnnotation(BeanFactoryMethod.class);
        if (annotation == null) {
            return false;
        }
        
        // Must be static
        if (!Modifier.isStatic(method.getModifiers())) {
            log.warn("Factory method {} in class {} is not static. Factory methods must be static.", 
                    method.getName(), beanClass.getName());
            return false;
        }
        
        // Must return the correct type or a compatible type
        if (!beanClass.isAssignableFrom(method.getReturnType())) {
            log.warn("Factory method {} in class {} does not return compatible type. Expected: {}, Got: {}", 
                    method.getName(), beanClass.getName(), beanClass.getName(), method.getReturnType().getName());
            return false;
        }
        
        // Check if arguments match
        if (arguments == null) {
            return method.getParameterCount() == 0 || annotation.value().length == 0;
        }
        
        Class<?>[] parameterTypes = method.getParameterTypes();
        Class<?>[] argumentTypes = arguments.getTypes();
        
        if (parameterTypes.length != argumentTypes.length) {
            return false;
        }
        
        // Check if parameter types are compatible
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!parameterTypes[i].isAssignableFrom(argumentTypes[i]) && 
                !isAutoboxingCompatible(parameterTypes[i], argumentTypes[i])) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Checks if two types are compatible via autoboxing/unboxing.
     * 
     * @param parameterType the method parameter type
     * @param argumentType the argument type
     * @return true if types are autoboxing compatible
     */
    private boolean isAutoboxingCompatible(Class<?> parameterType, Class<?> argumentType) {
        if (parameterType.isPrimitive() && !argumentType.isPrimitive()) {
            return getWrapperType(parameterType).equals(argumentType);
        } else if (!parameterType.isPrimitive() && argumentType.isPrimitive()) {
            return parameterType.equals(getWrapperType(argumentType));
        }
        return false;
    }
    
    /**
     * Gets the wrapper type for a primitive type.
     * 
     * @param primitiveType the primitive type
     * @return the corresponding wrapper type
     */
    private Class<?> getWrapperType(Class<?> primitiveType) {
        if (primitiveType == boolean.class) return Boolean.class;
        if (primitiveType == byte.class) return Byte.class;
        if (primitiveType == char.class) return Character.class;
        if (primitiveType == short.class) return Short.class;
        if (primitiveType == int.class) return Integer.class;
        if (primitiveType == long.class) return Long.class;
        if (primitiveType == float.class) return Float.class;
        if (primitiveType == double.class) return Double.class;
        return primitiveType;
    }
    
    /**
     * Instantiates an object using the provided factory method.
     * 
     * @param beanClass the target class
     * @param factoryMethod the factory method to use
     * @param arguments the constructor arguments
     * @return the instantiated object
     */
    @SuppressWarnings("unchecked")
    private <T> T instantiateUsingFactoryMethod(Class<T> beanClass, Method factoryMethod, ConstructorArguments arguments) {
        BeanMapperTraceLogger.log("Creating a new instance of type {} using factory method {}.", 
                beanClass, factoryMethod.getName());
        
        try {
            factoryMethod.setAccessible(true);
            
            if (arguments == null || arguments.getValues().length == 0) {
                return (T) factoryMethod.invoke(null);
            }
            
            // Map parameterized arguments if needed
            var methodParameterTypes = Arrays.stream(factoryMethod.getParameters())
                    .map(Parameter::getParameterizedType)
                    .toArray(Type[]::new);
            Object[] mappedArguments = mapParameterizedArguments(methodParameterTypes, arguments.getValues());
            
            return (T) factoryMethod.invoke(null, mappedArguments);
            
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Could not instantiate bean of class {} using factory method {}. Returning the default value. {}", 
                    beanClass.getName(), factoryMethod.getName(), e.getMessage());
            return DefaultValues.defaultValueFor(beanClass);
        }
    }
    
    /**
     * Maps arguments to handle parameterized types if needed.
     * 
     * @param methodParameterTypes the method parameter types
     * @param arguments the arguments to map
     * @return the mapped arguments
     */
    private Object[] mapParameterizedArguments(Type[] methodParameterTypes, Object[] arguments) {
        final BeanMapper beanMapper = new BeanMapperBuilder().build();
        Object[] mappedArguments = new Object[arguments.length];
        
        for (int i = 0; i < arguments.length; i++) {
            Object argument = arguments[i];
            if (argument != null && argument.getClass().getTypeParameters().length > 0) {
                argument = beanMapper.map(argument, (ParameterizedType) methodParameterTypes[i]);
            }
            mappedArguments[i] = argument;
        }
        
        return mappedArguments;
    }
}