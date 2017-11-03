package io.beanmapper.config;

import static org.junit.Assert.assertNull;

import org.junit.Test;

public class OverrideFieldTest {

    @Test
    public void block() {
        OverrideField<String> text = new OverrideField<>(this::get);
        text.block();
        assertNull(text.get());
    }

    private String get() { return "Hello world!"; }

}
