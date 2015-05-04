package io.beanmapper.testmodel.encapsulate;

import io.beanmapper.annotations.BeanProperty;

public class ResultCountry {

    @BeanProperty(name = "name")
    private String countryName;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
