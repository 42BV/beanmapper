package io.beanmapper.config;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class BeanPairTest {

    @Test
    public void matches() {
        BeanPair beanPair = new BeanPair(ArrayList.class, TreeSet.class);
        assertTrue(beanPair.matches(List.class, Set.class));
    }

}
