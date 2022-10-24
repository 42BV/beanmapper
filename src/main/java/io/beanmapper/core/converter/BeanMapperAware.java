/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter;

import io.beanmapper.BeanMapper;

/**
 * By implementing this interface we will attempt to inject
 * the bean mapper on demand.
 *
 * @author Jeroen van Schagen
 * @since Nov 13, 2015
 * @deprecated As of release v0.4.0, internally solved by passing BeanMapper to BeanConverter.convert
 */
public interface BeanMapperAware {

    /**
     * Store the bean mapper.
     *
     * @param beanMapper the bean mapper
     */
    void setBeanMapper(BeanMapper beanMapper);

}
