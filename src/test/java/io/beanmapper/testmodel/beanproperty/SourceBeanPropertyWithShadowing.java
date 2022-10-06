package io.beanmapper.testmodel.beanproperty;

import io.beanmapper.annotations.BeanProperty;

public class SourceBeanPropertyWithShadowing {

    public String field1;

    @BeanProperty("field1")
    public String field2;

    public SourceBeanPropertyWithShadowing(String field1, String field2) {
        this.field1 = field1;
        this.field2 = field2;
    }

}
