package io.beanmapper.testmodel.collections;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.testmodel.person.PersonView;

public class CollectionListTargetClear {

    @BeanCollection(elementType = PersonView.class, beanCollectionUsage = BeanCollectionUsage.CLEAR)
    public List<PersonView> items = new ArrayList<PersonView>();

}
