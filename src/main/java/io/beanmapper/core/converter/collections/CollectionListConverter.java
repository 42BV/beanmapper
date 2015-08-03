package io.beanmapper.core.converter.collections;

import java.util.ArrayList;
import java.util.List;

public class CollectionListConverter extends AbstractCollectionConverter<List> {

    @Override
    protected List createCollection() {
        return new ArrayList();
    }
}
