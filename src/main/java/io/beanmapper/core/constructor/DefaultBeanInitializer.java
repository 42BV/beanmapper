/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.constructor;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.exceptions.BeanConstructException;
import io.beanmapper.exceptions.BeanInstantiationException;
import io.beanmapper.strategy.ConstructorArguments;

public class DefaultBeanInitializer implements BeanInitializer {

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
        } catch (NoSuchMethodException e) {
            throw new BeanConstructException(beanClass, e);
        } catch (Exception e) {
            throw new BeanInstantiationException(beanClass, e);
        }
    }

    private Object[] mapParameterizedArguments(Type[] constructorParameterTypes, Object[] arguments) {
        BeanMapper beanMapper = new BeanMapperBuilder().build();
        Object[] mappedArguments = new Object[arguments.length];
        for (var i = 0; i < arguments.length; ++i) {
            var argument = arguments[i];
            if (argument instanceof Collection<?> collection) {
                argument = beanMapper.map(collection, (Class<?>) ((ParameterizedType) constructorParameterTypes[i]).getActualTypeArguments()[0]);
            } else if (argument instanceof Map<?, ?> map) {
                argument = beanMapper.map(map, (Class<?>) ((ParameterizedType) constructorParameterTypes[i]).getActualTypeArguments()[1]);
            } else if (argument instanceof Optional<?> optional) {
                argument = beanMapper.map(optional, (Class<?>) ((ParameterizedType) constructorParameterTypes[i]).getActualTypeArguments()[0]);
            }
            mappedArguments[i] = argument;
        }
        return mappedArguments;
    }

}
