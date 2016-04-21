package io.beanmapper.testmodel.projection;

import io.beanmapper.annotations.BeanExpression;

/**
 * 
 *
 * @author jeroen
 * @since Apr 21, 2016
 */
public interface PersonProjection {
    
    String getName();
    
    @BeanExpression("#{name} #{place}")
    String getNameAndPlace();

}
