package io.beanmapper.testmodel.encapsulate;

import io.beanmapper.annotations.BeanUnwrap;

public class ResultOneToMany {

    @BeanUnwrap
    public ResultCountry resultCountry;

    public ResultCountry getResultCountry() {
        return resultCountry;
    }

    public void setResultCountry(ResultCountry resultCountry) {
        this.resultCountry = resultCountry;
    }
}
