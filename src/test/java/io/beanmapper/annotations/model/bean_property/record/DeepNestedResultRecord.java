package io.beanmapper.annotations.model.bean_property.record;

import io.beanmapper.annotations.BeanProperty;

public record DeepNestedResultRecord(
    Long id,
    String name,
    @BeanProperty("parent.parent.id") Long grandParentId
) {}
