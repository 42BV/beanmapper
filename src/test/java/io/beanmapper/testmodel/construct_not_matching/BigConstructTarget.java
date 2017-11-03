package io.beanmapper.testmodel.construct_not_matching;

import io.beanmapper.annotations.BeanUnwrap;

public class BigConstructTarget {

    public Long id;
    public String street;
    @BeanUnwrap
    public NestedConstructTarget nestedClass;

}
