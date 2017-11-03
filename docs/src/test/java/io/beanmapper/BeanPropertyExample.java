package io.beanmapper;

import static junit.framework.Assert.assertEquals;

import io.beanmapper.annotations.BeanProperty;
import io.beanmapper.config.BeanMapperBuilder;

import org.junit.Test;

public class BeanPropertyExample {

    @Test
    public void mapOtherNames() {
        Source source = new Source(1L, "Henk", 42);
        Target target = new BeanMapperBuilder().build()
                .map(source, Target.class);

        assertEquals("Henk", target.otherName);
        assertEquals(42, target.otherAge, 0);
    }

    public static class Source {
        public Long id;
        public String name;
        @BeanProperty(name = "otherAge")
        public Integer age;

        public Source(Long id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }
    }

    public static class Target {
        @BeanProperty(name = "name")
        public String otherName;
        public Integer otherAge;
    }
}
