package io.beanmapper.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.BeanMapper;

import org.junit.Before;
import org.junit.Test;

public class CollectionHandlerStoreTest {

    private Configuration configuration;

    @Before
    public void setup() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .build();
        configuration = beanMapper.getConfiguration();
    }

    @Test
    public void getCollectionHandlerFor_null() {
        assertNull(configuration.getCollectionHandlerFor(null));
    }

    @Test
    public void getCollectionHandlerFor_nonCollection() {
        assertNull(configuration.getCollectionHandlerFor(String.class));
    }

    @Test
    public void getCollectionHandlerFor_List() {
        assertEquals(List.class, configuration.getCollectionHandlerFor(ArrayList.class).getType());
    }

    @Test
    public void getCollectionHandlerFor_AnonymousClass() {
        assertEquals(List.class, configuration.getCollectionHandlerFor(new ArrayList(){{}}.getClass()).getType());
    }


}
