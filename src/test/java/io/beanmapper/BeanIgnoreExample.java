package io.beanmapper;

import io.beanmapper.annotations.BeanIgnore;
import io.beanmapper.annotations.BeanProperty;
import io.beanmapper.config.BeanMapperBuilder;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class BeanIgnoreExample {

    @Test
    public void mapOtherNames() {
        Source source = new Source(1L, "Henk", 42);
        Target target = new BeanMapperBuilder().build()
                .map(source, Target.class);

        assertNull(target.name);
        assertNull(target.age);
    }

    public static class Source {
        public Long id;
        public String name;
        @BeanIgnore
        public Integer age;

        public Source(Long id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }
    }

    public static class Target {
        @BeanIgnore
        public String name;
        public Integer age;
    }
}
