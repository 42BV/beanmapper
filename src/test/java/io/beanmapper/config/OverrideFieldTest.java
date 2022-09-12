package io.beanmapper.config;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class OverrideFieldTest {

    @Test
    void block() {
        OverrideField<String> text = new OverrideField<>(this::get);
        text.block();
        assertNull(text.get());
    }

    private String get() {return "Hello world!";}

}
