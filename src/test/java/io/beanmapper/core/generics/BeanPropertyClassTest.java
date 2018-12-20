package io.beanmapper.core.generics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.beanmapper.core.BeanPropertyMatchupDirection;
import io.beanmapper.core.inspector.PropertyAccessor;
import io.beanmapper.core.inspector.PropertyAccessors;

import org.junit.Test;

public class BeanPropertyClassTest {

    @Test
    public void basicList() {
        assertEquals(
                new BeanPropertyClassNormal(List.class),
                new BeanPropertyClassNormal(List.class));
    }

    @Test
    public void basicListAndSetAreNotTheSame() {
        assertNotSame(
                new BeanPropertyClassNormal(List.class),
                new BeanPropertyClassNormal(Set.class));
    }

    @Test
    public void parameterizedAndNonParameterizedAreNeverTheSame() {
        assertNotSame(
                extractBeanPropertyClass(
                        BeanPropertyMatchupDirection.SOURCE_TO_TARGET,
                        SourceClassWithListString.class,
                        "list"),
                new BeanPropertyClassNormal(Set.class));
    }

    @Test
    public void sameListString() {
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
    public void listStringAndListLongAreNotTheSame() {
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
    public void complexTypesWithASlightDifferenceInTheGenerics() {
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
    public void exactSameComplexTypes() {
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
        PropertyAccessor sourceAccessor = new PropertyAccessors().findProperty(containingClass, property);
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

    public class TargetClassWithComplexListLong{
        public List<Map<String, Long>> list;
    }

}
