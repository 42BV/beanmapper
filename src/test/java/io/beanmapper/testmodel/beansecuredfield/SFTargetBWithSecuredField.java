package io.beanmapper.testmodel.beansecuredfield;

import io.beanmapper.annotations.BeanSecuredProperty;

public class SFTargetBWithSecuredField {

    @BeanSecuredProperty({"ADMIN", "DEV"})
    public String name;

}
