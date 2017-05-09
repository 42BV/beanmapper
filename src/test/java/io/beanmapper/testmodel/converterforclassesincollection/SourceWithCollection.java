package io.beanmapper.testmodel.converterforclassesincollection;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.testmodel.converterbetweennestedclasses.NestedSourceClass;

public class SourceWithCollection {

    public List<NestedSourceClass> objects = new ArrayList<>();
}