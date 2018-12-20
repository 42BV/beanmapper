package io.beanmapper.core.generics;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import io.beanmapper.core.BeanPropertyMatchupDirection;
import io.beanmapper.core.inspector.PropertyAccessor;
import io.beanmapper.core.inspector.PropertyAccessors;

import org.junit.Test;

public class DirectedBeanPropertyTest {

    @Test
    public void verifyInSourceRole() {
        DirectedBeanProperty directedBeanProperty = getDirectedBeanProperty(
                ClassContainingTypedList.class,
                BeanPropertyMatchupDirection.SOURCE_TO_TARGET,
                "list");
        assertEquals(List.class, directedBeanProperty.getBeanPropertyClass().getBasicType());
        assertEquals(Integer.class, directedBeanProperty.getBeanPropertyClass().getParameterizedType(0));
        assertEquals(List.class, directedBeanProperty.getBeanFieldClass().getBasicType());
        assertEquals(String.class, directedBeanProperty.getBeanFieldClass().getParameterizedType(0));
    }

    @Test
    public void verifyInTargetRole() {
        DirectedBeanProperty directedBeanProperty = getDirectedBeanProperty(
                ClassContainingTypedList.class,
                BeanPropertyMatchupDirection.TARGET_TO_SOURCE,
                "list");
        assertEquals(List.class, directedBeanProperty.getBeanPropertyClass().getBasicType());
        assertEquals(Long.class, directedBeanProperty.getBeanPropertyClass().getParameterizedType(0));
        assertEquals(List.class, directedBeanProperty.getBeanFieldClass().getBasicType());
        assertEquals(String.class, directedBeanProperty.getBeanFieldClass().getParameterizedType(0));
    }

    public class ClassContainingTypedList {

        private List<String> list;
        public List<Integer> getList() {
            return this.list.stream().map(Integer::parseInt).collect(Collectors.toList());
        }

        public void setList(List<Long> list) {
            this.list = list.stream().map(Object::toString).collect(Collectors.toList());
        }

    }

    @Test
    public void containsNoFieldInSourceRole() {
        DirectedBeanProperty directedBeanProperty = getDirectedBeanProperty(
                ClassContainingNoField.class,
                BeanPropertyMatchupDirection.SOURCE_TO_TARGET,
                "list");
        assertEquals(Integer.class, directedBeanProperty.getGenericClassOfProperty(0));
    }

    @Test
    public void containsNoFieldInTargetRole() {
        DirectedBeanProperty directedBeanProperty = getDirectedBeanProperty(
                ClassContainingNoField.class,
                BeanPropertyMatchupDirection.TARGET_TO_SOURCE,
                "list");
        assertEquals(Long.class, directedBeanProperty.getGenericClassOfProperty(0));
    }

    public class ClassContainingNoField {
        public List<Integer> getList() { return null; }
        public void setList(List<Long> list) {}
    }

    @Test
    public void theGetterHasADifferentTypeFromTheField() {
        DirectedBeanProperty directedBeanProperty = getDirectedBeanProperty(
                ClassContainingAllDifferentTypes.class,
                BeanPropertyMatchupDirection.SOURCE_TO_TARGET,
                "list");
        assertEquals(String.class, directedBeanProperty.getGenericClassOfProperty(0));
    }

    @Test
    public void theSetterHasADifferentTypeFromTheField() {
        DirectedBeanProperty directedBeanProperty = getDirectedBeanProperty(
                ClassContainingAllDifferentTypes.class,
                BeanPropertyMatchupDirection.TARGET_TO_SOURCE,
                "list");
        assertEquals(Long.class, directedBeanProperty.getGenericClassOfProperty(0));
    }

    public class ClassContainingAllDifferentTypes {
        private List<Integer> list;
        public List<String> getList() { return null; }
        public void setList(List<Long> list) { }
    }

    private DirectedBeanProperty getDirectedBeanProperty(
            Class containingClass,
            BeanPropertyMatchupDirection sourceToTarget,
            String property) {
        PropertyAccessor accessor = new PropertyAccessors().findProperty(containingClass, property);
        return new DirectedBeanProperty(
                sourceToTarget,
                accessor,
                containingClass);
    }

}
