package io.beanmapper.annotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import io.beanmapper.annotations.model.bean_construct.NestedSource;
import io.beanmapper.annotations.model.bean_construct.Source;
import io.beanmapper.annotations.model.bean_construct.Target;
import io.beanmapper.config.BeanMapperBuilder;

import org.junit.jupiter.api.Test;

class BeanConstructTest {

    @Test
    void beanConstruct_shouldUse_argsConstructor() {
        NestedSource nestedSource = new NestedSource();
        nestedSource.nestedField = 42;

        Source source = new Source();
        source.field1 = 40;
        source.field2 = 41;
        source.nested = List.of(nestedSource);

        Target target = new BeanMapperBuilder().build().map(source, Target.class);
        assertEquals(Integer.valueOf(40), target.getField1());
        assertEquals(Integer.valueOf(41), target.getField2());
        assertEquals(Integer.valueOf(42), target.getNested().get(0).getNestedField());
    }
}