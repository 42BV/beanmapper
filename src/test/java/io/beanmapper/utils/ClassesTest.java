package io.beanmapper.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class ClassesTest {

    @Test
    void nonExistingClass() {
        assertThrows(IllegalArgumentException.class, () -> Classes.forName("a.b.c.DoesNotExist"));
    }

    @Test
    void genericTypeOfClassWithGenericType() {
        ClassWithGeneric source = new ClassWithGeneric();
        assertEquals(String.class, source.getType());
    }

    @Test
    void genericTypeOfClassWithoutGenericType() {
        ClassWithNestedGeneric source = new ClassWithNestedGeneric();
        assertEquals(List.class, source.getType());
    }

    public static class AbstractClassWithGeneric<C> {
        private final Class<C> type;

        public AbstractClassWithGeneric() {
            this.type = (Class<C>) Classes.getParameteredTypes(getClass())[0];
        }

        public Class<C> getType() {return type;}
    }

    public static class ClassWithGeneric extends AbstractClassWithGeneric<String> {
    }

    public static class ClassWithNestedGeneric extends AbstractClassWithGeneric<List<String>> {
    }
}
