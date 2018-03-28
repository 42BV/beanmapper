package io.beanmapper.testmodel.beansecuredfield;

import io.beanmapper.annotations.LogicSecuredCheck;

public class NeverReturnTrueCheck implements LogicSecuredCheck<SFSourceDLogicSecured, Object> {

    @Override
    public boolean isAllowed(SFSourceDLogicSecured source, Object target) {
        return false;
    }

}
