package io.beanmapper.testmodel.constructNotMatching;

import io.beanmapper.annotations.BeanConstruct;

@BeanConstruct({ "city", "country" })
public class NestedConstructTarget {

    public String city;
    public String country;
    private String cityCountry;

    public NestedConstructTarget(String city, String country){
        cityCountry = city + " " + country;
    }

    public String getCityCountry() {
        return cityCountry;
    }
}
