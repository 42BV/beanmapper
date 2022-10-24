package io.beanmapper.dynclass;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.dynclass.model.Person;
import javassist.ClassPool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClassStoreTest extends AbstractConcurrentTest {

    private ClassStore store;

    @BeforeEach
    void before() {
        this.store = new ClassStore(new ClassGenerator(new ClassPool(true)));
    }

    @Test
    void shouldCacheThreadSafe() throws InterruptedException {
        final Set<Class> results = new CopyOnWriteArraySet<>();
        run(8, () -> results.add(store.getOrCreateGeneratedClass(
                Person.class,
                Collections.synchronizedList(Collections.singletonList("name")),
                new BeanMapperBuilder()
                        .build()
                        .getConfiguration()
                        .getStrictMappingProperties())));
        assertEquals(1, results.size()); // A thread safe implementation should return one class.
    }
}
