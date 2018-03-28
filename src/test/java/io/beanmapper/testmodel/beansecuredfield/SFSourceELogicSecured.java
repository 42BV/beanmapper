package io.beanmapper.testmodel.beansecuredfield;

import io.beanmapper.annotations.BeanLogicSecured;

public class SFSourceELogicSecured {

    @BeanLogicSecured(CheckSameNameLogicCheck.class)
    public String name;

}
