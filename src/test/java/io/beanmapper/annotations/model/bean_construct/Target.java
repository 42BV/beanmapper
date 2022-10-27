package io.beanmapper.annotations.model.bean_construct;

import java.util.List;

import io.beanmapper.annotations.BeanConstruct;

@BeanConstruct({ "field1", "field2", "nested" })
public class Target {
    private final Integer field1;
    private final Integer field2;
    private final List<NestedTarget> nested;

    public Target(Integer field1, Integer field2, List<NestedTarget> nested) {
        this.field1 = field1;
        this.field2 = field2;
        this.nested = nested;
    }

    public Integer getField1() {
        return field1;
    }

    public Integer getField2() {
        return field2;
    }

    public List<NestedTarget> getNested() {
        return nested;
    }

}
