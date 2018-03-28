package io.beanmapper.testmodel.beansecuredfield;

import io.beanmapper.annotations.BeanRoleSecured;

public class SFSourceCWithSecuredMethod {

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    @BeanRoleSecured("ADMIN")
    public String getName() {
        return this.name;
    }

}