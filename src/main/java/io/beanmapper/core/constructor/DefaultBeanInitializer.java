/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.constructor;

import io.beanmapper.exceptions.BeanConstructException;
import io.beanmapper.exceptions.BeanInstantiationException;
import io.beanmapper.utils.ConstructorArguments;

public class DefaultBeanInitializer implements BeanInitializer {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T instantiate(Class<T> beanClass, ConstructorArguments arguments) {
        try {
            if (arguments == null) {
                return beanClass.getConstructor().newInstance();
            } else {
                return (T) beanClass.getConstructor(arguments.getTypes()).newInstance(arguments.getValues());
            }
        } catch (NoSuchMethodException e){
            throw new BeanConstructException(beanClass, e);
        } catch (Exception e) {
            throw new BeanInstantiationException(beanClass, e);
        }
    }
}
