package io.beanmapper;

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.beanmapper.config.BeanMapperBuilder;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StrategiesExample {

    @Test
    void mapToClass() {
        Source source = new Source(1L, "Henk", 42);

        Target target = new BeanMapperBuilder().build()
                .map(source, Target.class);

        assertEquals("Henk", target.name);
        assertEquals(42, target.age, 0);
    }

    @Test
    void mapToInstance() {
        Source source = new Source(1L, "Henk", 42);

        Target target = new BeanMapperBuilder().build()
                .map(source, Target.class);

        assertEquals("Henk", target.name);
        assertEquals(42, target.age, 0);
    }

    @Test
    void mapToCollection() {
        List<Source> sources = new ArrayList<Source>();
        sources.add(new Source(1L, "Henk", 42));
        sources.add(new Source(2L, "Piet", 50));
        sources.add(new Source(3L, "Kees", 3));

        ArrayList<Target> targets = (ArrayList<Target>) new BeanMapperBuilder().build()
                .map(sources, Target.class, ArrayList.class);

        assertEquals(3, targets.size(), 0);
        assertEquals("Henk", targets.get(0).name);
        assertEquals("Piet", targets.get(1).name);
        assertEquals("Kees", targets.get(2).name);
    }

    @Test
    void downsizeSource() {
        BeanMapper beanMapper = new BeanMapperBuilder().build();
        Source source = new Source(1L, "Henk", 42);
        Target target = new Target("Piet", 12);

        beanMapper.wrapConfig()
                .downsizeSource(Arrays.asList("age"))
                .build()
                .map(source, target);

        assertEquals("Piet", target.name);
        assertEquals(42, target.age, 0);
    }

    @Test
    void downsizeTarget() throws JsonProcessingException {
        BeanMapper beanMapper = new BeanMapperBuilder().build();
        Source source = new Source(1L, "Henk", 42);

        Object target = beanMapper.wrapConfig()
                .downsizeTarget(Arrays.asList("name"))
                .build()
                .map(source, Target.class);

        String json = new ObjectMapper().writeValueAsString(target);
        assertEquals("{\"name\":\"Henk\"}", json);
    }

    public static class Source {
        public Long id;
        public String name;
        public Integer age;

        public Source(Long id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }
    }

    public static class Target {
        public String name;
        public Integer age;

        public Target() {
        }

        public Target(String name, Integer age) {
            this.name = name;
            this.age = age;
        }
    }
}
