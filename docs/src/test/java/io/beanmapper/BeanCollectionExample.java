package io.beanmapper;

import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.config.BeanMapperBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

public class BeanCollectionExample {

    @Test
    public void mapCollectionWithConstruct() {
        Source source = new Source();
        source.items = new LinkedHashSet<Person>();
        source.items.add(new Person(1L, "Henk", 42));
        source.items.add(new Person(2L, "Piet", 18));
        source.items.add(new Person(3L, "Gijs", 67));

        TargetWithConstruct target = new TargetWithConstruct();
        target.items = new ArrayList<PersonResult>();
        target.items.add(new PersonResult("Kees", 13));
        target.items.add(new PersonResult("Klaas", 24));

        TargetWithConstruct result = new BeanMapperBuilder().build()
                .map(source, target);

        assertEquals(3, result.items.size(), 0);
        assertEquals("Henk", result.items.get(0).name);
        assertEquals("Piet", result.items.get(1).name);
        assertEquals("Gijs", result.items.get(2).name);
    }

    @Test
    public void mapCollectionWithReuse() {
        Source source = new Source();
        source.items = new LinkedHashSet<Person>();
        source.items.add(new Person(1L, "Henk", 42));
        source.items.add(new Person(2L, "Piet", 18));
        source.items.add(new Person(3L, "Gijs", 67));

        TargetWithReuse target = new TargetWithReuse();
        target.items = new ArrayList<PersonResult>();
        target.items.add(new PersonResult("Kees", 13));
        target.items.add(new PersonResult("Klaas", 24));

        TargetWithReuse result = new BeanMapperBuilder().build()
                .map(source, target);

        assertEquals(5, result.items.size(), 0);
        assertEquals("Kees", result.items.get(0).name);
        assertEquals("Klaas", result.items.get(1).name);
        assertEquals("Henk", result.items.get(2).name);
        assertEquals("Piet", result.items.get(3).name);
        assertEquals("Gijs", result.items.get(4).name);
    }

    @Test
    public void mapCollectionWithClear() {
        Source source = new Source();
        source.items = new LinkedHashSet<Person>();
        source.items.add(new Person(1L, "Henk", 42));
        source.items.add(new Person(2L, "Piet", 18));
        source.items.add(new Person(3L, "Gijs", 67));

        TargetWithClear target = new TargetWithClear();
        target.items = new ArrayList<PersonResult>();
        target.items.add(new PersonResult("Kees", 13));
        target.items.add(new PersonResult("Klaas", 24));

        TargetWithClear result = new BeanMapperBuilder().build()
                .map(source, target);

        assertEquals(3, result.items.size(), 0);
        assertEquals("Henk", result.items.get(0).name);
        assertEquals("Piet", result.items.get(1).name);
        assertEquals("Gijs", result.items.get(2).name);
    }

    public static class Source {
        public Set<Person> items;
    }

    public static class TargetWithConstruct {
        @BeanCollection(elementType = PersonResult.class, beanCollectionUsage = BeanCollectionUsage.CONSTRUCT)
        public List<PersonResult> items;
    }

    public static class TargetWithReuse {
        @BeanCollection(elementType = PersonResult.class, beanCollectionUsage = BeanCollectionUsage.REUSE)
        public List<PersonResult> items;
    }

    public static class TargetWithClear {
        @BeanCollection(elementType = PersonResult.class, beanCollectionUsage = BeanCollectionUsage.CLEAR)
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

        public PersonResult() {
        }

        public PersonResult(String name, Integer age) {
            this.name = name;
            this.age = age;
        }
    }
}
