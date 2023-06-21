package io.beanmapper.core.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.testmodel.optional_getter.EntityFormWithOptionalOfOptional;
import io.beanmapper.testmodel.optional_getter.MyEntity;
import io.beanmapper.testmodel.optional_getter.MyEntityResult;
import io.beanmapper.testmodel.optional_getter.MyEntityResultWithList;
import io.beanmapper.testmodel.optional_getter.MyEntityWithList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OptionalToObjectConverterTest {

    private OptionalToObjectConverter converter;

    private BeanMapper beanMapper;

    private MyEntity myEntity;
    private MyEntity child;

    @BeforeEach
    void setUp() {

        converter = new OptionalToObjectConverter();

        beanMapper = new BeanMapperBuilder().build();

        myEntity = new MyEntity();
        myEntity.value = "Henk";

        child = new MyEntity();
        child.value = "Piet";

        myEntity.child = child;
    }

    @Test
    void convertEntityWithChild() {
        MyEntityResult result = this.beanMapper.map(myEntity, MyEntityResult.class);
        assertEquals(this.myEntity.value, result.value);
        assertEquals(this.myEntity.child.value, result.child.value);
        assertNull(result.child.child);
    }

    @Test
    void convertEntityWithoutChild() {
        this.myEntity.child = null;
        var result = this.beanMapper.map(myEntity, MyEntityResult.class);
        assertEquals(this.myEntity.value, result.value);
        assertNull(result.child);
    }

    @Test
    void matchIsTrueForSourceOptionalAndObjectTarget() {
        assertTrue(this.converter.match(Optional.class, Object.class));
    }

    @Test
    void matchIsFalseForSourceNotOptional() {
        assertFalse(this.converter.match(Object.class, Objects.class));
    }

    @Test
    void convertEntityWithChildToSameEntity() {
        var result = this.beanMapper.map(myEntity, MyEntity.class);
        assertEquals(myEntity.value, result.value);
        assertEquals(myEntity.child.value, result.child.value);
        assertEquals(myEntity.child.child, result.child.child);
    }

    @Test
    void convertEntityWithoutChildToSameEntity() {
        this.myEntity.child = null;
        var result = this.beanMapper.map(myEntity, MyEntity.class);
        assertEquals(myEntity.value, result.value);
        assertNull(result.child);
    }

    @Test
    void convertMyEntityWithListToEntityResultWithList() {
        MyEntityWithList[] children = new MyEntityWithList[] { new MyEntityWithList(), new MyEntityWithList() };
        children[0].value = "Klaas";
        children[1].value = "Piet";

        MyEntityWithList form = new MyEntityWithList();
        form.value = "Henk";
        form.children = List.of(children);

        var result = this.beanMapper.map(form, MyEntityResultWithList.class);
        assertEquals(2, result.children.size());
    }

    @Test
    void convertEntityWithOptionalOfOptionalOfListToEntityWithList() {
        EntityFormWithOptionalOfOptional child1 = new EntityFormWithOptionalOfOptional();
        child1.value = "Klaas";
        child1.children = Optional.of(Optional.of(Collections.emptyList()));

        EntityFormWithOptionalOfOptional child2 = new EntityFormWithOptionalOfOptional();
        child2.value = "Piet";
        child2.children = Optional.of(Optional.of(Collections.emptyList()));

        List<EntityFormWithOptionalOfOptional> children = List.of(child1, child2);

        EntityFormWithOptionalOfOptional form = new EntityFormWithOptionalOfOptional();
        form.value = "Henk";
        form.children = Optional.of(Optional.of(children));

        var result = beanMapper.map(form, MyEntityWithList.class);
        assertEquals("Henk", result.value);
        assertEquals(2, result.children.size());
    }

    @Test
    void convertListWithOptionalToListOfDifferentType() {
        List<Optional<Integer>> list = List.of(Optional.of(42), Optional.of(23), Optional.empty());
        Collection<Long> listOfLong = this.beanMapper.map(list, Long.TYPE);
        assertEquals(list.size(), listOfLong.size());
        assertTrue(listOfLong.contains(42L));
        assertTrue(listOfLong.contains(23L));
        assertTrue(listOfLong.contains(null));
    }

    @Test
    void convertListWithoutOptionalToListOfWithOptional() {
        List<Optional<Integer>> list = List.of(Optional.of(42), Optional.of(23), Optional.empty());
        Collection<Long> listOfLong = this.beanMapper.map(list, Long.TYPE);
        List<Optional<Integer>> unoReversedList = this.beanMapper.map(listOfLong, Integer.TYPE).stream().map(Optional::ofNullable).toList();
        assertEquals(list.size(), listOfLong.size());
        assertTrue(unoReversedList.contains(Optional.of(42)));
        assertTrue(unoReversedList.contains(Optional.of(23)));
        assertTrue(unoReversedList.contains(Optional.<Integer>of(this.beanMapper.getConfiguration().getDefaultValueForClass(Integer.TYPE))));
    }

    @Test
    void convertMapWithOptionalToMapOfDifferentType() {
        Map<Integer, Optional<Integer>> map = Map.of(42, Optional.of(42), 23, Optional.of(23));
        Map<Integer, Optional<Long>> longMap = this.beanMapper.map(map, Long.TYPE).entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), Optional.ofNullable(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertEquals(map.size(), longMap.size());
        assertEquals(Optional.of(42L), longMap.get(42));
        assertEquals(Optional.of(23L), longMap.get(23));
    }

    public static class ClassWithOptionalOfListOfOptionalOfInteger {
        public Optional<List<Optional<Integer>>> list = Optional.of(List.of(Optional.of(1), Optional.of(23), Optional.of(42)));
    }

    public static class ClassWithListOfLong {
        public List<Long> list;
    }

    @Test
    void convertOptionalOfListToList() {
        var form = new ClassWithOptionalOfListOfOptionalOfInteger();
        var result = this.beanMapper.map(form, ClassWithListOfLong.class);
        assertEquals(3, result.list.size());
        assertTrue(result.list.contains(1L));
        assertTrue(result.list.contains(23L));
        assertTrue(result.list.contains(42L));
    }
}
