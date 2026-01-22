package io.beanmapper.annotations.model.bean_property.record;

import io.beanmapper.annotations.BeanProperty;

public record NestedResultRecord(
    Long id,
    String name,
    @BeanProperty("parent.id") Long parentId,
    @BeanProperty("parent.name") String parentName
) {}
