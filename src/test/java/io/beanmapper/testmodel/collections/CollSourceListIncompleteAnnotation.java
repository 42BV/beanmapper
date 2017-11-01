package io.beanmapper.testmodel.collections;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.annotations.BeanCollection;

public class CollSourceListIncompleteAnnotation {

    @BeanCollection
    public List<Long> list = new ArrayList<>();

}
