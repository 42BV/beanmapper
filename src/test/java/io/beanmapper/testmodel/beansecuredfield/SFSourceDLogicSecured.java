package io.beanmapper.testmodel.beansecuredfield;

import io.beanmapper.annotations.BeanLogicSecured;

public class SFSourceDLogicSecured {

    @BeanLogicSecured(NeverReturnTrueCheck.class)
    public String name;

}
