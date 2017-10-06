package io.beanmapper.dynclass;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.junit.Before;
import org.junit.Test;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.config.StrictMappingProperties;
import io.beanmapper.core.unproxy.DefaultBeanUnproxy;
import io.beanmapper.core.unproxy.SkippingBeanUnproxy;
import io.beanmapper.dynclass.model.Person;

public class ClassStoreTest extends AbstractConcurrentTest {

    private ClassStore store;

    @Before
    public void init() {
        store = new ClassStore();
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
