package io.beanmapper.providers;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DefaultValuesProviderTest {
    private final DefaultValuesProvider defaultValuesProvider = new DefaultValuesProvider();

    @Test
    void shouldGetNull() {
        Object actualValue = defaultValuesProvider.get(null);
        UndefinedClassInProvider actualValue2 = defaultValuesProvider.get(UndefinedClassInProvider.class);

        assertNull(actualValue);
        assertNull(actualValue2);
    }

    @Test
    void shouldGetDefault() {
        assertEquals(false, defaultValuesProvider.get(boolean.class));
        byte actualDefaultByte = defaultValuesProvider.get(byte.class);
        assertEquals((byte) 0, actualDefaultByte);
        short actualDefaultShort = defaultValuesProvider.get(short.class);
        assertEquals((short) 0, actualDefaultShort);
        int actualDefaultInt = defaultValuesProvider.get(int.class);
        assertEquals(0, actualDefaultInt);
        long actualDefaultLong = defaultValuesProvider.get(long.class);
        assertEquals(0L, actualDefaultLong);
        char actualDefaultChar = defaultValuesProvider.get(char.class);
        assertEquals('\0', actualDefaultChar);
        float actualDefaultFloat = defaultValuesProvider.get(float.class);
        assertEquals(0.0F, actualDefaultFloat);
        double actualDefaultDouble = defaultValuesProvider.get(double.class);
        assertEquals(0.0, actualDefaultDouble);
        assertEquals(Optional.empty(), defaultValuesProvider.get(Optional.class));
        assertEquals(emptyList(), defaultValuesProvider.get(List.class));
        assertEquals(emptySet(), defaultValuesProvider.get(Set.class));
        assertEquals(emptyMap(), defaultValuesProvider.get(Map.class));
    }

    private static final class UndefinedClassInProvider {
    }
}