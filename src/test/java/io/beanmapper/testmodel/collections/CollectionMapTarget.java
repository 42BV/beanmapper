package io.beanmapper.testmodel.collections;

import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.testmodel.person.PersonView;

import java.util.Map;

public class CollectionMapTarget {

    @BeanCollection(elementType = PersonView.class)
    public Map<String, PersonView> items;

}
