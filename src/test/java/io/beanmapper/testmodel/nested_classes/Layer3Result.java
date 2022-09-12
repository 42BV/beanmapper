package io.beanmapper.testmodel.nested_classes;

import io.beanmapper.annotations.BeanProperty;

public class Layer3Result {

    private String name3;
    @BeanProperty(name = "layer4.id")
    private Long id4;

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public Long getId4() {
        return id4;
    }

    public void setId4(Long id4) {
        this.id4 = id4;
    }
}
