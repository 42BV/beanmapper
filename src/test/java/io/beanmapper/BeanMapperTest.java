package io.beanmapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import io.beanmapper.config.AfterClearFlusher;
import io.beanmapper.config.BeanMapperBuilder;
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
import io.beanmapper.testmodel.enums.EnumSourceArraysAsList;
import io.beanmapper.testmodel.enums.EnumTargetList;
import io.beanmapper.testmodel.enums.RGB;
import io.beanmapper.testmodel.enums.UserRole;
import io.beanmapper.testmodel.enums.UserRoleResult;
import io.beanmapper.testmodel.ignore.IgnoreSource;
import io.beanmapper.testmodel.ignore.IgnoreTarget;
import io.beanmapper.testmodel.initially_unmatched_source.SourceWithUnmatchedField;
import io.beanmapper.testmodel.initially_unmatched_source.TargetWithoutUnmatchedField;
import io.beanmapper.testmodel.innerclass.SourceWithInnerClass;
import io.beanmapper.testmodel.innerclass.TargetWithInnerClass;
import io.beanmapper.testmodel.multiple_unwrap.AllTogether;
import io.beanmapper.testmodel.multiple_unwrap.LayerA;
import io.beanmapper.testmodel.nested_classes.Layer1;
import io.beanmapper.testmodel.nested_classes.Layer1Result;
import io.beanmapper.testmodel.nested_classes.Layer3;
import io.beanmapper.testmodel.nested_classes.Layer4;
import io.beanmapper.testmodel.nested_generics.SourceWithNestedGenerics;
import io.beanmapper.testmodel.nested_generics.TargetWithNestedGenerics;
import io.beanmapper.testmodel.not_accessible.source_contains_nested_class.TargetWithPersonName;
import io.beanmapper.testmodel.not_accessible.source_contains_nested_class.SourceWithPerson;
import io.beanmapper.testmodel.not_accessible.target_contains_nested_class.SourceWithPersonName;
import io.beanmapper.testmodel.not_accessible.target_contains_nested_class.TargetWithPerson;
import io.beanmapper.testmodel.numbers.ClassWithInteger;
import io.beanmapper.testmodel.numbers.ClassWithLong;
import io.beanmapper.testmodel.numbers.SourceWithDouble;
import io.beanmapper.testmodel.numbers.TargetWithDouble;
import io.beanmapper.testmodel.othername.SourceWithOtherName;
import io.beanmapper.testmodel.othername.TargetWithOtherName;
import io.beanmapper.testmodel.parent.Player;
import io.beanmapper.testmodel.parent.PlayerForm;
import io.beanmapper.testmodel.parent.SkillForm;
import io.beanmapper.testmodel.parentclass.Project;
import io.beanmapper.testmodel.parentclass.Source;
import io.beanmapper.testmodel.parentclass.Target;
import io.beanmapper.testmodel.person.Person;
import io.beanmapper.testmodel.person.PersonAo;
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
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class BeanMapperTest {

    private BeanMapper beanMapper;
    @Rule public ExpectedException exception = ExpectedException.none();

    @Before
    public void prepareBeanMapper() {
        beanMapper = new BeanMapperBuilder()
                .addPackagePrefix(BeanMapper.class)
                .addConverter(new LocalDateTimeToLocalDate())
                .addConverter(new LocalDateToLocalDateTime())
                .addConverter(new ObjectToStringConverter())
                .addConverter(new NestedSourceClassToNestedTargetClassConverter())
                .build();
    }

    @Test
    public void mapEnum() throws BeanMappingException {
        ColorEntity colorEntity = new ColorEntity();
        colorEntity.setCurrentColor(RGB.BLUE);
        ColorResult colorResult = beanMapper.map(colorEntity, ColorResult.class);
        assertEquals(RGB.BLUE, colorResult.currentColor);
    }

    @Test
    public void mapEnumWithExistingTarget() throws BeanMappingException {
        ColorEntity source = new ColorEntity();
        source.setCurrentColor(RGB.BLUE);

        ColorResult target = new ColorResult();
        target.currentColor = RGB.GREEN;

        beanMapper.map(source, target);
        assertEquals(RGB.BLUE, target.currentColor);
    }

    @Test
    public void mapEnumToString() throws BeanMappingException {
        ColorEntity colorEntity = new ColorEntity();
        colorEntity.setCurrentColor(RGB.GREEN);
        ColorStringResult colorStringResult = beanMapper.map(colorEntity, ColorStringResult.class);
        assertEquals("GREEN", colorStringResult.currentColor);
    }

    @Test
    public void mapFromInterface(@Mocked final PersonAo source) {
        new Expectations() {
            {
                source.getId();
                result = Long.valueOf(42);
                
                source.getName();
                result = "Jan";
            }
        };
        
        Person person = beanMapper.map(source, Person.class);
        assertEquals(Long.valueOf(42), person.getId());
        assertEquals("Jan", person.getName());
        assertNull(person.getPlace());
        assertNull(person.getBankAccount());
    }
    
    @Test
    public void mapToInterface(@Mocked final PersonAo target) {
        Person person = new Person();
        person.setId(Long.valueOf(42));
        person.setName("Jan");
        
        beanMapper.map(person, target);

        new Verifications() {
            {
                target.setId(Long.valueOf(42));
                target.setName("Jan");
            }
        };
    }
    
    @Test
    public void copyToNewTargetInstance() {
        Person person = createPerson();
        PersonView personView = beanMapper.map(person, PersonView.class);
        assertEquals("Henk", personView.name);
        assertEquals("Zoetermeer", personView.place);
    }

    @Test
    public void mapMapCollectionInContainer() {
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
    public void mapListCollectionInContainer() {
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
    public void mapListCollectionWithNested() {
        List<Address> sourceItems = new ArrayList<Address>() {{
            add(new Address("Koraalrood", 1, "Nederland"));
            add(new Address("ComputerStraat", 1, "Duitsland"));
            add(new Address("InternetWeg", 1, "Belgie"));
            add(new Address("MuisLaan", 1, "Frankrijk"));
        }};
        List<ResultAddress> targetItems =
                (List<ResultAddress>) beanMapper.map(sourceItems, ResultAddress.class);
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
    public void mapListCollectionInContainerAndClearTheContainer() {
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
        assertEquals("The target ArrayList must have been cleared, not re-constructed", expectedTargetList, target.items);
        assertEquals("The number of entries must be 4, not 5, because the list has been cleared", 4, target.items.size());
        assertEquals("Jan", target.items.get(0).name);
        assertEquals("Piet", target.items.get(1).name);
        assertEquals("Joris", target.items.get(2).name);
        assertEquals("Korneel", target.items.get(3).name);
    }

    @Test
    public void mapSetCollectionInContainer() {
        CollectionSetSource source = new CollectionSetSource();
        source.items = new TreeSet<String>() {{
            add("13");
            add("29");
            add("43");
        }};
        CollectionSetTarget target = beanMapper.map(source, CollectionSetTarget.class);
        assertEquals(TreeSet.class, target.items.getClass());
        assertTrue("Must contain 13", target.items.contains(13L));
        assertTrue("Must contain 29", target.items.contains(29L));
        assertTrue("Must contain 43", target.items.contains(43L));
    }

    @Test
    public void mapSetCollectionsToIncorrectSubTypeIgnoreCollectionType() {
        CollectionSetSource source = new CollectionSetSource();
        source.items = new TreeSet<String>() {{
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
    public void mapSetCollectionsToSpecificSubType() {
        CollectionSetSource source = new CollectionSetSource();
        source.items = new TreeSet<String>();
        source.items.add("13");
        source.items.add("29");
        source.items.add("43");

        CollectionSetTargetSpecificSubtype target = beanMapper.map(source, CollectionSetTargetSpecificSubtype.class);
        assertEquals(HashSet.class, target.items.getClass());
        assertTrue("Must contain 13", target.items.contains(13L));
        assertTrue("Must contain 29", target.items.contains(29L));
        assertTrue("Must contain 43", target.items.contains(43L));
    }

    @Test
    public void mapArrayList() {
        List<Person> sourceItems = new ArrayList<Person>() {{
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
    public void mapTreeMap() {
        Map<String, Person> sourceItems = new TreeMap<String, Person>() {{
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
    public void direcltyMapSet_comparable() {
        Set<Long> source = new TreeSet<Long>() {{
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
    public void direcltyMapSet_nonComparable() {
        Set<Person> source = new HashSet<Person>() {{
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
    public void mapToCollectionArraysAsList() {
        List<RGB> collection = Arrays.asList(RGB.values());
        List<String> result = beanMapper.map(collection, String.class);
        assertEquals(RGB.values().length, result.size());
    }

    @Test
    public void mapToNestedCollectionArraysAsList() {
        EnumSourceArraysAsList source = new EnumSourceArraysAsList();
        EnumTargetList target =  beanMapper.map(source, EnumTargetList.class);
        assertEquals(RGB.values().length, target.items.size());
        assertEquals("RED", target.items.get(0));
    }

    @Test
    public void mapToCollectionNewArrayList() {
        List<RGB> collection = new ArrayList<>(Arrays.asList(RGB.values()));
        List<String> result = beanMapper.map(collection, String.class);
        assertEquals(RGB.values().length, result.size());
    }

    @Test
    public void mapNonAnnotatedList() {
        CollSourceListNotAnnotated source = new CollSourceListNotAnnotated() {{
            list.add(42L);
            list.add(33L);
        }};
        CollTargetListNotAnnotated target = beanMapper.map(source, CollTargetListNotAnnotated.class);
        assertNotSame("Source and Target list may not be the same, must be copied", source.list, target.list);
        assertEquals(2, target.list.size());
        assertEquals("42", target.list.get(0));
        assertEquals("33", target.list.get(1));
    }

    @Test
    public void mapNonAnnotatedListUseSetter() {
        CollSourceListNotAnnotated source = new CollSourceListNotAnnotated() {{
            list.add(42L);
            list.add(33L);
        }};
        CollTargetListNotAnnotatedUseSetter target = beanMapper.map(source, CollTargetListNotAnnotatedUseSetter.class);
        assertNotSame("Source and Target list may not be the same, must be copied", source.list, target.getList());
        assertEquals(2, target.getList().size());
        assertEquals("42", target.getList().get(0));
        assertEquals("33", target.getList().get(1));
    }

    @Test
    public void mapIncompletelyAnnotatedList() {
        CollSourceListIncompleteAnnotation source = new CollSourceListIncompleteAnnotation() {{
            list.add(42L);
            list.add(33L);
        }};
        CollTargetListNotAnnotated target = beanMapper.map(source, CollTargetListNotAnnotated.class);
        assertNotSame("Source and Target list may not be the same, must be copied", source.list, target.list);
        assertEquals(2, target.list.size());
        assertEquals("42", target.list.get(0));
        assertEquals("33", target.list.get(1));
    }

    @Test
    public void mapNonAnnotatedMap() {
        CollSourceMapNotAnnotated source = new CollSourceMapNotAnnotated() {{
            map.put("a", 42);
            map.put("b", 33);
        }};
        CollTargetMapNotAnnotated target = beanMapper.map(source, CollTargetMapNotAnnotated.class);
        assertNotSame("Source and Target list may not be the same, must be copied", source.map, target.map);
        assertEquals(2, target.map.size());
        assertEquals((Long)42L, target.map.get("a"));
        assertEquals((Long)33L, target.map.get("b"));
    }

    @Test
    public void mapNonAnnotatedListInSuperClass() {
        CollSourceListNotAnnotated source = new CollSourceListNotAnnotated() {{
            list.add(42L);
            list.add(33L);
        }};
        CollSubTargetList target = beanMapper.map(source, CollSubTargetList.class);
        assertNotSame("Source and Target list may not be the same, must be copied", source.list, target.list);
        assertEquals(2, target.list.size());
        assertEquals("42", target.list.get(0));
        assertEquals("33", target.list.get(1));
    }

    @Test
    public void mapListToCollectionEmptySet() {
        CollSourceListNotAnnotated source = new CollSourceListNotAnnotated() {{
            list.add(42L);
            list.add(33L);
        }};
        CollTargetEmptyList target = beanMapper.map(source, CollTargetEmptyList.class);
        assertNotSame("Source and Target list may not be the same, must be copied", source.list, target.list);
        assertEquals(2, target.list.size());
        assertEquals("42", target.list.get(0));
        assertEquals("33", target.list.get(1));
    }

    @Test
    public void mapNonAnnotatedListWithoutGenerics() {
        CollSourceNoGenerics source = new CollSourceNoGenerics() {{
            list.add("42");
            list.add("33");
        }};
        CollTargetNoGenerics target = beanMapper.map(source, CollTargetNoGenerics.class);
        assertSame("Source and Target list may not be the same, must be copied", source.list, target.list);
        assertEquals(2, target.list.size());
        assertEquals("42", target.list.get(0));
        assertEquals("33", target.list.get(1));
    }

    @Test
    public void emptySource() {
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
        assertEquals(false, emptyTarget.bool); // Default for primitive boolean is false
    }

    @Test
    public void emptySourceToExistingTarget() {
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
        assertEquals(false, mappedTarget.bool);// Default for primitive boolean is false
        assertNull(mappedTarget.nestedEmptyClass.name);
        assertNull(mappedTarget.nestedEmpty);
    }

    @Test
    public void mapNull() {
        EmptySource source = null;
        EmptyTarget target = beanMapper.map(source, EmptyTarget.class);
        assertNull(target);
    }

    @Test
    public void copyToExistingTargetInstance() {
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
    public void mapDouble() {
        SourceWithDouble source = new SourceWithDouble();
        source.number = 13.5;
        TargetWithDouble target = beanMapper.map(source, TargetWithDouble.class);
        assertEquals((Double)13.5, target.number);
    }

    @Test
    public void mapLongToInteger() {
        ClassWithLong source = new ClassWithLong();
        source.number = 42L;
        ClassWithInteger target = beanMapper.map(source, ClassWithInteger.class);
        assertEquals((Integer)42, target.number);
    }

    @Test
    public void mapIntegerToLong() {
        ClassWithInteger source = new ClassWithInteger();
        source.number = 42;
        ClassWithLong target = beanMapper.map(source, ClassWithLong.class);
        assertEquals((Long)42L, target.number);
    }

    @Test
    public void beanIgnore() {
        IgnoreSource ignoreSource = new IgnoreSource() {{
            setBothIgnore("bothIgnore");
            setSourceIgnore("sourceIgnore");
            setTargetIgnore("targetIgnore");
            setNoIgnore("noIgnore");
        }};
        IgnoreTarget ignoreTarget = beanMapper.map(ignoreSource, IgnoreTarget.class);
        assertNull("bothIgnore -> target should be empty", ignoreTarget.getBothIgnore());
        assertNull("sourceIgnore -> target should be empty", ignoreTarget.getSourceIgnore());
        assertNull("targetIgnore -> target should be empty", ignoreTarget.getTargetIgnore());
        assertEquals("noIgnore", ignoreTarget.getNoIgnore());
    }

    @Test
    public void mappingToOtherNames() {
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
    public void nonStringToString() {
        SourceWithNonString obj = new SourceWithNonString();
        obj.setDate(LocalDate.of(2015, 4, 1));
        TargetWithString view = beanMapper.map(obj, TargetWithString.class);
        assertEquals("2015-04-01", view.getDate());
    }

    @Test
    public void beanDefault() {
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
    public void testParentClass() {
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
    public void testParentClassReversed() {
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
    public void encapsulateManyToMany() {
        House house = createHouse();

        ResultManyToMany result = beanMapper.map(house, ResultManyToMany.class);
        assertEquals("housename", result.getName());
        assertEquals("denneweg", result.getAddressOfTheHouse().getStreet());
        assertEquals(1, result.getAddressOfTheHouse().getNumber());
        assertEquals("Nederland", result.getAddressOfTheHouse().getCountry().getCountryName());
    }

    @Test
    public void encapsulateManyToOne() {
        House house = createHouse();

        ResultManyToOne result = beanMapper.map(house, ResultManyToOne.class);
        assertEquals("housename", result.getName());
        assertEquals("denneweg", result.getStreet());
        assertEquals(1, result.getNumber());
        assertEquals("Nederland", result.getCountryName());
    }

    @Test
    public void encapsulateManyToOneWithNull() {
        House house = createHouse();
        house.setAddress(null);

        ResultManyToOne result = beanMapper.map(house, ResultManyToOne.class);
        assertEquals("housename", result.getName());
        assertEquals(null, result.getStreet());
        assertEquals(0, result.getNumber());
        assertEquals(null, result.getCountryName());
    }


    @Test
    public void encapsulateOneToMany() {
        Country country = new Country("Nederland");

        ResultOneToMany result = beanMapper.map(country, ResultOneToMany.class);
        assertEquals("Nederland", result.getResultCountry().getCountryName());
    }

    @Test
    public void sourceAnnotated() {
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
    public void initiallyUnmatchedSourceMustBeUsed() {
        SourceWithUnmatchedField swuf = new SourceWithUnmatchedField();
        swuf.setName("Henk");
        swuf.setCountry("NL");
        TargetWithoutUnmatchedField twuf = beanMapper.map(swuf, new TargetWithoutUnmatchedField());
        assertEquals("Henk", twuf.getName());
        assertEquals("NL", twuf.getNation());
    }

    @Test
    public void nestedClasses() {
        Layer1 layer1 = Layer1.createNestedClassObject();
        Layer1Result result = beanMapper.map(layer1, Layer1Result.class);
        assertEquals("layer1", result.getName1());
        assertEquals("layer2", result.getLayer2().getName2());
        assertEquals("name3", result.getLayer2().getLayer3().getName3());
        assertEquals(null, result.getLayer2().getLayer3().getId4());
    }

    @Test
    public void multipleUnwrap() {
        LayerA source = LayerA.create();
        AllTogether target = beanMapper.map(source, AllTogether.class);
        assertEquals("name1", target.getName1());
        assertEquals("name2", target.getName2());
        assertEquals("name3", target.getName3());
    }

    @Test
    public void multipleUnwrapReversed() {
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
    public void publicFields() {
        SourceWithPublicFields source = new SourceWithPublicFields() {{
            name = "Henk";
            id = 42L;
            date = LocalDate.of(2015, 5, 4);
        }};
        TargetWithPublicFields target = beanMapper.map(source, TargetWithPublicFields.class);
        assertEquals("Henk", target.name);
        assertEquals(42L, (long)target.id);
        assertEquals(LocalDate.of(2015, 5, 4), target.date);
    }

    @Test
    public void similarSubclasses() {
        SimilarSubclass subclass = new SimilarSubclass();
        subclass.name = "Henk";
        DifferentSource source = new DifferentSource();
        source.subclass = subclass;
        DifferentTarget target = beanMapper.map(source, DifferentTarget.class);
        assertEquals(source.subclass, target.subclass);
    }

    @Test
    public void converterDateToDateTime() {
        SourceWithDate source = new SourceWithDate();
        source.setDiffType(LocalDate.of(2015, 1, 1));
        source.setSameType(LocalDate.of(2000, 1, 1));

        TargetWithDateTime target = beanMapper.map(source, TargetWithDateTime.class);
        assertEquals(target.getDiffType(), LocalDateTime.of(2015, 1, 1, 0, 0));
        assertEquals(target.getSameType(), LocalDate.of(2000, 1, 1));
    }

    @Test
    public void converterDateTimeToDate() {
        TargetWithDateTime source = new TargetWithDateTime();
        source.setDiffType(LocalDateTime.of(2015, 1, 1, 0, 0));
        source.setSameType(LocalDate.of(2000, 1, 1));

        SourceWithDate target = beanMapper.map(source, SourceWithDate.class);
        assertEquals(target.getDiffType(), LocalDate.of(2015, 1, 1));
        assertEquals(target.getSameType(), LocalDate.of(2000, 1, 1));
    }

    @Test
    public void converterBetweenClasses() {
        SourceWithNestedClass source = new SourceWithNestedClass();
        source.number = 42;
        NestedSourceClass nestedSourceClass = new NestedSourceClass();
        nestedSourceClass.name = "42BV";
        nestedSourceClass.laptopNumber = "765GR";
        source.nestedClass = nestedSourceClass;

        TargetWithNestedClass target = beanMapper.map(source, TargetWithNestedClass.class);
        assertEquals(source.number, target.number, 0);
        assertEquals(source.nestedClass.name, target.nestedClass.name);
        assertEquals("[" + source.nestedClass.laptopNumber + "]", ((NestedTargetClass)target.nestedClass).laptopNumber);
    }

    @Test
    public void converterForClassesInCollection() {
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
    public void sameSourceTwoDiffResults() {
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
    public void testIgnoreWhenNotWritable() {
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
    public void convertGetterListToPublicFieldList() {
        SourceWithListGetter source = new SourceWithListGetter() {{
            lines.add("alpha");
            lines.add("beta");
            lines.add("gamma");
        }};
        TargetWithListPublicField target = beanMapper.map(source, TargetWithListPublicField.class);
        assertEquals(3, target.lines.size());
    }

    @Test
    public void beanAliasTest() {
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
    public void beanConstructTest() {
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
        assertEquals(source.nestedClass.street+source.nestedClass.number, target.nestedClass.streetWithNumber);
    }

    @Test
    public void nestedConstructorAtTargetSideWithBeanUnwrap() {
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
    public void nestedConstructorAtTargetSideWithBeanProperty() {
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
    public void sourceToTargetWithRule() {
        SourceWithRule source = createSourceWithRule();
        TargetWithRule target = createTargetWithRule();
        beanMapper.map(source, target);
        assertEquals(source.getId(), target.getId());
        assertEquals(source.getName(), target.getName());
        assertEquals(target.getNested().nestedInt, target.getNested().nestedInt);
        assertEquals(source.getNested().nestedName, target.getNested().nestedName);
    }

    @Test
    public void sourceToTargetWithRuleOnlyNames() {
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
    public void mapWithInnerClass() {
        SourceWithInnerClass source = new SourceWithInnerClass(1L, "42BV");
        source.innerClass = new SourceWithInnerClass.SourceInnerClass("IT Company");

        TargetWithInnerClass target = beanMapper.map(source, TargetWithInnerClass.class);
        assertEquals(1L, target.id, 0);
        assertEquals("42BV", target.name);
        assertEquals("IT Company", target.innerClass.description);
    }

    @Test
    public void overrideConverterTest() {
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

        exception.expect(BeanConversionException.class);
        exception.expectMessage("Could not convert LocalDate to LocalDateTime.");
        beanMapper.map(source, TargetWithDateTime.class);
    }

    @Test
    public void beanParentDirectDescendantAnnotationOnSource() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .addPackagePrefix(BeanMapper.class)
                .build();

        PlayerForm form = new PlayerForm();
        form.name = "w00t";
        form.skill = new SkillForm();
        form.skill.name = "Athletics";

        Player player = beanMapper.map(form, Player.class);

        assertEquals("@BeanParent on source side was not triggered", form.name, player.getSkill().getPlayer1().getName());
        assertEquals("@BeanParent on target side was not triggered", form.name, player.getSkill().getPlayer2().getName());
    }

    @Test
    public void beanParentThroughCollection() {
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
    public void numberToNumberInList() {
        BeanMapper beanMapper = new BeanMapperBuilder().build();

        List<String> numberStrings = new ArrayList<>();
        numberStrings.add("1");

        List<Integer> numbers = beanMapper.map(numberStrings, Integer.class);
        assertEquals((Integer)1, numbers.get(0));
    }

    @Test
    public void referenceCopy() {
        Layer3 source = new Layer3();
        Layer4 nestedInSource = new Layer4();
        nestedInSource.setId(42L);
        source.setLayer4(nestedInSource);

        BeanMapper beanMapper = new BeanMapperBuilder().build();

        Layer3 target = beanMapper.map(source, Layer3.class);
        assertEquals(source.getLayer4(), target.getLayer4());
    }

    @Test(expected = BeanStrictMappingRequirementsException.class)
    public void strictSource() {
        new BeanMapperBuilder()
                .addBeanPairWithStrictSource(SourceAStrict.class, TargetANonStrict.class)
                .build();
    }

    @Test(expected = BeanStrictMappingRequirementsException.class)
    public void strictTarget() {
        new BeanMapperBuilder()
                .addBeanPairWithStrictTarget(SourceBNonStrict.class, TargetBStrict.class)
                .build();
    }

    @Test(expected = BeanStrictMappingRequirementsException.class)
    public void strictSourceUseAlias() {
        new BeanMapperBuilder()
                .addBeanPairWithStrictSource(SourceCStrict.class, TargetCNonStrict.class)
                .build();
    }

    @Test
    public void strictSourceAllIsFine() {
        new BeanMapperBuilder()
                .addBeanPairWithStrictSource(SourceDStrict.class, TargetDNonStrict.class)
                .build();
    }

    @Test
    public void strictMultipleBeanMismatches() {
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

    @Test(expected = BeanStrictMappingRequirementsException.class)
    public void strictMappingConventionForForm() {
        beanMapper.map(new SourceEForm(), TargetE.class);
    }

    @Test(expected = BeanStrictMappingRequirementsException.class)
    public void strictMappingConventionForResult() {
        beanMapper.map(new SourceF(), TargetFResult.class);
    }

    @Test(expected = BeanStrictMappingRequirementsException.class)
    public void strictMappingConventionMissingMatchForGetter() {
        beanMapper.map(new SCSourceCForm(), SCTargetC.class);
    }

    @Test
    public void strictMappingConventionWithPrivateFieldOnForm() {
        SCSourceAForm source = new SCSourceAForm();
        source.name = "Alpha";
        source.setDoesNotExist("some value");
        SCTargetA target = beanMapper.map(source, SCTargetA.class);
        assertEquals("Alpha", target.name);
        assertEquals("some value", target.doesExist);
    }

    @Test
    public void strictMappingConventionWithPrivateFieldOnResult() {
        SCSourceB source = new SCSourceB();
        source.name = "Alpha";
        source.doesExist = "some value";
        SCTargetBResult target = beanMapper.map(source, SCTargetBResult.class);
        assertEquals("Alpha", target.name);
        assertEquals("some value", target.getDoesNotExist());
    }

    @Test
    public void beanCollectionClearEmptySource() {
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
    public void beanCollectionReuseEmptySource() {
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
    public void beanCollectionConstructEmptySource() {
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
    public void beanCollectionClear() {
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
    public void beanCollectionClearCallsAfterClearFlusher() throws Exception {
        AfterClearFlusher afterClearFlusher = createAfterClearFlusher();
        BeanMapper beanMapper = new BeanMapperBuilder()
                .setFlushEnabled(true)
                .addAfterClearFlusher(afterClearFlusher)
                .build();
        CollSourceClearFlush source = new CollSourceClearFlush() {{
            items.add("A");
        }};
        CollTarget target = new CollTarget() {{
            items = new ArrayList<>();
            items.add("B");
        }};
        beanMapper.map(source, target);
        assertTrue("Should have called the afterClearFlusher instance",
                afterClearFlusher.getClass().getField("trigger").getBoolean(afterClearFlusher));
    }

    @Test
    public void beanCollectionClearDoesNotCallFlusher() throws Exception {
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
        assertFalse("Should NOT have called the afterClearFlusher instance",
                afterClearFlusher.getClass().getField("trigger").getBoolean(afterClearFlusher));
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
    public void beanCollectionReuse() {
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
    public void beanCollectionConstruct() {
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
    public void anonymousClass() {
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
    public void beanMapper_shouldMapFieldsFromSuperclass() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .setApplyStrictMappingConvention(false)
                .build();
        UserRoleResult result = beanMapper.map(UserRole.ADMIN, UserRoleResult.class);
        assertEquals(UserRole.ADMIN.name(), result.name);
    }

    @Test
    public void unmodifiableRandomAccessList() {
        List<Long> numbers = Collections.unmodifiableList(new ArrayList<Long>() {{
            add(42L);
            add(57L);
            add(33L);
        }});
        List<String> numbersAsText = beanMapper.map(numbers, String.class);
        assertEquals(3, numbersAsText.size());
        assertEquals("42", numbersAsText.get(0));
    }

    @Test(expected = BeanNoSuchPropertyException.class)
    public void beanPropertyMismatch() {
        SourceBeanProperty source = new SourceBeanProperty() {{
            age = 42;
        }};
        beanMapper.map(source, TargetBeanProperty.class);
    }

    @Test(expected = BeanNoSuchPropertyException.class)
    public void beanPropertyNestedMismatch() {
        SourceNestedBeanProperty source = new SourceNestedBeanProperty() {{
            value1 = "42";
            value2 = "33";
        }};
        beanMapper.map(source, TargetNestedBeanProperty.class);
    }

    @Test
    public void wrap_mustAlwaysWrap() {
        assertNotSame(beanMapper.getConfiguration(), beanMapper.wrapConfig().build().getConfiguration());
        assertNotSame(beanMapper.getConfiguration(), beanMapper.config().build().getConfiguration());
        assertNotSame(beanMapper.getConfiguration(), beanMapper.wrap().build().getConfiguration());
    }

    @Test
    public void securedSourceFieldHasAccess() {
        assertSecuredSourceField(roles -> true, "Henk");
    }

    @Test
    public void securedSourceFieldNoAccess() {
        assertSecuredSourceField(roles -> false, null);
    }

    @Test
    public void securedTargetFieldHasAccess() {
        assertSecuredTargetField(roles -> true, "Henk");
    }

    @Test
    public void securedTargetFieldNoAccess() {
        assertSecuredTargetField(roles -> false, null);
    }

    @Test
    public void securedSourceMethodNoAccess() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .setSecuredPropertyHandler(roles -> false)
                .build();
        SFSourceCWithSecuredMethod source = new SFSourceCWithSecuredMethod() {{
            setName("Henk");
        }};
        SFTargetA target = beanMapper.map(source, SFTargetA.class);
        assertEquals(null, target.name);
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

    @Test(expected = BeanNoRoleSecuredCheckSetException.class)
    public void throwExceptionWhenSecuredPropertyDoesNotHaveAHandler() {
        SFSourceAWithSecuredField source = new SFSourceAWithSecuredField() {{
            name = "Henk";
        }};
        beanMapper.map(source, SFTargetA.class);
    }

    @Test
    public void allowSecuredPropertyDoesNotHaveAHandler() {
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
    public void logicSecuredCheckMustBlock() {
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
    public void logicSecuredCheckMustBlockBecauseInequalName() {
        logicCheckForEqualName("Blake", null);
    }

    @Test
    public void logicSecuredCheckMustAllowBecauseEqualName() {
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

    @Test(expected = BeanNoLogicSecuredCheckSetException.class)
    public void logicSecuredMissingCheck() {
        SFSourceDLogicSecured source = new SFSourceDLogicSecured() {{
            name = "Henk";
        }};
        beanMapper.map(source, SFTargetA.class);
    }

    @Test
    public void unwrappedToWrapped() {
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
    public void emptyListToExistingList() {
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
    public void nullListToNullList() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .setUseCollectionNullValue(false)
                .build();
        CollectionListSource source = new CollectionListSource();
        source.items = null;
        CollectionListTarget target = beanMapper.map(source, CollectionListTarget.class);
        assertNull(target.items);
    }

    @Test
    public void useBeanPropertyPathToAccessGetterOnly() {
        SourceWithPerson source = new SourceWithPerson();
        TargetWithPersonName target = beanMapper.map(source, TargetWithPersonName.class);
        assertEquals(source.person.getFullName(), target.name);
    }

    @Test
    public void useBeanPropertyPathToAccessSetterOnly() {
        SourceWithPersonName source = new SourceWithPersonName();
        source.name = "Zeefod Beeblebrox";
        TargetWithPerson target = beanMapper.map(source, TargetWithPerson.class);
        assertEquals(source.name, target.person.result);
    }

    @Test
    public void nestedGenericsEmptyList() {
        SourceWithNestedGenerics source = new SourceWithNestedGenerics();
        TargetWithNestedGenerics target = beanMapper.map(source, TargetWithNestedGenerics.class);
        assertNotNull(target.names);
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
