package io.beanmapper.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class BeanPairTest {

    @Test
    public void matches_twoMatchingClass() {
        BeanPair beanPair = new BeanPair(ArrayList.class, TreeSet.class);
        assertTrue(beanPair.matches(List.class, Set.class));
    }

    @Test
    public void matches_matchingSourceClass() {
        BeanPair beanPair = new BeanPair(ArrayList.class, TreeSet.class);
        assertFalse(beanPair.matches(List.class, String.class));
    }

    @Test
    public void matches_matchingTargetClass() {
        BeanPair beanPair = new BeanPair(ArrayList.class, TreeSet.class);
        assertFalse(beanPair.matches(String.class, Set.class));
    }

}
