package io.beanmapper.dynclass;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import io.beanmapper.config.StrictMappingProperties;
import io.beanmapper.dynclass.model.Person;

public class ClassGeneratorTest extends AbstractConcurrentTest {

    @Test
    public void shouldNotFailConcurrently() throws Exception {
        final ClassGenerator gen = new ClassGenerator();
        final List<Exception> results = Collections.synchronizedList(new ArrayList<Exception>());
        final Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    for (int t = 0; t < 1000; t++) {
                        gen.createClass(
                                Person.class,
                                Node.createTree(Collections.singletonList("name")),
                                StrictMappingProperties.defaultConfig());
                        Thread.yield();
                    }
                } catch (Exception ex) {
                    results.add(ex);
                }
            }
        };

        run(8, r);

        assertTrue("" + results.size(), results.isEmpty()); // Class generation should produce zero exceptions.
    }

}
