package io.beanmapper.testmodel.beansecuredfield;

import io.beanmapper.annotations.BeanRoleSecured;

public class SFTargetBWithSecuredField {

    @BeanRoleSecured({"ADMIN", "DEV"})
    public String name;

}
