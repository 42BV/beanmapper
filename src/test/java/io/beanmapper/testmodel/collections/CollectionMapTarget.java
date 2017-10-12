package io.beanmapper.testmodel.collections;

import java.util.Map;

import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.testmodel.person.PersonView;

public class CollectionMapTarget {

    @BeanCollection(elementType = PersonView.class)
    public Map<String, PersonView> items;

}
