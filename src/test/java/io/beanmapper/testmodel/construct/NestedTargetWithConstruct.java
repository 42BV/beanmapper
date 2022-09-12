package io.beanmapper.testmodel.construct;

import io.beanmapper.annotations.BeanConstruct;

@BeanConstruct({ "street", "number" })
public class NestedTargetWithConstruct {

    public String streetWithNumber;

    public NestedTargetWithConstruct(String street, Integer number) {
        this.streetWithNumber = street + number;
    }
}
