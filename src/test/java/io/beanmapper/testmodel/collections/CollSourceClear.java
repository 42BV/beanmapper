package io.beanmapper.testmodel.collections;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.annotations.BeanCollection;

public class CollSourceClear {

    @BeanCollection(elementType = CollTarget.class)
    public List<String> items = new ArrayList<>();

}
