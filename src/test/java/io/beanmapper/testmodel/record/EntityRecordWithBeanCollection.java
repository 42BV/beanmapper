package io.beanmapper.testmodel.record;

import java.util.List;

import io.beanmapper.annotations.BeanCollection;

public record EntityRecordWithBeanCollection(int id, String name, @BeanCollection(elementType = Tag.class) List<String> tags) {
}
