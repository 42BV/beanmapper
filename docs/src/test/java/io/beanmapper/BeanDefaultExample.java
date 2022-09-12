package io.beanmapper;

import static junit.framework.Assert.assertEquals;

import io.beanmapper.annotations.BeanDefault;
import io.beanmapper.config.BeanMapperBuilder;

import org.junit.jupiter.api.Test;

public class BeanDefaultExample {

    @Test
    void mapWithDefaults() {
        Source source = new Source(null, null, null);
        Target target = new BeanMapperBuilder()
                .build()
                .map(source, Target.class);

        assertEquals(5, target.age, 0);
        assertEquals("Henk", target.name);
    }

    public static class Source {
        public Long id;
        public String name;
        @BeanDefault("5")
        public Integer age;

        public Source(Long id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }
    }

    public static class Target {
        @BeanDefault("Henk")
        public String name;
        public Integer age;
    }
}
