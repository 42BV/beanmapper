package io.beanmapper;

import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.annotations.BeanProperty;
import io.beanmapper.config.BeanMapperBuilder;
import org.junit.Test;

import java.util.*;

import static junit.framework.Assert.assertEquals;

public class BeanCollectionExample {

    @Test
    public void mapCollections() {
        Source source = new Source();
        source.items = new LinkedHashSet<Person>();
        source.items.add(new Person(1L, "Henk", 42));
        source.items.add(new Person(2L, "Piet", 18));
        source.items.add(new Person(3L, "Gijs", 67));

        Target target = new BeanMapperBuilder().build()
                .map(source, Target.class);

        assertEquals(3, target.items.size(), 0);
        assertEquals("Henk", target.items.get(0).name);
        assertEquals("Piet", target.items.get(1).name);
        assertEquals("Gijs", target.items.get(2).name);
    }

    public static class Source {
        public Set<Person> items;
    }

    public static class Target {
        @BeanCollection(elementType = PersonResult.class, beanCollectionUsage = BeanCollectionUsage.REUSE)
        public List<PersonResult> items;
    }

    public static class Person {
        public Long id;
        public String name;
        public Integer age;

        public Person(Long id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }
    }

    public static class PersonResult {
        public String name;
        public Integer age;
    }
}
