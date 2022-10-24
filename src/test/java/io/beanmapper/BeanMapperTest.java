package io.beanmapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;

import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.config.AfterClearFlusher;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.config.FlushAfterClearInstruction;
import io.beanmapper.config.RoleSecuredCheck;
import io.beanmapper.core.BeanStrictMappingRequirementsException;
import io.beanmapper.core.converter.impl.LocalDateTimeToLocalDate;
import io.beanmapper.core.converter.impl.LocalDateToLocalDateTime;
import io.beanmapper.core.converter.impl.NestedSourceClassToNestedTargetClassConverter;
import io.beanmapper.core.converter.impl.ObjectToStringConverter;
import io.beanmapper.exceptions.BeanConversionException;
import io.beanmapper.exceptions.BeanMappingException;
import io.beanmapper.exceptions.BeanNoLogicSecuredCheckSetException;
import io.beanmapper.exceptions.BeanNoRoleSecuredCheckSetException;
import io.beanmapper.exceptions.BeanNoSuchPropertyException;
import io.beanmapper.testmodel.anonymous.Book;
import io.beanmapper.testmodel.anonymous.BookForm;
import io.beanmapper.testmodel.beanalias.NestedSourceWithAlias;
import io.beanmapper.testmodel.beanalias.SourceWithAlias;
import io.beanmapper.testmodel.beanalias.TargetWithAlias;
import io.beanmapper.testmodel.beanproperty.SourceBeanProperty;
import io.beanmapper.testmodel.beanproperty.SourceNestedBeanProperty;
import io.beanmapper.testmodel.beanproperty.TargetBeanProperty;
import io.beanmapper.testmodel.beanproperty.TargetNestedBeanProperty;
import io.beanmapper.testmodel.beansecuredfield.CheckSameNameLogicCheck;
import io.beanmapper.testmodel.beansecuredfield.NeverReturnTrueCheck;
import io.beanmapper.testmodel.beansecuredfield.SFSourceAWithSecuredField;
import io.beanmapper.testmodel.beansecuredfield.SFSourceB;
import io.beanmapper.testmodel.beansecuredfield.SFSourceCWithSecuredMethod;
import io.beanmapper.testmodel.beansecuredfield.SFSourceDLogicSecured;
import io.beanmapper.testmodel.beansecuredfield.SFSourceELogicSecured;
import io.beanmapper.testmodel.beansecuredfield.SFTargetA;
import io.beanmapper.testmodel.beansecuredfield.SFTargetBWithSecuredField;
import io.beanmapper.testmodel.collections.CollSourceClear;
import io.beanmapper.testmodel.collections.CollSourceClearFlush;
import io.beanmapper.testmodel.collections.CollSourceConstruct;
import io.beanmapper.testmodel.collections.CollSourceListIncompleteAnnotation;
import io.beanmapper.testmodel.collections.CollSourceListNotAnnotated;
import io.beanmapper.testmodel.collections.CollSourceMapNotAnnotated;
import io.beanmapper.testmodel.collections.CollSourceNoGenerics;
import io.beanmapper.testmodel.collections.CollSourceReuse;
import io.beanmapper.testmodel.collections.CollSubTargetList;
import io.beanmapper.testmodel.collections.CollTarget;
import io.beanmapper.testmodel.collections.CollTargetEmptyList;
import io.beanmapper.testmodel.collections.CollTargetListNotAnnotated;
import io.beanmapper.testmodel.collections.CollTargetListNotAnnotatedUseSetter;
import io.beanmapper.testmodel.collections.CollTargetMapNotAnnotated;
import io.beanmapper.testmodel.collections.CollTargetNoGenerics;
import io.beanmapper.testmodel.collections.CollectionListSource;
import io.beanmapper.testmodel.collections.CollectionListTarget;
import io.beanmapper.testmodel.collections.CollectionListTargetClear;
import io.beanmapper.testmodel.collections.CollectionMapSource;
import io.beanmapper.testmodel.collections.CollectionMapTarget;
import io.beanmapper.testmodel.collections.CollectionPriorityQueueTarget;
import io.beanmapper.testmodel.collections.CollectionQueueSource;
import io.beanmapper.testmodel.collections.CollectionSetSource;
import io.beanmapper.testmodel.collections.CollectionSetTarget;
import io.beanmapper.testmodel.collections.CollectionSetTargetIncorrectSubtype;
import io.beanmapper.testmodel.collections.CollectionSetTargetSpecificSubtype;
import io.beanmapper.testmodel.collections.SourceWithListGetter;
import io.beanmapper.testmodel.collections.TargetWithListPublicField;
import io.beanmapper.testmodel.collections.target_is_wrapped.SourceWithUnwrappedItems;
import io.beanmapper.testmodel.collections.target_is_wrapped.TargetWithWrappedItems;
import io.beanmapper.testmodel.collections.target_is_wrapped.UnwrappedSource;
import io.beanmapper.testmodel.collections.target_is_wrapped.UnwrappedToWrappedBeanConverter;
import io.beanmapper.testmodel.collections.target_is_wrapped.WrappedTarget;
import io.beanmapper.testmodel.construct.NestedSourceWithoutConstruct;
import io.beanmapper.testmodel.construct.SourceWithConstruct;
import io.beanmapper.testmodel.construct.TargetWithoutConstruct;
import io.beanmapper.testmodel.construct_not_matching.BigConstructTarget;
import io.beanmapper.testmodel.construct_not_matching.BigConstructTarget2;
import io.beanmapper.testmodel.construct_not_matching.FlatConstructSource;
import io.beanmapper.testmodel.construct_not_matching.FlatConstructSource2;
import io.beanmapper.testmodel.converter.SourceWithDate;
import io.beanmapper.testmodel.converter.TargetWithDateTime;
import io.beanmapper.testmodel.converter_between_nested_classes.NestedSourceClass;
import io.beanmapper.testmodel.converter_between_nested_classes.NestedTargetClass;
import io.beanmapper.testmodel.converter_between_nested_classes.SourceWithNestedClass;
import io.beanmapper.testmodel.converter_between_nested_classes.TargetWithNestedClass;
import io.beanmapper.testmodel.converter_for_classes_in_collection.SourceWithCollection;
import io.beanmapper.testmodel.converter_for_classes_in_collection.TargetWithCollection;
import io.beanmapper.testmodel.defaults.SourceWithDefaults;
import io.beanmapper.testmodel.defaults.TargetWithDefaults;
import io.beanmapper.testmodel.emptyobject.EmptySource;
import io.beanmapper.testmodel.emptyobject.EmptyTarget;
import io.beanmapper.testmodel.emptyobject.NestedEmptyTarget;
import io.beanmapper.testmodel.encapsulate.Address;
import io.beanmapper.testmodel.encapsulate.Country;
import io.beanmapper.testmodel.encapsulate.House;
import io.beanmapper.testmodel.encapsulate.ResultAddress;
import io.beanmapper.testmodel.encapsulate.ResultManyToMany;
import io.beanmapper.testmodel.encapsulate.ResultManyToOne;
import io.beanmapper.testmodel.encapsulate.ResultOneToMany;
import io.beanmapper.testmodel.encapsulate.source_annotated.Car;
import io.beanmapper.testmodel.encapsulate.source_annotated.CarDriver;
import io.beanmapper.testmodel.encapsulate.source_annotated.Driver;
import io.beanmapper.testmodel.enums.ColorEntity;
import io.beanmapper.testmodel.enums.ColorResult;
import io.beanmapper.testmodel.enums.ColorStringResult;
import io.beanmapper.testmodel.enums.Day;
import io.beanmapper.testmodel.enums.DayEnumSourceArraysAsList;
import io.beanmapper.testmodel.enums.EnumSourceArraysAsList;
import io.beanmapper.testmodel.enums.EnumTargetList;
import io.beanmapper.testmodel.enums.RGB;
import io.beanmapper.testmodel.enums.UserRole;
import io.beanmapper.testmodel.enums.UserRoleResult;
import io.beanmapper.testmodel.enums.WeekEntity;
import io.beanmapper.testmodel.enums.WeekResult;
import io.beanmapper.testmodel.enums.WeekStringResult;
import io.beanmapper.testmodel.ignore.IgnoreSource;
import io.beanmapper.testmodel.ignore.IgnoreTarget;
import io.beanmapper.testmodel.initially_unmatched_source.SourceWithUnmatchedField;
import io.beanmapper.testmodel.initially_unmatched_source.TargetWithoutUnmatchedField;
import io.beanmapper.testmodel.innerclass.SourceWithInnerClass;
import io.beanmapper.testmodel.innerclass.TargetWithInnerClass;
import io.beanmapper.testmodel.mappable_enum.CountryEnum;
import io.beanmapper.testmodel.mappable_enum.CountryHolder;
import io.beanmapper.testmodel.mappable_enum.CountryResult;
import io.beanmapper.testmodel.mappable_enum.CountryResultHolder;
import io.beanmapper.testmodel.multiple_unwrap.AllTogether;
import io.beanmapper.testmodel.multiple_unwrap.LayerA;
import io.beanmapper.testmodel.nested_classes.Layer1;
import io.beanmapper.testmodel.nested_classes.Layer1Result;
import io.beanmapper.testmodel.nested_classes.Layer3;
import io.beanmapper.testmodel.nested_classes.Layer4;
import io.beanmapper.testmodel.nested_generics.SourceWithNestedGenerics;
import io.beanmapper.testmodel.nested_generics.TargetWithNestedGenerics;
import io.beanmapper.testmodel.not_accessible.source_contains_nested_class.SourceWithPerson;
import io.beanmapper.testmodel.not_accessible.source_contains_nested_class.TargetWithPersonName;
import io.beanmapper.testmodel.not_accessible.target_contains_nested_class.SourceWithPersonName;
import io.beanmapper.testmodel.not_accessible.target_contains_nested_class.TargetWithPerson;
import io.beanmapper.testmodel.numbers.ClassWithInteger;
import io.beanmapper.testmodel.numbers.ClassWithLong;
import io.beanmapper.testmodel.numbers.SourceWithDouble;
import io.beanmapper.testmodel.numbers.TargetWithDouble;
import io.beanmapper.testmodel.optional_getter.MyEntity;
import io.beanmapper.testmodel.optional_getter.MyEntityResult;
import io.beanmapper.testmodel.othername.SourceWithOtherName;
import io.beanmapper.testmodel.othername.TargetWithOtherName;
import io.beanmapper.testmodel.parent.Player;
import io.beanmapper.testmodel.parent.PlayerForm;
import io.beanmapper.testmodel.parent.SkillForm;
import io.beanmapper.testmodel.parentclass.Project;
import io.beanmapper.testmodel.parentclass.Source;
import io.beanmapper.testmodel.parentclass.Target;
import io.beanmapper.testmodel.person.Person;
import io.beanmapper.testmodel.person.PersonForm;
import io.beanmapper.testmodel.person.PersonResult;
import io.beanmapper.testmodel.person.PersonView;
import io.beanmapper.testmodel.project.CodeProject;
import io.beanmapper.testmodel.project.CodeProjectResult;
import io.beanmapper.testmodel.public_fields.SourceWithPublicFields;
import io.beanmapper.testmodel.public_fields.TargetWithPublicFields;
import io.beanmapper.testmodel.rule.NestedWithRule;
import io.beanmapper.testmodel.rule.SourceWithRule;
import io.beanmapper.testmodel.rule.TargetWithRule;
import io.beanmapper.testmodel.same_source_diff_results.Entity;
import io.beanmapper.testmodel.same_source_diff_results.ResultOne;
import io.beanmapper.testmodel.same_source_diff_results.ResultTwo;
import io.beanmapper.testmodel.similar_subclasses.DifferentSource;
import io.beanmapper.testmodel.similar_subclasses.DifferentTarget;
import io.beanmapper.testmodel.similar_subclasses.SimilarSubclass;
import io.beanmapper.testmodel.strict.SourceAStrict;
import io.beanmapper.testmodel.strict.SourceBNonStrict;
import io.beanmapper.testmodel.strict.SourceCStrict;
import io.beanmapper.testmodel.strict.SourceDStrict;
import io.beanmapper.testmodel.strict.SourceEForm;
import io.beanmapper.testmodel.strict.SourceF;
import io.beanmapper.testmodel.strict.TargetANonStrict;
import io.beanmapper.testmodel.strict.TargetBStrict;
import io.beanmapper.testmodel.strict.TargetCNonStrict;
import io.beanmapper.testmodel.strict.TargetDNonStrict;
import io.beanmapper.testmodel.strict.TargetE;
import io.beanmapper.testmodel.strict.TargetFResult;
import io.beanmapper.testmodel.strict_convention.SCSourceAForm;
import io.beanmapper.testmodel.strict_convention.SCSourceB;
import io.beanmapper.testmodel.strict_convention.SCSourceCForm;
import io.beanmapper.testmodel.strict_convention.SCTargetA;
import io.beanmapper.testmodel.strict_convention.SCTargetBResult;
import io.beanmapper.testmodel.strict_convention.SCTargetC;
import io.beanmapper.testmodel.tostring.SourceWithNonString;
import io.beanmapper.testmodel.tostring.TargetWithString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class BeanMapperTest {

    private BeanMapper beanMapper;

    @BeforeEach
    void prepareBeanMapper() {
        beanMapper = new BeanMapperBuilder()
                .addPackagePrefix(BeanMapper.class)
                .addConverter(new LocalDateTimeToLocalDate())
                .addConverter(new LocalDateToLocalDateTime())
                .addConverter(new ObjectToStringConverter())
                .addConverter(new NestedSourceClassToNestedTargetClassConverter())
                .build();
    }

    @Test
    void mapEnum() throws BeanMappingException {
        ColorEntity colorEntity = new ColorEntity();
        colorEntity.setCurrentColor(RGB.BLUE);
        ColorResult colorResult = beanMapper.map(colorEntity, ColorResult.class);
        assertEquals(RGB.BLUE, colorResult.currentColor);
    }

    @Test
    void mapEnumWithOveriddenToString() {
        WeekEntity weekEntity = new WeekEntity();
        weekEntity.setCurrentDay(Day.FRIDAY);
        WeekResult weekResult = beanMapper.map(weekEntity, WeekResult.class);
        assertEquals(Day.FRIDAY, weekResult.currentDay);
    }

    @Test
    void mapEnumWithExistingTarget() throws BeanMappingException {
        ColorEntity source = new ColorEntity();
        source.setCurrentColor(RGB.BLUE);

        ColorResult target = new ColorResult();
        target.currentColor = RGB.GREEN;

        beanMapper.map(source, target);
        assertEquals(RGB.BLUE, target.currentColor);
    }

    @Test
    void mapEnumWithExistingTargetAndOverriddenToString() {
        WeekEntity weekEntity = new WeekEntity();
        weekEntity.setCurrentDay(Day.FRIDAY);

        WeekResult weekResult = new WeekResult();
        weekResult.currentDay = Day.THURSDAY;

        beanMapper.map(weekEntity, weekResult);
        assertEquals(Day.FRIDAY, weekResult.currentDay);
    }

    @Test
    void mapEnumToString() throws BeanMappingException {
        ColorEntity colorEntity = new ColorEntity();
        colorEntity.setCurrentColor(RGB.GREEN);
        ColorStringResult colorStringResult = beanMapper.map(colorEntity, ColorStringResult.class);
        assertEquals("GREEN", colorStringResult.currentColor);
    }

    @Test
    void mapDayEnumToString() {
        WeekEntity weekEntity = new WeekEntity();
        weekEntity.setCurrentDay(Day.FRIDAY);
        WeekStringResult weekStringResult = beanMapper.map(weekEntity, WeekStringResult.class);
        assertEquals("Friday", weekStringResult.currentDay);
    }

    @Test
    void copyToNewTargetInstance() {
        Person person = createPerson();
        PersonView personView = beanMapper.map(person, PersonView.class);
        assertEquals("Henk", personView.name);
        assertEquals("Zoetermeer", personView.place);
    }

    @Test
    void mapMapCollectionInContainer() {
        CollectionMapSource source = new CollectionMapSource() {{
            items.put("Jan", createPerson("Jan"));
            items.put("Piet", createPerson("Piet"));
            items.put("Joris", createPerson("Joris"));
            items.put("Korneel", createPerson("Korneel"));
        }};

        CollectionMapTarget target = beanMapper.map(source, CollectionMapTarget.class);
        assertEquals("Jan", target.items.get("Jan").name);
        assertEquals("Piet", target.items.get("Piet").name);
        assertEquals("Joris", target.items.get("Joris").name);
        assertEquals("Korneel", target.items.get("Korneel").name);
    }

    @Test
    void mapListCollectionInContainer() {
        CollectionListSource source = new CollectionListSource() {{
            items = new ArrayList<Person>();
            items.add(createPerson("Jan"));
            items.add(createPerson("Piet"));
            items.add(createPerson("Joris"));
            items.add(createPerson("Korneel"));
        }};

        CollectionListTarget target = beanMapper.map(source, CollectionListTarget.class);
        assertEquals("Jan", target.items.get(0).name);
        assertEquals("Piet", target.items.get(1).name);
        assertEquals("Joris", target.items.get(2).name);
        assertEquals("Korneel", target.items.get(3).name);
    }

    @Test
    void mapListCollectionWithNested() {
        List<Address> sourceItems = new ArrayList<Address>() {{
            add(new Address("Koraalrood", 1, "Nederland"));
            add(new Address("ComputerStraat", 1, "Duitsland"));
            add(new Address("InternetWeg", 1, "Belgie"));
            add(new Address("MuisLaan", 1, "Frankrijk"));
        }};
        List<ResultAddress> targetItems =
                beanMapper.map(sourceItems, ResultAddress.class);
        assertEquals("Koraalrood", targetItems.get(0).getStreet());
        assertEquals("ComputerStraat", targetItems.get(1).getStreet());
        assertEquals("InternetWeg", targetItems.get(2).getStreet());
        assertEquals("MuisLaan", targetItems.get(3).getStreet());

        assertEquals("Nederland", targetItems.get(0).getCountry().getCountryName());
        assertEquals("Duitsland", targetItems.get(1).getCountry().getCountryName());
        assertEquals("Belgie", targetItems.get(2).getCountry().getCountryName());
        assertEquals("Frankrijk", targetItems.get(3).getCountry().getCountryName());
    }

    @Test
    void mapListCollectionInContainerAndClearTheContainer() {
        CollectionListSource source = new CollectionListSource() {{
            items.add(createPerson("Jan"));
            items.add(createPerson("Piet"));
            items.add(createPerson("Joris"));
            items.add(createPerson("Korneel"));
        }};
        CollectionListTargetClear target = new CollectionListTargetClear();
        List expectedTargetList = target.items;
        target.items.add(new PersonView()); // This entry must be cleared

        target = beanMapper.map(source, target);
        assertEquals(expectedTargetList, target.items, "The target ArrayList must have been cleared, not re-constructed");
        assertEquals(4, target.items.size(), "The number of entries must be 4, not 5, because the list has been cleared");
        assertEquals("Jan", target.items.get(0).name);
        assertEquals("Piet", target.items.get(1).name);
        assertEquals("Joris", target.items.get(2).name);
        assertEquals("Korneel", target.items.get(3).name);
    }

    @Test
    void mapSetCollectionInContainer() {
        CollectionSetSource source = new CollectionSetSource();
        source.items = new TreeSet<>() {{
            add("13");
            add("29");
            add("43");
        }};
        CollectionSetTarget target = beanMapper.map(source, CollectionSetTarget.class);
        assertEquals(TreeSet.class, target.items.getClass());
        assertTrue(target.items.contains(13L), "Must contain 13");
        assertTrue(target.items.contains(29L), "Must contain 29");
        assertTrue(target.items.contains(43L), "Must contain 43");
    }

    @Test
    void mapSetCollectionsToIncorrectSubTypeIgnoreCollectionType() {
        CollectionSetSource source = new CollectionSetSource();
        source.items = new TreeSet<>() {{
            add("13");
            add("29");
            add("43");
        }};

        CollectionSetTargetIncorrectSubtype target =
                beanMapper.map(source, CollectionSetTargetIncorrectSubtype.class);
        assertTrue(target.items.contains(13L));
        assertTrue(target.items.contains(29L));
        assertTrue(target.items.contains(43L));
    }

    @Test
    void mapSetCollectionsToSpecificSubType() {
        CollectionSetSource source = new CollectionSetSource();
        source.items = new TreeSet<>();
        source.items.add("13");
        source.items.add("29");
        source.items.add("43");

        CollectionSetTargetSpecificSubtype target = beanMapper.map(source, CollectionSetTargetSpecificSubtype.class);
        assertEquals(HashSet.class, target.items.getClass());
        assertTrue(target.items.contains(13L), "Must contain 13");
        assertTrue(target.items.contains(29L), "Must contain 29");
        assertTrue(target.items.contains(43L), "Must contain 43");
    }

    @Test
    void mapArrayList() {
        List<Person> sourceItems = new ArrayList<>() {{
            add(createPerson("Jan"));
            add(createPerson("Piet"));
            add(createPerson("Joris"));
            add(createPerson("Korneel"));
        }};
        List<PersonView> targetItems = beanMapper.map(sourceItems, PersonView.class);
        assertEquals("Jan", targetItems.get(0).name);
        assertEquals("Piet", targetItems.get(1).name);
        assertEquals("Joris", targetItems.get(2).name);
        assertEquals("Korneel", targetItems.get(3).name);
    }

    @Test
    void mapTreeMap() {
        Map<String, Person> sourceItems = new TreeMap<>() {{
            put("jan", createPerson("Jan"));
            put("piet", createPerson("Piet"));
            put("joris", createPerson("Joris"));
            put("korneel", createPerson("Korneel"));
        }};
        Map<String, PersonView> targetItems = beanMapper.map(sourceItems, PersonView.class);
        assertEquals("Jan", targetItems.get("jan").name);
        assertEquals("Piet", targetItems.get("piet").name);
        assertEquals("Joris", targetItems.get("joris").name);
        assertEquals("Korneel", targetItems.get("korneel").name);
    }

    @Test
    void direcltyMapSet_comparable() {
        Set<Long> source = new TreeSet<>() {{
            add(13L);
            add(29L);
            add(43L);
        }};
        Set<String> target = beanMapper.map(source, String.class);
        assertEquals(TreeSet.class, target.getClass());
        assertEquals(3, target.size());
        assertTrue(target.contains("13"));
        assertTrue(target.contains("29"));
        assertTrue(target.contains("43"));
    }

    @Test
    void direcltyMapSet_nonComparable() {
        Set<Person> source = new HashSet<>() {{
            add(new Person() {{
                this.setName("Henk");
            }});
        }};
        Set<PersonResult> target = beanMapper.map(source, PersonResult.class);
        assertEquals(HashSet.class, target.getClass());
        assertEquals(1, target.size());
        assertEquals("Henk", target.iterator().next().name);
    }

    @Test
    void mapToCollectionArraysAsList() {
        List<RGB> collection = Arrays.asList(RGB.values());
        List<String> result = beanMapper.map(collection, String.class);
        assertEquals(RGB.values().length, result.size());
    }

    @Test
    void mapToNestedCollectionArraysAsList() {
        EnumSourceArraysAsList source = new EnumSourceArraysAsList();
        EnumTargetList target = beanMapper.map(source, EnumTargetList.class);
        assertEquals(RGB.values().length, target.items.size());
        assertEquals("RED", target.items.get(0));
    }

    @Test
    void mapDayToNestedCollectionArraysAsList() {
        DayEnumSourceArraysAsList source = new DayEnumSourceArraysAsList();
        EnumTargetList target = beanMapper.map(source, EnumTargetList.class);
        assertEquals(Day.values().length, target.items.size());
        for (var i = 0; i < target.items.size(); i++) {
            assertEquals(Day.values()[i].toString(), target.items.get(i));
        }
    }

    @Test
    void mapToCollectionNewArrayList() {
        List<RGB> collection = new ArrayList<>(Arrays.asList(RGB.values()));
        List<String> result = beanMapper.map(collection, String.class);
        assertEquals(RGB.values().length, result.size());
    }

    @Test
    void mapNonAnnotatedList() {
        CollSourceListNotAnnotated source = new CollSourceListNotAnnotated() {{
            list.add(42L);
            list.add(33L);
        }};
        CollTargetListNotAnnotated target = beanMapper.map(source, CollTargetListNotAnnotated.class);
        assertNotSame(source.list, target.list, "Source and Target list may not be the same, must be copied");
        assertEquals(2, target.list.size());
        assertEquals("42", target.list.get(0));
        assertEquals("33", target.list.get(1));
    }

    @Test
    void mapNonAnnotatedListUseSetter() {
        CollSourceListNotAnnotated source = new CollSourceListNotAnnotated() {{
            list.add(42L);
            list.add(33L);
        }};
        CollTargetListNotAnnotatedUseSetter target = beanMapper.map(source, CollTargetListNotAnnotatedUseSetter.class);
        assertNotSame(source.list, target.getList(), "Source and Target list may not be the same, must be copied");
        assertEquals(2, target.getList().size());
        assertEquals("42", target.getList().get(0));
        assertEquals("33", target.getList().get(1));
    }

    @Test
    void mapIncompletelyAnnotatedList() {
        CollSourceListIncompleteAnnotation source = new CollSourceListIncompleteAnnotation() {{
            list.add(42L);
            list.add(33L);
        }};
        CollTargetListNotAnnotated target = beanMapper.map(source, CollTargetListNotAnnotated.class);
        assertNotSame(source.list, target.list, "Source and Target list may not be the same, must be copied");
        assertEquals(2, target.list.size());
        assertEquals("42", target.list.get(0));
        assertEquals("33", target.list.get(1));
    }

    @Test
    void mapNonAnnotatedMap() {
        CollSourceMapNotAnnotated source = new CollSourceMapNotAnnotated() {{
            map.put("a", 42);
            map.put("b", 33);
        }};
        CollTargetMapNotAnnotated target = beanMapper.map(source, CollTargetMapNotAnnotated.class);
        assertNotSame(source.map, target.map, "Source and Target list may not be the same, must be copied");
        assertEquals(2, target.map.size());
        assertEquals((Long) 42L, target.map.get("a"));
        assertEquals((Long) 33L, target.map.get("b"));
    }

    @Test
    void mapNonAnnotatedListInSuperClass() {
        CollSourceListNotAnnotated source = new CollSourceListNotAnnotated() {{
            list.add(42L);
            list.add(33L);
        }};
        CollSubTargetList target = beanMapper.map(source, CollSubTargetList.class);
        assertNotSame(source.list, target.list, "Source and Target list may not be the same, must be copied");
        assertEquals(2, target.list.size());
        assertEquals("42", target.list.get(0));
        assertEquals("33", target.list.get(1));
    }

    @Test
    void mapListToCollectionEmptySet() {
        CollSourceListNotAnnotated source = new CollSourceListNotAnnotated() {{
            list.add(42L);
            list.add(33L);
        }};
        CollTargetEmptyList target = beanMapper.map(source, CollTargetEmptyList.class);
        assertNotSame(source.list, target.list, "Source and Target list may not be the same, must be copied");
        assertEquals(2, target.list.size());
        assertEquals("42", target.list.get(0));
        assertEquals("33", target.list.get(1));
    }

    @Test
    void mapNonAnnotatedListWithoutGenerics() {
        CollSourceNoGenerics source = new CollSourceNoGenerics() {{
            list.add("42");
            list.add("33");
        }};
        CollTargetNoGenerics target = beanMapper.map(source, CollTargetNoGenerics.class);
        assertSame(source.list, target.list, "Source and Target list may not be the same, must be copied");
        assertEquals(2, target.list.size());
        assertEquals("42", target.list.get(0));
        assertEquals("33", target.list.get(1));
    }

    @Test
    void emptySource() {
        // Test of correctly mapping
        EmptySource source = new EmptySource() {{
            id = 42;
            name = "sourceName";
            emptyName = "notEmpty";
            bool = true;
        }};
        EmptyTarget target = beanMapper.map(source, EmptyTarget.class);
        assertEquals(source.id, target.id, 0);
        assertEquals(source.name, target.name);
        assertEquals(source.emptyName, target.nestedEmptyClass.name);
        assertEquals(source.bool, target.bool);
        // Test with empty source
        EmptySource emptySource = new EmptySource();
        EmptyTarget emptyTarget = beanMapper.map(emptySource, EmptyTarget.class);
        assertEquals(0, emptyTarget.id, 0); // Default for primitive int is 0
        assertNull(emptyTarget.name);
        assertNull(emptyTarget.nestedEmptyClass);
        assertNull(emptyTarget.nestedEmpty);
        assertFalse(emptyTarget.bool); // Default for primitive boolean is false
    }

    @Test
    void emptySourceToExistingTarget() {
        EmptySource source = new EmptySource();

        EmptyTarget existingTarget = new EmptyTarget() {{
            id = 42;
            name = "ExistingTargetName";
            bool = true;
            nestedEmptyClass = new NestedEmptyTarget();
            nestedEmptyClass.name = "Hallo";
            nestedEmpty = new NestedEmptyTarget();
            nestedEmpty.name = "existingNestedTarget";
        }};
        EmptyTarget mappedTarget = beanMapper.map(source, existingTarget);
        assertEquals(0, mappedTarget.id, 0);// Default for primitive int is 0
        assertNull(mappedTarget.name);
        assertFalse(mappedTarget.bool);// Default for primitive boolean is false
        assertNull(mappedTarget.nestedEmptyClass.name);
        assertNull(mappedTarget.nestedEmpty);
    }

    @Test
    void mapNull() {
        EmptySource source = null;
        EmptyTarget target = beanMapper.map(source, EmptyTarget.class);
        assertNull(target);
    }

    @Test
    void copyToExistingTargetInstance() {
        Person person = createPerson();
        PersonForm form = createPersonForm();
        person = beanMapper
                .wrap()
                .setApplyStrictMappingConvention(false)
                .build()
                .map(form, person);
        assertEquals(1984L, (long) person.getId());
        assertEquals("Truus", person.getName());
        assertEquals("XHT-8311-t33l-llac", person.getBankAccount());
        assertEquals("Den Haag", person.getPlace());
    }

    @Test
    void mapDouble() {
        SourceWithDouble source = new SourceWithDouble();
        source.number = 13.5;
        TargetWithDouble target = beanMapper.map(source, TargetWithDouble.class);
        assertEquals((Double) 13.5, target.number);
    }

    @Test
    void mapLongToInteger() {
        ClassWithLong source = new ClassWithLong();
        source.number = 42L;
        ClassWithInteger target = beanMapper.map(source, ClassWithInteger.class);
        assertEquals((Integer) 42, target.number);
    }

    @Test
    void mapIntegerToLong() {
        ClassWithInteger source = new ClassWithInteger();
        source.number = 42;
        ClassWithLong target = beanMapper.map(source, ClassWithLong.class);
        assertEquals((Long) 42L, target.number);
    }

    @Test
    void beanIgnore() {
        IgnoreSource ignoreSource = new IgnoreSource() {{
            setBothIgnore("bothIgnore");
            setSourceIgnore("sourceIgnore");
            setTargetIgnore("targetIgnore");
            setNoIgnore("noIgnore");
        }};
        IgnoreTarget ignoreTarget = beanMapper.map(ignoreSource, IgnoreTarget.class);
        assertNull(ignoreTarget.getBothIgnore(), "bothIgnore -> target should be empty");
        assertNull(ignoreTarget.getSourceIgnore(), "sourceIgnore -> target should be empty");
        assertNull(ignoreTarget.getTargetIgnore(), "targetIgnore -> target should be empty");
        assertEquals("noIgnore", ignoreTarget.getNoIgnore());
    }

    @Test
    void mappingToOtherNames() {
        SourceWithOtherName source = new SourceWithOtherName() {{
            setBothOtherName1("bothOtherName");
            setSourceOtherName1("sourceOtherName");
            setTargetOtherName("targetOtherName");
            setNoOtherName("noOtherName");
        }};
        TargetWithOtherName target = beanMapper.map(source, TargetWithOtherName.class);
        assertEquals("bothOtherName", target.getBothOtherName2());
        assertEquals("sourceOtherName", target.getSourceOtherName());
        assertEquals("targetOtherName", target.getTargetOtherName1());
        assertEquals("noOtherName", target.getNoOtherName());
    }

    @Test
    void nonStringToString() {
        SourceWithNonString obj = new SourceWithNonString();
        obj.setDate(LocalDate.of(2015, 4, 1));
        TargetWithString view = beanMapper.map(obj, TargetWithString.class);
        assertEquals("2015-04-01", view.getDate());
    }

    @Test
    void beanDefault() {
        SourceWithDefaults source = new SourceWithDefaults();
        source.setNoDefault("value1");
        source.setTargetDefaultWithValue("value2");

        TargetWithDefaults target = beanMapper.map(source, TargetWithDefaults.class);
        assertEquals("bothdefault2", target.getBothDefault());
        assertEquals("sourcedefault", target.getSourceDefault());
        assertEquals("targetdefault", target.getTargetDefault());
        assertEquals("value1", target.getNoDefault());
        assertEquals("targetdefaultwithoutmatch", target.getTargetDefaultWithoutMatch());
        assertEquals("value2", target.getTargetDefaultWithValue());
    }

    @Test
    void testParentClass() {
        Project project = new Project();
        project.setProjectName("projectName");
        project.setId(42L);
        Source entity = new Source() {{
            setProject(project);
            setId(1L);
            setName("abstractName");
            setStreet("street");
            setHouseNumber(42);
        }};
        Target target = beanMapper.map(entity, Target.class);
        assertEquals(1, target.getId(), 0);
        assertEquals("abstractName", target.getName());
        assertEquals("street", target.getStreet());
        assertEquals(42, target.getHouseNumber(), 0);
        assertEquals(42, target.getProjectId(), 0);
    }

    @Test
    void testParentClassReversed() {
        Target target = new Target() {{
            setProjectId(42L);
            setId(1L);
            setName("abstractName");
            setStreet("street");
            setHouseNumber(42);
        }};
        Source source = beanMapper.map(target, Source.class);
        assertEquals(1, source.getId(), 0);
        assertEquals("abstractName", source.getName());
        assertEquals("street", source.getStreet());
        assertEquals(42, source.getHouseNumber(), 0);
        assertEquals(42, source.getProject().getId(), 0);
    }

    @Test
    void encapsulateManyToMany() {
        House house = createHouse();

        ResultManyToMany result = beanMapper.map(house, ResultManyToMany.class);
        assertEquals("housename", result.getName());
        assertEquals("denneweg", result.getAddressOfTheHouse().getStreet());
        assertEquals(1, result.getAddressOfTheHouse().getNumber());
        assertEquals("Nederland", result.getAddressOfTheHouse().getCountry().getCountryName());
    }

    @Test
    void encapsulateManyToOne() {
        House house = createHouse();

        ResultManyToOne result = beanMapper.map(house, ResultManyToOne.class);
        assertEquals("housename", result.getName());
        assertEquals("denneweg", result.getStreet());
        assertEquals(1, result.getNumber());
        assertEquals("Nederland", result.getCountryName());
    }

    @Test
    void encapsulateManyToOneWithNull() {
        House house = createHouse();
        house.setAddress(null);

        ResultManyToOne result = beanMapper.map(house, ResultManyToOne.class);
        assertEquals("housename", result.getName());
        assertNull(result.getStreet());
        assertEquals(0, result.getNumber());
        assertNull(result.getCountryName());
    }

    @Test
    void encapsulateOneToMany() {
        Country country = new Country("Nederland");

        ResultOneToMany result = beanMapper.map(country, ResultOneToMany.class);
        assertEquals("Nederland", result.getResultCountry().getCountryName());
    }

    @Test
    void sourceAnnotated() {
        // One to Many & Many to One
        Driver driver = new Driver("driverName");
        Car car = new Car("Opel", 4);
        driver.setCar(car);
        driver.setMonteurName("monteur");

        CarDriver target = beanMapper.map(driver, CarDriver.class);
        assertEquals("driverName", target.getName());
        assertEquals("Opel", target.getBrand());
        assertEquals(4, target.getWheels());
        assertEquals("monteur", target.getMonteur().getName());
    }

    @Test
    void initiallyUnmatchedSourceMustBeUsed() {
        SourceWithUnmatchedField swuf = new SourceWithUnmatchedField();
        swuf.setName("Henk");
        swuf.setCountry("NL");
        TargetWithoutUnmatchedField twuf = beanMapper.map(swuf, new TargetWithoutUnmatchedField());
        assertEquals("Henk", twuf.getName());
        assertEquals("NL", twuf.getNation());
    }

    @Test
    void nestedClasses() {
        Layer1 layer1 = Layer1.createNestedClassObject();
        Layer1Result result = beanMapper.map(layer1, Layer1Result.class);
        assertEquals("layer1", result.getName1());
        assertEquals("layer2", result.getLayer2().getName2());
        assertEquals("name3", result.getLayer2().getLayer3().getName3());
        assertNull(result.getLayer2().getLayer3().getId4());
    }

    @Test
    void multipleUnwrap() {
        LayerA source = LayerA.create();
        AllTogether target = beanMapper.map(source, AllTogether.class);
        assertEquals("name1", target.getName1());
        assertEquals("name2", target.getName2());
        assertEquals("name3", target.getName3());
    }

    @Test
    void multipleUnwrapReversed() {
        AllTogether source = new AllTogether();
        source.setName1("name1");
        source.setName2("name2");
        source.setName3("name3");

        LayerA target = beanMapper.map(source, LayerA.class);
        assertEquals("name1", target.getName1());
        assertEquals("name2", target.getLayerB().getName2());
        assertEquals("name3", target.getLayerB().getLayerC().getName3());
    }

    @Test
    void publicFields() {
        SourceWithPublicFields source = new SourceWithPublicFields() {{
            name = "Henk";
            id = 42L;
            date = LocalDate.of(2015, 5, 4);
        }};
        TargetWithPublicFields target = beanMapper.map(source, TargetWithPublicFields.class);
        assertEquals("Henk", target.name);
        assertEquals(42L, (long) target.id);
        assertEquals(LocalDate.of(2015, 5, 4), target.date);
    }

    @Test
    void similarSubclasses() {
        SimilarSubclass subclass = new SimilarSubclass();
        subclass.name = "Henk";
        DifferentSource source = new DifferentSource();
        source.subclass = subclass;
        DifferentTarget target = beanMapper.map(source, DifferentTarget.class);
        assertEquals(source.subclass, target.subclass);
    }

    @Test
    void converterDateToDateTime() {
        SourceWithDate source = new SourceWithDate();
        source.setDiffType(LocalDate.of(2015, 1, 1));
        source.setSameType(LocalDate.of(2000, 1, 1));

        TargetWithDateTime target = beanMapper.map(source, TargetWithDateTime.class);
        assertEquals(target.getDiffType(), LocalDateTime.of(2015, 1, 1, 0, 0));
        assertEquals(target.getSameType(), LocalDate.of(2000, 1, 1));
    }

    @Test
    void converterDateTimeToDate() {
        TargetWithDateTime source = new TargetWithDateTime();
        source.setDiffType(LocalDateTime.of(2015, 1, 1, 0, 0));
        source.setSameType(LocalDate.of(2000, 1, 1));

        SourceWithDate target = beanMapper.map(source, SourceWithDate.class);
        assertEquals(target.getDiffType(), LocalDate.of(2015, 1, 1));
        assertEquals(target.getSameType(), LocalDate.of(2000, 1, 1));
    }

    @Test
    void converterBetweenClasses() {
        SourceWithNestedClass source = new SourceWithNestedClass();
        source.number = 42;
        NestedSourceClass nestedSourceClass = new NestedSourceClass();
        nestedSourceClass.name = "42BV";
        nestedSourceClass.laptopNumber = "765GR";
        source.nestedClass = nestedSourceClass;

        TargetWithNestedClass target = beanMapper.map(source, TargetWithNestedClass.class);
        assertEquals(source.number, target.number, 0);
        assertEquals(source.nestedClass.name, target.nestedClass.name);
        assertEquals("[" + source.nestedClass.laptopNumber + "]", ((NestedTargetClass) target.nestedClass).laptopNumber);
    }

    @Test
    void converterForClassesInCollection() {
        SourceWithCollection source = new SourceWithCollection();
        NestedSourceClass nestedSourceClass = new NestedSourceClass();
        nestedSourceClass.name = "42BV";
        nestedSourceClass.laptopNumber = "765GR";
        source.objects.add(nestedSourceClass);

        TargetWithCollection target = beanMapper.map(source, TargetWithCollection.class);
        assertEquals(source.objects.get(0).name, target.objects.get(0).name);
        assertEquals("[" + source.objects.get(0).laptopNumber + "]", target.objects.get(0).laptopNumber);
    }

    @Test
    void sameSourceTwoDiffResults() {
        Entity entity = new Entity() {{
            setId(1L);
            setName("name");
            setDescription("description");
        }};
        ResultOne resultOne = beanMapper.map(entity, ResultOne.class);
        ResultTwo resultTwo = beanMapper.map(entity, ResultTwo.class);

        assertEquals(1L, resultOne.getId(), 0);
        assertEquals("name", resultOne.getName());
        assertEquals(1L, resultTwo.getId(), 0);
        assertEquals("description", resultTwo.getDescription());
    }

    @Test
    void testIgnoreWhenNotWritable() {
        CodeProject masterProject = new CodeProject() {{
            id = 42L;
            name = "master";
        }};
        CodeProject project = new CodeProject() {{
            id = 24L;
            name = "project";
            master = masterProject;
        }};
        CodeProjectResult result = beanMapper.map(project, CodeProjectResult.class);

        assertEquals(Long.valueOf(24), result.id);
        assertEquals(Long.valueOf(42), result.masterId);

        assertNull(result.name); // Ignored because final field and no setter
        assertNull(result.getMasterName()); // Ignored because private field and no setter
        // Ignored isMaster() because no setter
    }

    @Test
    void convertGetterListToPublicFieldList() {
        SourceWithListGetter source = new SourceWithListGetter() {{
            lines.add("alpha");
            lines.add("beta");
            lines.add("gamma");
        }};
        TargetWithListPublicField target = beanMapper.map(source, TargetWithListPublicField.class);
        assertEquals(3, target.lines.size());
    }

    @Test
    void beanAliasTest() {
        // Nested class variables
        NestedSourceWithAlias nested = new NestedSourceWithAlias() {{
            x = "100";
            y = 200;
            property = "Property";
        }};
        SourceWithAlias source = new SourceWithAlias() {{
            id = 42;
            sourceName = "name";
            nestedWithAlias = nested;
        }};

        TargetWithAlias target = beanMapper.map(source, TargetWithAlias.class);
        assertEquals(source.id, target.aliasId, 0);
        assertEquals(source.sourceName, target.targetName);
        assertEquals(source.nestedWithAlias.x, target.nestedWithAlias.nestedString);
        assertEquals(source.nestedWithAlias.y, target.nestedWithAlias.nestedInt, 0);
        assertEquals(source.nestedWithAlias.property, target.nestedWithAlias.property);
    }

    @Test
    void beanConstructTest() {
        SourceWithConstruct source = new SourceWithConstruct() {{
            id = 238L;
            firstName = "Piet";
            infix = "van";
            lastName = "Straten";
        }};
        NestedSourceWithoutConstruct nestedClass = new NestedSourceWithoutConstruct() {{
            street = "boomweg";
            number = 42;
        }};
        source.nestedClass = nestedClass;

        TargetWithoutConstruct target = beanMapper.map(source, TargetWithoutConstruct.class);
        assertEquals(source.id, target.id, 0);
        assertEquals(source.firstName + source.infix + source.lastName, target.getFullName());
        assertEquals(source.nestedClass.street + source.nestedClass.number, target.nestedClass.streetWithNumber);
    }

    @Test
    void nestedConstructorAtTargetSideWithBeanUnwrap() {
        FlatConstructSource source = new FlatConstructSource() {{
            id = 2901L;
            street = "boomweg";
            city = "Zoetermeer";
            country = "Nederland";
        }};
        BigConstructTarget target = beanMapper.map(source, BigConstructTarget.class);
        assertEquals(source.id, target.id, 0);
        assertEquals(source.street, target.street);
        assertEquals(source.city, target.nestedClass.city);
        assertEquals(source.country, target.nestedClass.country);
        assertEquals(source.city + " " + source.country, target.nestedClass.getCityCountry());
    }

    @Test
    void nestedConstructorAtTargetSideWithBeanProperty() {
        FlatConstructSource2 source = new FlatConstructSource2() {{
            id = 2901L;
            street = "boomweg";
            city = "Zoetermeer";
            country = "Nederland";
        }};
        BigConstructTarget2 target = beanMapper.map(source, BigConstructTarget2.class);
        assertEquals(source.id, target.id, 0);
        assertEquals(source.street, target.street);
        assertEquals(source.city, target.nestedClass.city);
        assertEquals(source.country, target.nestedClass.country);
        assertEquals(source.city + " " + source.country, target.nestedClass.getCityCountry());
    }

    @Test
    void sourceToTargetWithRule() {
        SourceWithRule source = createSourceWithRule();
        TargetWithRule target = createTargetWithRule();
        beanMapper.map(source, target);
        assertEquals(source.getId(), target.getId());
        assertEquals(source.getName(), target.getName());
        assertEquals(target.getNested().nestedInt, target.getNested().nestedInt);
        assertEquals(source.getNested().nestedName, target.getNested().nestedName);
    }

    @Test
    void sourceToTargetWithRuleOnlyNames() {
        // Second only names mapping
        SourceWithRule source = createSourceWithRule();
        TargetWithRule target = createTargetWithRule();

        beanMapper.wrap()
                .downsizeSource(Arrays.asList("name", "nested", "nested.nestedName"))
                .build()
                .map(source, target);
        assertEquals(1, target.getId(), 0); // Not mapped
        assertEquals(source.getName(), target.getName()); // Overwritten
        assertEquals(2, target.getNested().nestedInt, 0); // Not mapped
        assertEquals(source.getNested().nestedName, target.getNested().nestedName); // Overwritten
    }

    private TargetWithRule createTargetWithRule() {
        return new TargetWithRule() {{
            setId(1);
            setName("targetName");
            setNested(new NestedWithRule());
            getNested().nestedInt = 2;
            getNested().nestedName = "targetNestedName";
        }};
    }

    private SourceWithRule createSourceWithRule() {
        return new SourceWithRule() {{
            setId(null);
            setName("Name");
            setNested(new NestedWithRule());
            getNested().nestedInt = null;
            getNested().nestedName = "NestedName";
        }};
    }

    @Test
    void mapWithInnerClass() {
        SourceWithInnerClass source = new SourceWithInnerClass(1L, "42BV");
        source.innerClass = new SourceWithInnerClass.SourceInnerClass("IT Company");

        TargetWithInnerClass target = beanMapper.map(source, TargetWithInnerClass.class);
        assertEquals(1L, target.id, 0);
        assertEquals("42BV", target.name);
        assertEquals("IT Company", target.innerClass.description);
    }

    @Test
    void overrideConverterTest() {
        var exception = assertThrows(BeanConversionException.class, () -> {
            BeanMapper beanMapper = new BeanMapperBuilder()
                    .addPackagePrefix(BeanMapper.class)
                    .build();

            SourceWithDate source = new SourceWithDate();
            source.setDiffType(LocalDate.of(2015, 1, 1));
            source.setSameType(LocalDate.of(2000, 1, 1));

            TargetWithDateTime target = beanMapper.wrap()
                    .addConverter(new LocalDateToLocalDateTime())
                    .build()
                    .map(source, TargetWithDateTime.class);

            assertEquals(target.getDiffType(), LocalDateTime.of(2015, 1, 1, 0, 0));
            assertEquals(target.getSameType(), LocalDate.of(2000, 1, 1));

            beanMapper.map(source, TargetWithDateTime.class);
        });
        assertEquals("Could not convert LocalDate to LocalDateTime.", exception.getMessage());
    }

    @Test
    void beanParentDirectDescendantAnnotationOnSource() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .addPackagePrefix(BeanMapper.class)
                .build();

        PlayerForm form = new PlayerForm();
        form.name = "w00t";
        form.skill = new SkillForm();
        form.skill.name = "Athletics";

        Player player = beanMapper.map(form, Player.class);

        assertEquals(form.name, player.getSkill().getPlayer1().getName(), "@BeanParent on source side was not triggered");
        assertEquals(form.name, player.getSkill().getPlayer2().getName(), "@BeanParent on target side was not triggered");
    }

    @Test
    void beanParentThroughCollection() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .addPackagePrefix(BeanMapper.class)
                .build();

        PlayerForm form = new PlayerForm();
        form.name = "w00t";
        form.skills = new ArrayList<SkillForm>();
        form.skills.add(new SkillForm());
        form.skills.get(0).name = "Athletics";
        form.skills.add(new SkillForm());
        form.skills.get(1).name = "Football";
        form.skills.add(new SkillForm());
        form.skills.get(2).name = "CLimbing";

        Player player = beanMapper.map(form, Player.class);

        assertEquals(form.name, player.getSkills().get(0).getPlayer1().getName());
        assertEquals(form.name, player.getSkills().get(1).getPlayer1().getName());
        assertEquals(form.name, player.getSkills().get(2).getPlayer1().getName());
    }

    @Test
    void numberToNumberInList() {
        BeanMapper beanMapper = new BeanMapperBuilder().build();

        List<String> numberStrings = new ArrayList<>();
        numberStrings.add("1");

        List<Integer> numbers = beanMapper.map(numberStrings, Integer.class);
        assertEquals((Integer) 1, numbers.get(0));
    }

    @Test
    void referenceCopy() {
        Layer3 source = new Layer3();
        Layer4 nestedInSource = new Layer4();
        nestedInSource.setId(42L);
        source.setLayer4(nestedInSource);

        BeanMapper beanMapper = new BeanMapperBuilder().build();

        Layer3 target = beanMapper.map(source, Layer3.class);
        assertEquals(source.getLayer4(), target.getLayer4());
    }

    @Test
    void strictSource() {
        assertThrows(BeanStrictMappingRequirementsException.class, () ->
                new BeanMapperBuilder()
                        .addBeanPairWithStrictSource(SourceAStrict.class, TargetANonStrict.class)
                        .build()
        );
    }

    @Test
    void strictTarget() {
        assertThrows(BeanStrictMappingRequirementsException.class, () ->
                new BeanMapperBuilder()
                        .addBeanPairWithStrictTarget(SourceBNonStrict.class, TargetBStrict.class)
                        .build()
        );
    }

    @Test
    void strictSourceUseAlias() {
        assertThrows(BeanStrictMappingRequirementsException.class, () ->
                new BeanMapperBuilder()
                        .addBeanPairWithStrictSource(SourceCStrict.class, TargetCNonStrict.class)
                        .build()
        );
    }

    @Test
    void strictSourceAllIsFine() {
        new BeanMapperBuilder()
                .addBeanPairWithStrictSource(SourceDStrict.class, TargetDNonStrict.class)
                .build();
    }

    @Test
    void strictMultipleBeanMismatches() {
        try {
            new BeanMapperBuilder()
                    .addBeanPairWithStrictSource(SourceAStrict.class, TargetANonStrict.class)
                    .addBeanPairWithStrictTarget(SourceBNonStrict.class, TargetBStrict.class)
                    .addBeanPairWithStrictSource(SourceCStrict.class, TargetCNonStrict.class)
                    .addBeanPairWithStrictSource(SourceDStrict.class, TargetDNonStrict.class)
                    .build();
            fail("Should have thrown an exception");
        } catch (BeanStrictMappingRequirementsException ex) {
            assertEquals(SourceAStrict.class, ex.getValidationMessages().get(0).getSourceClass());
            assertEquals("noMatch", ex.getValidationMessages().get(0).getFields().get(0).getName());
            assertEquals(TargetBStrict.class, ex.getValidationMessages().get(1).getTargetClass());
            assertEquals("noMatch", ex.getValidationMessages().get(1).getFields().get(0).getName());
            assertEquals(SourceCStrict.class, ex.getValidationMessages().get(2).getSourceClass());
            assertEquals("noMatch1", ex.getValidationMessages().get(2).getFields().get(0).getName());
            assertEquals("noMatch2", ex.getValidationMessages().get(2).getFields().get(1).getName());
            assertEquals("noMatch3", ex.getValidationMessages().get(2).getFields().get(2).getName());
        }
    }

    @Test
    void strictMappingConventionForForm() {
        assertThrows(BeanStrictMappingRequirementsException.class, () -> beanMapper.map(new SourceEForm(), TargetE.class));
    }

    @Test
    void strictMappingConventionForResult() {
        assertThrows(BeanStrictMappingRequirementsException.class, () -> beanMapper.map(new SourceF(), TargetFResult.class));
    }

    @Test
    void strictMappingConventionMissingMatchForGetter() {
        assertThrows(BeanStrictMappingRequirementsException.class, () -> beanMapper.map(new SCSourceCForm(), SCTargetC.class));
    }

    @Test
    void strictMappingConventionWithPrivateFieldOnForm() {
        SCSourceAForm source = new SCSourceAForm();
        source.name = "Alpha";
        source.setDoesNotExist("some value");
        SCTargetA target = beanMapper.map(source, SCTargetA.class);
        assertEquals("Alpha", target.name);
        assertEquals("some value", target.doesExist);
    }

    @Test
    void strictMappingConventionWithPrivateFieldOnResult() {
        SCSourceB source = new SCSourceB();
        source.name = "Alpha";
        source.doesExist = "some value";
        SCTargetBResult target = beanMapper.map(source, SCTargetBResult.class);
        assertEquals("Alpha", target.name);
        assertEquals("some value", target.getDoesNotExist());
    }

    @Test
    void beanCollectionClearEmptySource() {
        CollSourceClear source = new CollSourceClear();
        CollTarget target = new CollTarget();
        List<String> targetItems = target.items;
        targetItems.add("Alpha");
        source.items = null;
        target = beanMapper.map(source, target);
        assertEquals(targetItems, target.items);
        assertEquals(0, target.items.size());
    }

    @Test
    void beanCollectionReuseEmptySource() {
        CollSourceReuse source = new CollSourceReuse();
        CollTarget target = new CollTarget();
        List<String> targetItems = target.items;
        targetItems.add("Alpha");
        source.items = null;
        target = beanMapper.map(source, target);
        assertEquals(targetItems, target.items);
        assertEquals(1, target.items.size());
    }

    @Test
    void beanCollectionConstructEmptySource() {
        CollSourceConstruct source = new CollSourceConstruct();
        CollTarget target = new CollTarget();
        List<String> targetItems = target.items;
        targetItems.add("Alpha");
        source.items = null;
        target = beanMapper.map(source, target);
        assertNotSame(targetItems, target.items);
        assertEquals(0, target.items.size());
    }

    @Test
    void beanCollectionClear() {
        CollSourceClear source = new CollSourceClear();
        source.items.add("A");
        source.items.add("B");
        source.items.add("C");
        CollTarget target = new CollTarget();
        target.items = new ArrayList<>();
        List<String> expectedList = target.items;
        target.items.add("D");
        target = beanMapper.map(source, target);
        assertEquals(expectedList, target.items);
        assertEquals(3, target.items.size());
    }

    @Test
    void beanCollectionClearCallsAfterClearFlusher() throws Exception {
        AfterClearFlusher afterClearFlusher = createAfterClearFlusher();
        BeanMapper beanMapper = new BeanMapperBuilder()
                .addAfterClearFlusher(afterClearFlusher)
                .build().wrap()
                .setFlushEnabled(true)
                .setFlushAfterClear(FlushAfterClearInstruction.FLUSH_ENABLED)
                .build();
        CollSourceClearFlush source = new CollSourceClearFlush() {{
            items.add("A");
        }};
        CollTarget target = new CollTarget() {{
            items = new ArrayList<>();
            items.add("B");
        }};
        beanMapper.map(source, target);
        assertTrue(afterClearFlusher.getClass().getField("trigger").getBoolean(afterClearFlusher),
                "Should have called the afterClearFlusher instance");
    }

    @Test
    void beanCollectionClearDoesNotCallFlusher() throws Exception {
        AfterClearFlusher afterClearFlusher = createAfterClearFlusher();
        BeanMapper beanMapper = new BeanMapperBuilder()
                .addAfterClearFlusher(afterClearFlusher)
                .build();
        CollSourceClear source = new CollSourceClear() {{
            items.add("A");
        }};
        CollTarget target = new CollTarget() {{
            items = new ArrayList<>();
            items.add("B");
        }};
        beanMapper.map(source, target);
        assertFalse(afterClearFlusher.getClass().getField("trigger").getBoolean(afterClearFlusher),
                "Should NOT have called the afterClearFlusher instance");
        var wait = true;
    }

    private AfterClearFlusher createAfterClearFlusher() {
        return new AfterClearFlusher() {

            public boolean trigger = false;

            @Override
            public void flush() {
                trigger = true;
            }
        };
    }

    @Test
    void beanCollectionReuse() {
        CollSourceReuse source = new CollSourceReuse();
        source.items.add("A");
        source.items.add("B");
        source.items.add("C");
        CollTarget target = new CollTarget();
        target.items = new ArrayList<>();
        List<String> expectedList = target.items;
        target.items.add("D");
        target = beanMapper.map(source, target);
        assertEquals(4, target.items.size());
        assertEquals(expectedList, target.items);
        assertEquals("D", target.items.get(0));
    }

    @Test
    void beanCollectionConstruct() {
        CollSourceConstruct source = new CollSourceConstruct();
        source.items.add("A");
        source.items.add("B");
        source.items.add("C");
        CollTarget target = new CollTarget();
        target.items = new ArrayList<>();
        List<String> unexpectedList = target.items;
        target.items.add("D");
        target = beanMapper.map(source, target);
        assertEquals(3, target.items.size());
        assertNotSame(unexpectedList, target.items);
    }

    @Test
    void anonymousClass() {
        BookForm bookForm = new BookForm() {{
            name = "Henkie";
            street = "Stationsplein 31";
            city = "Brussel";
        }};
        Book book = beanMapper.map(bookForm, Book.class);
        assertEquals(bookForm.name, book.getName());
        assertEquals(bookForm.street, book.getStreet());
        assertEquals(bookForm.city, book.getCity());
    }

    @Test
    void beanMapper_shouldMapFieldsFromSuperclass() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .setApplyStrictMappingConvention(false)
                .build();
        UserRoleResult result = beanMapper.map(UserRole.ADMIN, UserRoleResult.class);
        assertEquals(UserRole.ADMIN.name(), result.name);
    }

    @Test
    void unmodifiableRandomAccessList() {
        List<Long> numbers = Collections.unmodifiableList(new ArrayList<>() {{
            add(42L);
            add(57L);
            add(33L);
        }});
        List<String> numbersAsText = beanMapper.map(numbers, String.class);
        assertEquals(3, numbersAsText.size());
        assertEquals("42", numbersAsText.get(0));
    }

    @Test
    void beanPropertyMismatch() {
        assertThrows(BeanNoSuchPropertyException.class, () -> {
            SourceBeanProperty source = new SourceBeanProperty() {{
                age = 42;
            }};
            beanMapper.map(source, TargetBeanProperty.class);
        });
    }

    @Test
    void beanPropertyNestedMismatch() {
        assertThrows(BeanNoSuchPropertyException.class, () -> {
            SourceNestedBeanProperty source = new SourceNestedBeanProperty() {{
                value1 = "42";
                value2 = "33";
            }};
            beanMapper.map(source, TargetNestedBeanProperty.class);
        });
    }

    @Test
    void wrap_mustAlwaysWrap() {
        assertNotSame(beanMapper.configuration(), beanMapper.wrap().build().configuration());
    }

    @Test
    void securedSourceFieldHasAccess() {
        assertSecuredSourceField(roles -> true, "Henk");
    }

    @Test
    void securedSourceFieldNoAccess() {
        assertSecuredSourceField(roles -> false, null);
    }

    @Test
    void securedTargetFieldHasAccess() {
        assertSecuredTargetField(roles -> true, "Henk");
    }

    @Test
    void securedTargetFieldNoAccess() {
        assertSecuredTargetField(roles -> false, null);
    }

    @Test
    void securedSourceMethodNoAccess() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .setSecuredPropertyHandler(roles -> false)
                .build();
        SFSourceCWithSecuredMethod source = new SFSourceCWithSecuredMethod() {{
            setName("Henk");
        }};
        SFTargetA target = beanMapper.map(source, SFTargetA.class);
        assertNull(target.name);
    }

    private void assertSecuredSourceField(RoleSecuredCheck roleSecuredCheck, String expectedName) {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .setSecuredPropertyHandler(roleSecuredCheck)
                .build();
        SFSourceAWithSecuredField source = new SFSourceAWithSecuredField() {{
            name = "Henk";
        }};
        SFTargetA target = beanMapper.map(source, SFTargetA.class);
        assertEquals(expectedName, target.name);
    }

    private void assertSecuredTargetField(RoleSecuredCheck roleSecuredCheck, String expectedName) {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .setSecuredPropertyHandler(roleSecuredCheck)
                .build();
        SFSourceB source = new SFSourceB() {{
            name = "Henk";
        }};
        SFTargetBWithSecuredField target = beanMapper.map(source, SFTargetBWithSecuredField.class);
        assertEquals(expectedName, target.name);
    }

    @Test
    void throwExceptionWhenSecuredPropertyDoesNotHaveAHandler() {
        assertThrows(BeanNoRoleSecuredCheckSetException.class, () -> {
            SFSourceAWithSecuredField source = new SFSourceAWithSecuredField() {{
                name = "Henk";
            }};
            beanMapper.map(source, SFTargetA.class);
        });
    }

    @Test
    void allowSecuredPropertyDoesNotHaveAHandler() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .setEnforcedSecuredProperties(false)
                .build();
        SFSourceAWithSecuredField source = new SFSourceAWithSecuredField() {{
            name = "Henk";
        }};
        SFTargetA target = beanMapper.map(source, SFTargetA.class);
        assertEquals("Henk", target.name);
    }

    @Test
    void logicSecuredCheckMustBlock() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .addLogicSecuredCheck(new NeverReturnTrueCheck())
                .build();
        SFSourceDLogicSecured source = new SFSourceDLogicSecured() {{
            name = "Henk";
        }};
        SFTargetA target = beanMapper.map(source, SFTargetA.class);
        assertNull(target.name);
    }

    @Test
    void logicSecuredCheckMustBlockBecauseInequalName() {
        logicCheckForEqualName("Blake", null);
    }

    @Test
    void logicSecuredCheckMustAllowBecauseEqualName() {
        logicCheckForEqualName("Henk", "Henk");
    }

    private void logicCheckForEqualName(String initialName, String expectedName) {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .addLogicSecuredCheck(new CheckSameNameLogicCheck())
                .build();
        SFSourceELogicSecured source = new SFSourceELogicSecured() {{
            name = initialName;
        }};
        SFTargetA target = beanMapper.map(source, SFTargetA.class);
        assertEquals(expectedName, target.name);
    }

    @Test
    void logicSecuredMissingCheck() {
        assertThrows(BeanNoLogicSecuredCheckSetException.class, () -> {
            SFSourceDLogicSecured source = new SFSourceDLogicSecured() {{
                name = "Henk";
            }};
            beanMapper.map(source, SFTargetA.class);
        });
    }

    @Test
    void unwrappedToWrapped() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .addConverter(new UnwrappedToWrappedBeanConverter())
                .addBeanPairWithStrictSource(SourceWithUnwrappedItems.class, TargetWithWrappedItems.class)
                .build();
        SourceWithUnwrappedItems source = new SourceWithUnwrappedItems();
        source.items.add(UnwrappedSource.BETA);
        source.items.add(UnwrappedSource.GAMMA);
        source.items.add(UnwrappedSource.ALPHA);
        TargetWithWrappedItems target = new TargetWithWrappedItems();
        List<WrappedTarget> targetItems = target.getItems();
        target = beanMapper.map(source, target);
        assertEquals(targetItems, target.getItems());
        assertEquals(source.items.get(0), target.getItems().get(0).getElement());
        assertEquals(source.items.get(1), target.getItems().get(1).getElement());
        assertEquals(source.items.get(2), target.getItems().get(2).getElement());
    }

    @Test
    void emptyListToExistingList() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .addConverter(new UnwrappedToWrappedBeanConverter())
                .build();
        SourceWithUnwrappedItems source = new SourceWithUnwrappedItems();
        source.items = null;
        TargetWithWrappedItems target = new TargetWithWrappedItems();
        List<WrappedTarget> targetItems = target.getItems();
        target = beanMapper.map(source, target);
        assertEquals(targetItems, target.getItems());
    }

    @Test
    void useBeanPropertyPathToAccessGetterOnly() {
        SourceWithPerson source = new SourceWithPerson();
        TargetWithPersonName target = beanMapper.map(source, TargetWithPersonName.class);
        assertEquals(source.person.getFullName(), target.name);
    }

    @Test
    void useBeanPropertyPathToAccessSetterOnly() {
        SourceWithPersonName source = new SourceWithPersonName();
        source.name = "Zeefod Beeblebrox";
        TargetWithPerson target = beanMapper.map(source, TargetWithPerson.class);
        assertEquals(source.name, target.person.result);
    }

    @Test
    void nestedGenericsEmptyList() {
        SourceWithNestedGenerics source = new SourceWithNestedGenerics();
        TargetWithNestedGenerics target = beanMapper.map(source, TargetWithNestedGenerics.class);
        assertNotNull(target.names);
    }

    @Test
    void deepMapEnum() {
        CountryEnum source = CountryEnum.NL;
        CountryResult target = beanMapper.map(source, CountryResult.class);
        assertEquals(CountryEnum.NL.getCode(), target.code);
        assertEquals(CountryEnum.NL.getDisplayName(), target.displayName);
        assertEquals(CountryEnum.NL.getImage(), target.image);
    }

    @Test
    void deepMapEnumInHolder() {
        CountryHolder sourceHolder = new CountryHolder();
        sourceHolder.country = CountryEnum.NL;
        CountryResultHolder target = beanMapper.map(sourceHolder, CountryResultHolder.class);
        assertEquals(CountryEnum.NL.getCode(), target.country.code);
        assertEquals(CountryEnum.NL.getDisplayName(), target.country.displayName);
        assertEquals(CountryEnum.NL.getImage(), target.country.image);
    }

    @Test
    void testMapClassWithGetterReturningOptionalOfFieldWithStrictMapping() {
        BeanMapper mapper = new BeanMapperBuilder()
                .addPackagePrefix("nl")
                .setApplyStrictMappingConvention(true)
                .build();
        MyEntity myEntity = createMyEntity();
        MyEntityResult result = mapper.map(myEntity, MyEntityResult.class);
        assertEquals(myEntity.value, result.value);
        assertEquals(myEntity.child.value, result.child.value);
    }

    @Test
    void testMapClassWithGetterReturningOptionalOfFieldWithNonStrictMapping() {
        BeanMapper mapper = new BeanMapperBuilder()
                .addPackagePrefix("nl")
                .setApplyStrictMappingConvention(false)
                .build();
        MyEntity myEntity = createMyEntity();
        MyEntityResult result = mapper.map(myEntity, MyEntityResult.class);
        assertEquals(myEntity.value, result.value);
        assertEquals(myEntity.child.value, result.child.value);
    }

    @Test
    void testMapClassWithGetterReturningOptionalOfFieldWhereFieldIsNull() {
        BeanMapper mapper = new BeanMapperBuilder()
                .addPackagePrefix("nl")
                .setApplyStrictMappingConvention(false)
                .build();
        MyEntity myEntity = createMyEntity();
        myEntity.child = null;
        MyEntityResult result = mapper.map(myEntity, MyEntityResult.class);
        assertEquals("Henk", result.value);
        assertNull(result.child);
    }

    @Test
    void mapToOptional() {
        Optional<Person> person = Optional.of(createPerson());
        PersonView personView = beanMapper.map(person, PersonView.class).get();
        assertEquals("Henk", personView.name);
        assertEquals("Zoetermeer", personView.place);
    }

    @Test
    void mapToOptionalEmpty() {
        Optional<Person> person = Optional.empty();
        Optional<PersonView> personView = beanMapper.map(person, PersonView.class);
        assertFalse(personView.isPresent());
    }

    @Test
    void mapCollection_List() {
        Collection<String> collection = List.of("42", "21", "12");
        var result = this.beanMapper.map(collection, Long.class);
        assertEquals(ArrayList.class, result.getClass());
        assertEquals(3, result.size());
        assertTrue(result.contains(42L));
        assertTrue(result.contains(21L));
        assertTrue(result.contains(12L));
    }

    @Test
    void mapCollection_TreeSet() {
        Collection<String> collection = Set.of("42", "21", "12");
        var result = this.beanMapper.map(collection, Long.class);
        assertEquals(TreeSet.class, result.getClass());
        assertEquals(3, result.size());
        assertTrue(result.contains(42L));
        assertTrue(result.contains(21L));
        assertTrue(result.contains(12L));
    }

    @Test
    void mapCollection_HashSet() {
        Collection<Person> collection = Set.of(createPerson("Henk"), createPerson("Klaas"), createPerson("Kees"));
        var result = this.beanMapper.map(collection, PersonResult.class);
        assertEquals(HashSet.class, result.getClass());
        assertEquals(3, result.size());
        assertTrue(result.stream().filter(coll -> coll.name.equals("Henk")).toList().size() == 1);
        assertTrue(result.stream().filter(coll -> coll.name.equals("Klaas")).toList().size() == 1);
        assertTrue(result.stream().filter(coll -> coll.name.equals("Kees")).toList().size() == 1);
        assertTrue(result.stream().filter(coll -> coll.name.equals("Broheim")).toList().isEmpty());
    }

    @Test
    void mapCollection_Queue() {
        Collection<String> collection = new ArrayBlockingQueue<>(10, false);
        collection.add("42");
        collection.add("21");
        collection.add("12");

        var result = this.beanMapper.map(collection, Long.class);
        assertEquals(collection.size(), result.size());
        assertEquals(42L, ((Queue<Long>) result).poll());
        assertEquals(21L, ((Queue<Long>) result).poll());
        assertEquals(12L, ((Queue<Long>) result).poll());
    }

    @Test
    @Disabled("BeanMapperBuilder#setPreferredCollectionClass doesn't work as reasonably expected, resulting in "
            + "failures.")
    void mapQueueToTarget_PriorityQueue() {
        Queue<String> collection = new ArrayDeque<>();
        collection.add("42");
        collection.add("21");
        collection.add("12");

        var result = new BeanMapperBuilder().build().wrap()
                .setPreferredCollectionClass(PriorityQueue.class)
                .setCollectionUsage(BeanCollectionUsage.CONSTRUCT)
                .setTargetClass(Long.class)
                .setFlushAfterClear(FlushAfterClearInstruction.FLUSH_DISABLED)
                .build()
                .map(collection, Long.class);

        assertEquals(PriorityQueue.class, result.getClass());
        assertEquals(collection.size(), result.size());
        assertEquals(42L, ((PriorityQueue<Long>) result).poll());
        assertEquals(21L, ((PriorityQueue<Long>) result).poll());
        assertEquals(12L, ((PriorityQueue<Long>) result).poll());
    }

    @Test
    void mapQueueFieldToPriorityQueueField() {
        var form = new CollectionQueueSource();
        form.queue.add("42");
        form.queue.add("21");
        form.queue.add("12");

        var result = this.beanMapper.map(form, CollectionPriorityQueueTarget.class);
        assertEquals(PriorityQueue.class, result.queue.getClass());
        assertEquals(form.queue.size(), result.queue.size());
        assertEquals(12L, result.queue.poll());
        assertEquals(21L, result.queue.poll());
        assertEquals(42L, result.queue.poll());
    }

    @Test
    void mapCollection_Deque() {
        Collection<String> collection = new ArrayDeque<>();
        collection.add("42");
        collection.add("21");
        collection.add("12");

        var result = this.beanMapper.map(collection, Long.class);
        assertEquals(3, result.size());
        assertTrue(result.contains(42L));
        assertTrue(result.contains(21L));
        assertTrue(result.contains(12L));
    }

    private MyEntity createMyEntity() {
        MyEntity child = new MyEntity();
        child.value = "Piet";

        MyEntity entity = new MyEntity();
        entity.child = child;
        entity.value = "Henk";

        return entity;
    }

    public Person createPerson(String name) {
        Person person = new Person();
        person.setId(1984L);
        person.setName(name);
        person.setPlace("Zoetermeer");
        person.setBankAccount("THX-1138-l33t-call");
        return person;
    }

    public Person createPerson() {
        return createPerson("Henk");
    }

    public PersonForm createPersonForm() {
        PersonForm person = new PersonForm();
        person.setName("Truus");
        person.setPlace("Den Haag");
        person.setBankAccount("XHT-8311-t33l-llac");
        person.setUnidentifiableFluff("0xCAFEBABE");
        return person;
    }

    public House createHouse() {
        House house = new House();
        house.setName("housename");
        Address address = new Address();
        address.setNumber(1);
        address.setStreet("denneweg");
        address.setCountry(new Country("Nederland"));
        house.setAddress(address);
        return house;
    }
}
