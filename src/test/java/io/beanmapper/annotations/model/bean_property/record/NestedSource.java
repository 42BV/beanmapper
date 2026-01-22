package io.beanmapper.annotations.model.bean_property.record;

public class NestedSource {
    public Long id;
    public String name;
    public NestedSource parent;

    public NestedSource(Long id, String name, NestedSource parent) {
        this.id = id;
        this.name = name;
        this.parent = parent;
    }
}
