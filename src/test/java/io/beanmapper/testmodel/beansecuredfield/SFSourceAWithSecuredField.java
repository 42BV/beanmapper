package io.beanmapper.testmodel.beansecuredfield;

import io.beanmapper.annotations.BeanSecuredProperty;

public class SFSourceAWithSecuredField {

    @BeanSecuredProperty("ADMIN")
    public String name;

}
