package io.beanmapper.strategy.record.model;

import io.beanmapper.annotations.BeanProperty;

public record ResultRecordWithNameAndPlace(@BeanProperty("abc") String name, @BeanProperty("ghi") String place) {
}
