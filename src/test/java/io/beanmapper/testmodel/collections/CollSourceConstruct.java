package io.beanmapper.testmodel.collections;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.annotations.BeanCollectionUsage;

public class CollSourceConstruct {

    @BeanCollection(elementType = CollTarget.class, beanCollectionUsage = BeanCollectionUsage.CONSTRUCT)
    public List<String> items = new ArrayList<>();

}
