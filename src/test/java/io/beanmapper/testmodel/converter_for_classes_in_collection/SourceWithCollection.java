package io.beanmapper.testmodel.converter_for_classes_in_collection;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.testmodel.converter_between_nested_classes.NestedSourceClass;

public class SourceWithCollection {

    public List<NestedSourceClass> objects = new ArrayList<>();
}