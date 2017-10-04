package io.beanmapper.testmodel.strict;

import io.beanmapper.annotations.BeanProperty;

public class SourceCStrict {

    public String name;
    @BeanProperty(name = "stad")
    public String city;

    public SourceAStrict noMatch1;
    public SourceBNonStrict noMatch2;
    public String noMatch3;

}
