package io.beanmapper.testmodel.collections;

import java.util.List;

import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.testmodel.person.PersonView;

public class CollectionListTarget {

    @BeanCollection(elementType = PersonView.class)
    public List<PersonView> items;

}
