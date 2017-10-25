package io.beanmapper.testmodel.collections;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.annotations.BeanCollection;

public class CollSourceClearFlush {

    @BeanCollection(elementType = CollTarget.class, flushAfterClear = true)
    public List<String> items = new ArrayList<>();

}
