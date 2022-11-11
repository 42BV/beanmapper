/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.constructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.utils.DefaultValues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBeanInitializer implements BeanInitializer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T instantiate(Class<T> beanClass, ConstructorArguments arguments) {
        try {
            if (arguments == null) {
                return beanClass.getConstructor().newInstance();
            }
            var constructor = beanClass.getConstructor(arguments.getTypes());
            var constructorParameterTypes = Arrays.stream(constructor.getParameters()).map(Parameter::getParameterizedType).toArray(Type[]::new);
            return beanClass.getConstructor(arguments.getTypes()).newInstance(mapParameterizedArguments(constructorParameterTypes, arguments.getValues()));
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Could not instantiate bean of class %s. Returning the default value associated with the given type. %s".formatted(beanClass.getName(),
                    e.getMessage()));
            return DefaultValues.defaultValueFor(beanClass);
        }
    }

    private Object[] mapParameterizedArguments(Type[] constructorParameterTypes, Object[] arguments) {
        final BeanMapper beanMapper = new BeanMapperBuilder().build();
        Object[] mappedArguments = new Object[arguments.length];
        for (var i = 0; i < arguments.length; ++i) {
            var argument = arguments[i];
            if (argument.getClass().getTypeParameters().length > 0) {
                argument = beanMapper.map(argument, (ParameterizedType) constructorParameterTypes[i]);
            }
            mappedArguments[i] = argument;
        }
        return mappedArguments;
    }

}
