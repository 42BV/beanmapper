package io.beanmapper.core.generics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.beanmapper.core.BeanPropertyMatchupDirection;
import io.beanmapper.core.inspector.PropertyAccessor;
import io.beanmapper.core.inspector.PropertyAccessors;

import org.junit.jupiter.api.Test;

class BeanPropertyClassTest {

    @Test
    void basicList() {
        assertEquals(
                new BeanPropertyClassNormal(List.class),
                new BeanPropertyClassNormal(List.class));
    }

    @Test
    void basicListAndSetAreNotTheSame() {
        assertNotSame(
                new BeanPropertyClassNormal(List.class),
                new BeanPropertyClassNormal(Set.class));
    }

    @Test
    void parameterizedAndNonParameterizedAreNeverTheSame() {
        assertNotSame(
                extractBeanPropertyClass(
                        BeanPropertyMatchupDirection.SOURCE_TO_TARGET,
                        SourceClassWithListString.class,
                        "list"),
                new BeanPropertyClassNormal(Set.class));
    }

    @Test
    void sameListString() {
        assertEquals(
                extractBeanPropertyClass(
                        BeanPropertyMatchupDirection.SOURCE_TO_TARGET,
                        SourceClassWithListString.class,
                        "list"),
                extractBeanPropertyClass(
                        BeanPropertyMatchupDirection.TARGET_TO_SOURCE,
                        TargetClassWithListString.class,
                        "list"));
    }

    @Test
    void listStringAndListLongAreNotTheSame() {
        assertNotSame(
                extractBeanPropertyClass(
                        BeanPropertyMatchupDirection.SOURCE_TO_TARGET,
                        SourceClassWithListString.class,
                        "list"),
                extractBeanPropertyClass(
                        BeanPropertyMatchupDirection.TARGET_TO_SOURCE,
                        TargetClassWithListLong.class,
                        "list"));
    }

    @Test
    void complexTypesWithASlightDifferenceInTheGenerics() {
        assertNotSame(
                extractBeanPropertyClass(
                        BeanPropertyMatchupDirection.SOURCE_TO_TARGET,
                        SourceClassWithComplexListLong.class,
                        "list"),
                extractBeanPropertyClass(
                        BeanPropertyMatchupDirection.TARGET_TO_SOURCE,
                        TargetClassWithComplexListString.class,
                        "list"));
    }

    @Test
    void exactSameComplexTypes() {
        assertEquals(
                extractBeanPropertyClass(
                        BeanPropertyMatchupDirection.SOURCE_TO_TARGET,
                        SourceClassWithComplexListLong.class,
                        "list"),
                extractBeanPropertyClass(
                        BeanPropertyMatchupDirection.TARGET_TO_SOURCE,
                        TargetClassWithComplexListLong.class,
                        "list"));
    }

    private BeanPropertyClass extractBeanPropertyClass(
            BeanPropertyMatchupDirection direction,
            Class containingClass,
            String property) {
        PropertyAccessor sourceAccessor = PropertyAccessors.findProperty(containingClass, property);
        DirectedBeanProperty directedBeanProperty = new DirectedBeanProperty(
                direction,
                sourceAccessor,
                containingClass);
        return directedBeanProperty.getBeanPropertyClass();
    }

    public class SourceClassWithListString {
        public List<String> list;
    }

    public class TargetClassWithListString {
        public List<String> list;
    }

    public class TargetClassWithListLong {
        public List<Long> list;
    }

    public class SourceClassWithComplexListLong {
        public List<Map<String, Long>> list;
    }

    public class TargetClassWithComplexListString {
        public List<Map<String, String>> list;
    }

    public class TargetClassWithComplexListLong {
        public List<Map<String, Long>> list;
    }

}
