package io.beanmapper.testmodel.collections;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.annotations.BeanCollectionUsage;

public class CollSourceReuse {

    @BeanCollection(elementType = CollTarget.class, beanCollectionUsage = BeanCollectionUsage.REUSE)
    public List<String> items = new ArrayList<>();

}
