package io.beanmapper.dynclass;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.dynclass.model.Person;
import javassist.ClassPool;

import org.junit.jupiter.api.Test;

class ClassGeneratorTest extends AbstractConcurrentTest {

    @Test
    void shouldNotFailConcurrently() throws Exception {
        final ClassGenerator gen = new ClassGenerator(new ClassPool());
        final List<Exception> results = Collections.synchronizedList(new ArrayList<>());
        final Runnable r = () -> {
            try {
                for (int t = 0; t < 1000; t++) {
                    gen.createClass(
                            Person.class,
                            Node.createTree(Collections.singletonList("name")),
                            new BeanMapperBuilder()
                                    .build()
                                    .getConfiguration()
                                    .getStrictMappingProperties());
                    Thread.yield();
                }
            } catch (Exception ex) {
                results.add(ex);
            }
        };

        run(8, r);

        assertTrue(results.isEmpty(), "%d".formatted(results.size())); // Class generation should produce zero exceptions.
    }
}
