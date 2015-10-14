/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.constructor;

import io.beanmapper.exceptions.BeanInstantiationException;

public class DefaultBeanInitializer implements BeanInitializer {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T instantiate(Class<T> beanClass, Object... constructValues) {
        try {
            if(constructValues == null){
                return beanClass.getConstructor().newInstance();
            }else {
                Class[] constructTypes = new Class[constructValues.length];
                for(int i=0; i < constructTypes.length; i++){
                    constructTypes[i] = constructValues[i].getClass();
                }
                return (T)beanClass.getConstructor(constructTypes).newInstance(constructValues);
            }
        } catch (Exception e) {
            throw new BeanInstantiationException(beanClass, e);
        }
    }
}
