package io.beanmapper.config;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

class OverrideFieldTest {

    @Test
    void block() {
        OverrideField<String> text = new OverrideField<>(this::get);
        text.block();
        assertNull(text.get());
    }

    private String get() {
        return "Hello world!";
    }

    @Test
    void testBlockIsTrueAfterPassingNull() throws NoSuchFieldException, IllegalAccessException {
        OverrideField<String> text = new OverrideField<>(this::get);
        text.set(null);
        Field blockField = text.getClass().getDeclaredField("block");
        blockField.setAccessible(true);
        assertTrue((Boolean) blockField.get(text));
    }

}
