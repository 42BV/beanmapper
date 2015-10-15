package io.beanmapper.testmodel.constructunmatching;

import io.beanmapper.annotations.BeanConstruct;

@BeanConstruct({ "nestedClass.city", "nestedClass.country" })
public class UnmatchingTargetWithConstruct {

    public String city;
    public String country;
    private String cityCountry;

    public UnmatchingTargetWithConstruct(String city, String country){
        cityCountry = city + " " + country;
    }

    public String getCityCountry() {
        return cityCountry;
    }
}
