package io.beanmapper.testmodel.beansecuredfield;

import io.beanmapper.annotations.BeanRoleSecured;

public class SFSourceAWithSecuredField {

    @BeanRoleSecured("ADMIN")
    public String name;

}
