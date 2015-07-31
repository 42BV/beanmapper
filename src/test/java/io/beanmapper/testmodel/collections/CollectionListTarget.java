package io.beanmapper.testmodel.collections;

import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.testmodel.person.PersonView;

import java.util.List;

public class CollectionListTarget {

    @BeanCollection(elementType = PersonView.class)
    public List<PersonView> items;
//    public List<PersonView> items = new ArrayList<PersonView>();

}
