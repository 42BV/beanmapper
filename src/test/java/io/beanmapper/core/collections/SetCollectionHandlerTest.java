package io.beanmapper.core.collections;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class SetCollectionHandlerTest {

    @Test
    public void createTreeSet() {
        SetCollectionHandler collectionHandler = new SetCollectionHandler();
        Set set = collectionHandler.create(NonComparableClass.class);
        assertEquals(HashSet.class, set.getClass());
    }

    @Test
    public void createHashSet() {
        SetCollectionHandler collectionHandler = new SetCollectionHandler();
        Set set = collectionHandler.create(ComparableClass.class);
        assertEquals(TreeSet.class, set.getClass());
    }

}
