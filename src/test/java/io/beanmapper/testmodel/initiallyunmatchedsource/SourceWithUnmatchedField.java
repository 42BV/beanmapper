package io.beanmapper.testmodel.initiallyunmatchedsource;

import io.beanmapper.annotations.BeanProperty;

public class SourceWithUnmatchedField {

    private String name;

    @BeanProperty(name = "nation")
    private String country;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
