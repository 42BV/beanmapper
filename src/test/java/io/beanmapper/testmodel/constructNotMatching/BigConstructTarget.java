package io.beanmapper.testmodel.constructNotMatching;

import io.beanmapper.annotations.BeanUnwrap;

public class BigConstructTarget {

    public Long id;
    public String street;
    @BeanUnwrap
    public NestedConstructTarget nestedClass;

}
