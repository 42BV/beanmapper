package io.beanmapper.core.converter.collections;

import java.util.Set;
import java.util.TreeSet;

public class CollectionSetConverter extends AbstractCollectionConverter<Set> {

    protected Set createCollection() {
        return new TreeSet();
    }
}
