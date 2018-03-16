package io.beanmapper.testmodel.beansecuredfield;

import io.beanmapper.annotations.BeanSecuredProperty;

public class SFSourceCWithSecuredMethod {

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    @BeanSecuredProperty("ADMIN")
    public String getName() {
        return this.name;
    }

}