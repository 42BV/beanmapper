package io.beanmapper.strategy;

import static io.beanmapper.shared.AssertionUtils.assertFieldWithNameHasValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.exceptions.RecordConstructorConflictException;
import io.beanmapper.exceptions.RecordNoAvailableConstructorsExceptions;
import io.beanmapper.shared.AssertionUtils;
import io.beanmapper.shared.ReflectionUtils;
import io.beanmapper.strategy.record.model.AlternativeResultRecordWithIdAndName;
import io.beanmapper.strategy.record.model.FormRecordWithId_Name_Place_BankAccount;
import io.beanmapper.strategy.record.model.FormWithIdAndName;
import io.beanmapper.strategy.record.model.FormWithId_Name_Place_BankAccount;
import io.beanmapper.strategy.record.model.FormWithName;
import io.beanmapper.strategy.record.model.ResultRecordWithIdAndName;
import io.beanmapper.strategy.record.model.ResultRecordWithId_Name_Place;
import io.beanmapper.strategy.record.model.ResultRecordWithNameAndPlace;
import io.beanmapper.strategy.record.model.SourceWithNested;
import io.beanmapper.strategy.record.model.TargetWithNested;
import io.beanmapper.strategy.record.model.annotation.bean_alias.form.FormWithCollection;
import io.beanmapper.strategy.record.model.annotation.bean_alias.form.FormWithId_Name_Place;
import io.beanmapper.strategy.record.model.annotation.record_construct.RecordWithAllConstructorsExcluded;
import io.beanmapper.strategy.record.model.annotation.record_construct.RecordWithMultipleForcedConstructors;
import io.beanmapper.strategy.record.model.collection.form.FormRecordWithList;
import io.beanmapper.strategy.record.model.collection.form.FormRecordWithMap;
import io.beanmapper.strategy.record.model.collection.form.FormRecordWithQueue;
import io.beanmapper.strategy.record.model.collection.form.FormRecordWithSet;
import io.beanmapper.strategy.record.model.collection.form.FormWithList;
import io.beanmapper.strategy.record.model.collection.form.FormWithMap;
import io.beanmapper.strategy.record.model.collection.form.FormWithQueue;
import io.beanmapper.strategy.record.model.collection.form.FormWithSet;
import io.beanmapper.strategy.record.model.collection.result.ResultRecordWithList;
import io.beanmapper.strategy.record.model.collection.result.ResultRecordWithMap;
import io.beanmapper.strategy.record.model.collection.result.ResultRecordWithName;
import io.beanmapper.strategy.record.model.collection.result.ResultRecordWithQueue;
import io.beanmapper.strategy.record.model.collection.result.ResultRecordWithSet;
import io.beanmapper.strategy.record.model.inheritance.Layer3;
import io.beanmapper.testmodel.person.Person;
import io.beanmapper.utils.DefaultValues;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MapToRecordStrategyTest {

    private BeanMapper beanMapper;

    @BeforeEach
    void setUp() {
        this.beanMapper = new BeanMapperBuilder().build();
    }

    @Test
    void testMapObjectToRecordWithEqualFields() {
        var form = new FormWithIdAndName();
        var result = this.beanMapper.map(form, ResultRecordWithIdAndName.class);

        assertEquals(form.id, result.id());
        assertEquals(form.name, result.name());
    }

    @Test
    void testMapObjectToRecordWithFewerFields() {
        var form = new FormWithIdAndName();
        var result = this.beanMapper.map(form, ResultRecordWithName.class);

        assertEquals(form.name, result.name());
        AssertionUtils.assertInstanceDoesNotContainField(result, "id");
    }

    @Test
    void testMapObjectToRecordWithMoreFields() {
        var form = new FormWithName();
        var result = this.beanMapper.map(form, ResultRecordWithIdAndName.class);

        assertEquals((int) DefaultValues.defaultValueFor(int.class), result.id());
        assertEquals(form.name, result.name());
    }

    @Test
    void testMapObjectWithListToRecord() {
        var form = new FormWithList();
        var result = this.beanMapper.map(form, ResultRecordWithList.class);

        assertEquals(form.id, result.id());
        assertEquals(form.name, result.name());
        assertEquals(form.numbers.size(), result.numbers().size());
        assertTrue(result.numbers().containsAll(List.of(1, 2, 3)));
    }

    @Test
    void testMapObjectWithSetToRecord() {
        var form = new FormWithSet();
        var result = this.beanMapper.map(form, ResultRecordWithSet.class);

        assertEquals(form.id, result.id());
        assertEquals(form.name, result.name());
        assertEquals(form.numbers.size(), result.numbers().size());
        assertTrue(result.numbers().containsAll(Set.of(1, 2, 3)));
    }

    @Test
    void testMapObjectWithQueueToRecord() {
        var form = new FormWithQueue();
        var result = this.beanMapper.map(form, ResultRecordWithQueue.class);

        assertEquals(form.id, result.id());
        assertEquals(form.name, result.name());
        assertEquals(form.numbers.size(), result.numbers().size());
        assertTrue(result.numbers().containsAll(Set.of(1, 2, 3)));
    }

    @Test
    void testMapObjectWithMapToRecord() {
        var form = new FormWithMap();
        var result = this.beanMapper.map(form, ResultRecordWithMap.class);

        assertEquals(form.id, result.id());
        assertEquals(form.name, result.name());
        assertEquals(form.numbers.size(), result.numbers().size());
        assertEquals(1, result.numbers().get(1));
        assertEquals(2, result.numbers().get(2));
        assertEquals(3, result.numbers().get(3));
    }

    @Test
    void testFieldsOfSuperClassAreMappedToRecord() {
        var form = new Layer3();
        var result = this.beanMapper.map(form, ResultRecordWithId_Name_Place.class);

        assertEquals(form.name, result.name());
        assertEquals(form.id, result.id());
        assertEquals(form.place, result.place());
    }

    @Test
    void testMapEmptyObjectToRecord() {
        var form = new Object();
        var result = this.beanMapper.map(form, ResultRecordWithIdAndName.class);

        assertEquals(0, result.id());
        assertNull(result.name());
    }

    @Test
    void testCustomDefaultsApplyToFieldsRecordMapping() {
        this.beanMapper = new BeanMapperBuilder()
                .addCustomDefaultValue(String.class, "Henk")
                .addCustomDefaultValue(int.class, 42)
                .build();

        var form = new Object();
        var result = this.beanMapper.map(form, ResultRecordWithIdAndName.class);

        assertEquals(42, result.id());
        assertEquals("Henk", result.name());
    }

    @Test
    void testCustomDefaultsApplyToRecordMapping() {
        var result = this.beanMapper.wrap()
                .addCustomDefaultValue(ResultRecordWithIdAndName.class, new ResultRecordWithIdAndName(42, "Henk"))
                .build()
                .map((Object) null, ResultRecordWithIdAndName.class);

        assertEquals(42, result.id());
        assertEquals("Henk", result.name());
    }

    @Test
    void testMapRecordToRecordWithEqualFields() {
        var form = new ResultRecordWithIdAndName(42, "Henk");
        var result = this.beanMapper.map(form, AlternativeResultRecordWithIdAndName.class);

        assertEquals(form.id(), result.id());
        assertEquals(form.name(), result.name());
    }

    @Test
    void testMapRecordToRecordWithFewerFields() {
        var form = new ResultRecordWithIdAndName(42, "Henk");
        var result = this.beanMapper.map(form, ResultRecordWithName.class);

        assertEquals(form.name(), result.name());
    }

    @Test
    void testMapRecordToRecordWithMoreFields() {
        var form = new ResultRecordWithName("Henk");
        var result = this.beanMapper.map(form, ResultRecordWithIdAndName.class);

        assertEquals(form.name(), result.name());
        assertEquals((int) this.beanMapper.configuration().getDefaultValueForClass(int.class), result.id());
    }

    @Test
    void testMapRecordWithListToRecord() {
        var form = new FormRecordWithList(42, "Henk", List.of("1", "2", "3"));
        var result = this.beanMapper.map(form, ResultRecordWithList.class);

        assertEquals(form.id(), result.id());
        assertEquals(form.name(), result.name());
        assertEquals(form.numbers().size(), result.numbers().size());
        assertTrue(result.numbers().containsAll(List.of(1, 2, 3)));
    }

    @Test
    void testMapRecordWithSetToRecord() {
        var form = new FormRecordWithSet(42, "Henk", Set.of("1", "2", "3"));
        var result = this.beanMapper.map(form, ResultRecordWithSet.class);

        assertEquals(form.id(), result.id());
        assertEquals(form.name(), result.name());
        assertEquals(form.numbers().size(), result.numbers().size());
        assertTrue(result.numbers().containsAll(Set.of(1, 2, 3)));
    }

    @Test
    void testMapRecordWithQueueToRecord() {
        var form = new FormRecordWithQueue(42, "Henk", new ArrayDeque<>() {
            {
                add("1");
                add("2");
                add("3");
            }
        });
        var result = this.beanMapper.map(form, ResultRecordWithQueue.class);

        assertEquals(form.id(), result.id());
        assertEquals(form.name(), result.name());
        assertEquals(form.numbers().size(), result.numbers().size());
    }

    @Test
    void testMapRecordWithMapToRecord() {
        var form = new FormRecordWithMap(42, "Henk", Map.of(1, "1", 2, "2", 3, "3"));
        var result = this.beanMapper.map(form, ResultRecordWithMap.class);

        assertEquals(form.id(), result.id());
        assertEquals(form.name(), result.name());
        assertEquals(1, result.numbers().get(1));
        assertEquals(2, result.numbers().get(2));
        assertEquals(3, result.numbers().get(3));
    }

    @Test
    void testMapDownsizedSourceClassWithFieldAnnotatedWithBeanAliasToRecord() {
        var form = new FormWithId_Name_Place_BankAccount(42, "Henk", "Zoetermeer", "NL00ABCD0000000000");
        var result = this.beanMapper.wrap()
                .downsizeSource(List.of("abc", "def", "ghi"))
                .build()
                .map(form, ResultRecordWithNameAndPlace.class);

        assertEquals(form.abc, result.name());
        assertEquals(form.ghi, result.place());
        AssertionUtils.assertInstanceDoesNotContainField(result, "id");
        AssertionUtils.assertInstanceDoesNotContainField(result, "bankAccount");
    }

    @Test
    void testMapClassToDownsizedTargetRecord() {
        var form = new Person();
        form.setName("Henk");
        form.setId(42L);
        form.setBankAccount("NL00ABCD0000000000");
        form.setPlace("Zoetermeer");

        var result = this.beanMapper.wrap()
                .downsizeTarget(List.of("name", "place"))
                .setTargetClass(ResultRecordWithId_Name_Place.class)
                .build()
                .map(form);

        assertNotNull(result);
        AssertionUtils.assertInstanceContainsField(result, "name");
        AssertionUtils.assertInstanceContainsField(result, "place");
        AssertionUtils.assertInstanceDoesNotContainField(result, "bankAccount");
        AssertionUtils.assertInstanceDoesNotContainField(result, "id");
        assertFieldWithNameHasValue(result, "name", "Henk");
        assertFieldWithNameHasValue(result, "place", "Zoetermeer");
    }

    @Test
    void testMapDownsizedSourceClassToDownsizedTargetRecord() {
        var form = new Person();
        form.setName("Henk");
        form.setId(42L);
        form.setBankAccount("NL00ABCD0000000000");
        form.setPlace("Zoetermeer");

        var result = this.beanMapper.wrap()
                .downsizeSource(List.of("name", "place", "bankAccount"))
                .downsizeTarget(List.of("name", "place"))
                .setTargetClass(ResultRecordWithId_Name_Place.class)
                .build()
                .map(form);

        assertNotNull(result);
        AssertionUtils.assertInstanceContainsField(result, "name");
        AssertionUtils.assertInstanceContainsField(result, "place");
        AssertionUtils.assertInstanceDoesNotContainField(result, "bankAccount");
        AssertionUtils.assertInstanceDoesNotContainField(result, "id");
        assertFieldWithNameHasValue(result, "name", "Henk");
        assertFieldWithNameHasValue(result, "place", "Zoetermeer");
    }

    @Test
    void testMapDownsizedSourceRecordToRecord() {
        var form = new FormRecordWithId_Name_Place_BankAccount();
        var result = this.beanMapper.wrap()
                .downsizeSource(List.of("name", "place"))
                .build()
                .map(form, ResultRecordWithId_Name_Place.class);

        assertEquals(0, result.id());
        assertEquals(form.name(), result.name());
        assertEquals(form.place(), result.place());
    }

    @Test
    void testMapRecordToDownsizedTargetRecord() {
        var form = new FormRecordWithId_Name_Place_BankAccount();
        var result = this.beanMapper.wrap()
                .downsizeTarget(List.of("name", "place"))
                .setTargetClass(ResultRecordWithId_Name_Place.class)
                .build()
                .map(form);

        assertEquals(form.name(), ReflectionUtils.getValueOfField(result, ReflectionUtils.getFieldWithName(result.getClass(), "name")));
        assertEquals(form.place(), ReflectionUtils.getValueOfField(result, ReflectionUtils.getFieldWithName(result.getClass(), "place")));
    }

    @Test
    void testMapDownsizedSourceRecordToDownsizedTargetRecord() {
        var form = new FormRecordWithId_Name_Place_BankAccount();
        var result = this.beanMapper.wrap()
                .downsizeTarget(List.of("name"))
                .downsizeSource(List.of("name", "place"))
                .setTargetClass(ResultRecordWithId_Name_Place.class)
                .build()
                .map(form);

        assertEquals(form.name(), ReflectionUtils.getValueOfField(result, ReflectionUtils.getFieldWithName(result.getClass(), "name")));
        AssertionUtils.assertInstanceDoesNotContainField(result, "id");
        AssertionUtils.assertInstanceDoesNotContainField(result, "place");
    }

    @Test
    void testMapClassWithFieldsAnnotatedWithBeanAliasToRecord() {
        var form = new FormWithId_Name_Place();
        var result = this.beanMapper.map(form, ResultRecordWithId_Name_Place.class);

        assertEquals(form.abc, result.name());
        assertEquals(form.def, result.id());
        assertEquals(form.ghi, result.place());
    }

    @Test
    void testMapClassWithFieldsAnnotatedWithBeanAliasToDissimilarRecord() {
        var form = new FormWithId_Name_Place();
        var result = this.beanMapper.map(form, ResultRecordWithIdAndName.class);

        assertEquals(form.abc, result.name());
        assertEquals(form.def, result.id());
        AssertionUtils.assertInstanceDoesNotContainField(result, "place");
    }

    @Test
    void testMapClassWithListAnnotatedWithBeanAliasToRecord() {
        var form = new io.beanmapper.strategy.record.model.annotation.bean_alias.form.FormWithList();
        var result = this.beanMapper.map(form, ResultRecordWithList.class);

        assertEquals(form.abc, result.name());
        assertEquals(form.def, result.id());
        assertEquals(form.ghi.size(), result.numbers().size());
        assertTrue(result.numbers().containsAll(List.of(1, 2, 3)));
        AssertionUtils.assertInstanceDoesNotContainField(result, "place");
    }

    @Test
    void testMapClassWithCollectionAnnotatedWithBeanAliasToRecord() {
        var form = new FormWithCollection();
        var result = this.beanMapper.map(form, ResultRecordWithList.class);

        assertEquals(form.abc, result.name());
        assertEquals(form.def, result.id());
        assertEquals(ArrayList.class, result.numbers().getClass());
        assertEquals(form.ghi.size(), result.numbers().size());
        assertTrue(result.numbers().containsAll(List.of(1, 2, 3)));
        AssertionUtils.assertInstanceDoesNotContainField(result, "place");
    }

    @Test
    void testMapToRecordWithMultipleForcedConstructors_ShouldThrow_RecordConstructorConflictException() {
        var form = new FormWithIdAndName();
        assertThrows(RecordConstructorConflictException.class, () -> this.beanMapper.map(form, RecordWithMultipleForcedConstructors.class));
    }

    @Test
    void testMapToRecordWhereAllConstructorsAreExcluded_ShouldThrow_RecordNoAvailableConstructorsException() {
        var form = new FormWithIdAndName();
        var result = assertThrows(RecordNoAvailableConstructorsExceptions.class,
                () -> this.beanMapper.map(form, RecordWithAllConstructorsExcluded.class));
        System.out.println(result.getMessage());
    }

    @Test
    void testRecordWithNestedToTargetWithNested() {
        var source = new SourceWithNested("Henk", new SourceWithNested.NestedSource("Jan"));
        var result = this.beanMapper.map(source, TargetWithNested.class);
        assertEquals(source.name(), result.name());
        assertEquals(source.nested().name(), result.nested().name());
    }
}