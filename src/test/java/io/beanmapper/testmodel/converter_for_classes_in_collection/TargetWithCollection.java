package io.beanmapper.testmodel.converter_for_classes_in_collection;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.testmodel.converter_between_nested_classes.NestedTargetClass;

public class TargetWithCollection {

    @BeanCollection(elementType = NestedTargetClass.class)
    public List<NestedTargetClass> objects = new ArrayList<>();
}