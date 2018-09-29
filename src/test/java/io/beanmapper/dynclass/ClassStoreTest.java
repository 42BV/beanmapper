package io.beanmapper.dynclass;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.core.BeanMatchStore;
import io.beanmapper.dynclass.model.Person;

import org.junit.Before;
import org.junit.Test;

public class ClassStoreTest extends AbstractConcurrentTest {

    private ClassStore store;

    @Before
    public void init() {
        store = new ClassStore(BeanMatchStore::new);
    }

    @Test
    public void shouldCacheThreadSafe() throws InterruptedException {
        final Set<Class> results = new CopyOnWriteArraySet<Class>();
        run(8, new Runnable() {
            @Override
            public void run() {
                results.add(store.getOrCreateGeneratedClass(
                        Person.class,
                        Collections.singletonList("name"),
                        new BeanMapperBuilder()
                                .build()
                                .getConfiguration()
                                .getStrictMappingProperties()));
            }
        });
        assertEquals(1, results.size()); // A thread safe implementation should return one class.
    }

}
