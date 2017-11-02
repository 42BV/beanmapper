package io.beanmapper;

import io.beanmapper.annotations.BeanAlias;
import io.beanmapper.annotations.BeanConstruct;
import io.beanmapper.annotations.BeanUnwrap;
import io.beanmapper.config.BeanMapperBuilder;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class BeanAliasExample {

    @Test
    public void mapWithAlias() {
        Source source = new Source(1L, "Henk");

        Target target = new BeanMapperBuilder().build()
                .map(source, Target.class);

        assertEquals("Henk", target.otherName);
    }

    public static class Source {
        public Long id;
        @BeanAlias("otherName")
        public String name;

        public Source(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static class Target {
        public String otherName;
    }
}
