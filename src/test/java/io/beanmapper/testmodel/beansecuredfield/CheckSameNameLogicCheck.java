package io.beanmapper.testmodel.beansecuredfield;

import io.beanmapper.annotations.LogicSecuredCheck;

public class CheckSameNameLogicCheck implements LogicSecuredCheck<SFSourceELogicSecured, Object> {

    @Override
    public boolean isAllowed(SFSourceELogicSecured source, Object target) {
        return "Henk".equals(source.name);
    }

}
