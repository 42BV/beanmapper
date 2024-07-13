package io.beanmapper.core.collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

class SetCollectionHandlerTest {

    @Test
    void createTreeSet() {
        SetCollectionHandler collectionHandler = new SetCollectionHandler();
        Set set = collectionHandler.create(NonComparableClass.class);
        assertEquals(HashSet.class, set.getClass());
    }

    @Test
    void createHashSet() {
        SetCollectionHandler collectionHandler = new SetCollectionHandler();
        Set set = collectionHandler.create(ComparableClass.class);
        assertEquals(TreeSet.class, set.getClass());
    }
}
