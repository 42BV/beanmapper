package io.beanmapper.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.BeanMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CollectionHandlerStoreTest {

    private Configuration configuration;

    @BeforeEach
    public void setup() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .build();
        configuration = beanMapper.getConfiguration();
    }

    @Test
    void getCollectionHandlerFor_null() {
        assertNull(configuration.getCollectionHandlerFor(null));
    }

    @Test
    void getCollectionHandlerFor_nonCollection() {
        assertNull(configuration.getCollectionHandlerFor(String.class));
    }

    @Test
    void getCollectionHandlerFor_List() {
        assertEquals(List.class, configuration.getCollectionHandlerFor(ArrayList.class).getType());
    }

    @Test
    void getCollectionHandlerFor_AnonymousClass() {
        assertEquals(List.class, configuration.getCollectionHandlerFor(new ArrayList() {{}}.getClass()).getType());
    }

}
