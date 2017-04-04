package io.beanmapper.testmodel.converterforclassesincollection;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.testmodel.converterbetweennestedclasses.NestedTargetClass;

public class TargetWithCollection {

    @BeanCollection(elementType = NestedTargetClass.class)
    public List<NestedTargetClass> objects = new ArrayList<>();
}