package io.beanmapper;

import io.beanmapper.core.converter.impl.LocalDateTimeToLocalDate;
import io.beanmapper.core.converter.impl.LocalDateToLocalDateTime;
import io.beanmapper.core.converter.impl.NestedSourceClassToNestedTargetClassConverter;
import io.beanmapper.core.converter.impl.ObjectToStringConverter;
import io.beanmapper.exceptions.BeanMappingException;
import io.beanmapper.testmodel.collections.*;
import io.beanmapper.testmodel.construct.NestedSourceWithoutConstruct;
import io.beanmapper.testmodel.construct.SourceWithoutConstruct;
import io.beanmapper.testmodel.construct.TargetWithConstruct;
import io.beanmapper.testmodel.converter.SourceWithDate;
import io.beanmapper.testmodel.converter.TargetWithDateTime;
import io.beanmapper.testmodel.converterbetweennestedclasses.NestedSourceClass;
import io.beanmapper.testmodel.converterbetweennestedclasses.NestedTargetClass;
import io.beanmapper.testmodel.converterbetweennestedclasses.SourceWithNestedClass;
import io.beanmapper.testmodel.converterbetweennestedclasses.TargetWithNestedClass;
import io.beanmapper.testmodel.defaults.SourceWithDefaults;
import io.beanmapper.testmodel.defaults.TargetWithDefaults;
import io.beanmapper.testmodel.emptyobject.EmptySource;
import io.beanmapper.testmodel.emptyobject.EmptyTarget;
import io.beanmapper.testmodel.emptyobject.NestedEmptyTarget;
import io.beanmapper.testmodel.encapsulate.*;
import io.beanmapper.testmodel.encapsulate.sourceAnnotated.Car;
import io.beanmapper.testmodel.encapsulate.sourceAnnotated.CarDriver;
import io.beanmapper.testmodel.encapsulate.sourceAnnotated.Driver;
import io.beanmapper.testmodel.enums.ColorEntity;
import io.beanmapper.testmodel.enums.ColorResult;
import io.beanmapper.testmodel.enums.ColorStringResult;
import io.beanmapper.testmodel.ignore.IgnoreSource;
import io.beanmapper.testmodel.ignore.IgnoreTarget;
import io.beanmapper.testmodel.initiallyunmatchedsource.SourceWithUnmatchedField;
import io.beanmapper.testmodel.initiallyunmatchedsource.TargetWithoutUnmatchedField;
import io.beanmapper.testmodel.multipleunwrap.AllTogether;
import io.beanmapper.testmodel.multipleunwrap.LayerA;
import io.beanmapper.testmodel.nestedclasses.Layer1;
import io.beanmapper.testmodel.nestedclasses.Layer1Result;
import io.beanmapper.testmodel.numbers.ClassWithInteger;
import io.beanmapper.testmodel.numbers.ClassWithLong;
import io.beanmapper.testmodel.numbers.SourceWithDouble;
import io.beanmapper.testmodel.numbers.TargetWithDouble;
import io.beanmapper.testmodel.othername.SourceWithOtherName;
import io.beanmapper.testmodel.othername.TargetWithOtherName;
import io.beanmapper.testmodel.parentClass.Project;
import io.beanmapper.testmodel.parentClass.Source;
import io.beanmapper.testmodel.parentClass.Target;
import io.beanmapper.testmodel.person.Person;
import io.beanmapper.testmodel.person.PersonAo;
import io.beanmapper.testmodel.person.PersonForm;
import io.beanmapper.testmodel.person.PersonView;
import io.beanmapper.testmodel.project.CodeProject;
import io.beanmapper.testmodel.project.CodeProjectResult;
import io.beanmapper.testmodel.publicfields.SourceWithPublicFields;
import io.beanmapper.testmodel.publicfields.TargetWithPublicFields;
import io.beanmapper.testmodel.samesourcediffresults.Entity;
import io.beanmapper.testmodel.samesourcediffresults.ResultOne;
import io.beanmapper.testmodel.samesourcediffresults.ResultTwo;
import io.beanmapper.testmodel.similarsubclasses.DifferentSource;
import io.beanmapper.testmodel.similarsubclasses.DifferentTarget;
import io.beanmapper.testmodel.similarsubclasses.SimilarSubclass;
import io.beanmapper.testmodel.tostring.SourceWithNonString;
import io.beanmapper.testmodel.tostring.TargetWithString;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class BeanMapperTest {

    private BeanMapper beanMapper;
    
    @Before
    public void prepareBeanMapper() {
        beanMapper = new BeanMapper();
        beanMapper.addPackagePrefix(BeanMapper.class);
        beanMapper.addConverter(new LocalDateTimeToLocalDate());
        beanMapper.addConverter(new LocalDateToLocalDateTime());
        beanMapper.addConverter(new ObjectToStringConverter());
        beanMapper.addConverter(new NestedSourceClassToNestedTargetClassConverter());
        beanMapper.addProxySkipClass(Enum.class);
    }

    @Test
    public void mapEnum() throws BeanMappingException {
        ColorEntity colorEntity = new ColorEntity();
        colorEntity.setCurrentColor(ColorEntity.RGB.BLUE);
        ColorResult colorResult = beanMapper.map(colorEntity, ColorResult.class);
        assertEquals(ColorEntity.RGB.BLUE, colorResult.currentColor);
    }

    @Test
    public void mapEnumToString() throws BeanMappingException {
        ColorEntity colorEntity = new ColorEntity();
        colorEntity.setCurrentColor(ColorEntity.RGB.GREEN);
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
        CollectionMapSource source = new CollectionMapSource();
        source.items.put("Jan", createPerson("Jan"));
        source.items.put("Piet", createPerson("Piet"));
        source.items.put("Joris", createPerson("Joris"));
        source.items.put("Korneel", createPerson("Korneel"));

        CollectionMapTarget target = beanMapper.map(source, CollectionMapTarget.class);
        assertEquals("Jan", target.items.get("Jan").name);
        assertEquals("Piet", target.items.get("Piet").name);
        assertEquals("Joris", target.items.get("Joris").name);
        assertEquals("Korneel", target.items.get("Korneel").name);
    }

    @Test
    public void mapListCollectionInContainer() {
        CollectionListSource source = new CollectionListSource();
        source.items = new ArrayList<Person>();
        source.items.add(createPerson("Jan"));
        source.items.add(createPerson("Piet"));
        source.items.add(createPerson("Joris"));
        source.items.add(createPerson("Korneel"));

        CollectionListTarget target = beanMapper.map(source, CollectionListTarget.class);
        assertEquals("Jan", target.items.get(0).name);
        assertEquals("Piet", target.items.get(1).name);
        assertEquals("Joris", target.items.get(2).name);
        assertEquals("Korneel", target.items.get(3).name);
    }

    @Test
    public void mapListCollectionInContainerAndClearTheContainer() {
        CollectionListSource source = new CollectionListSource();
        source.items.add(createPerson("Jan"));
        source.items.add(createPerson("Piet"));
        source.items.add(createPerson("Joris"));
        source.items.add(createPerson("Korneel"));

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
        source.items = new TreeSet<String>();
        source.items.add("13");
        source.items.add("29");
        source.items.add("43");

        CollectionSetTarget target = beanMapper.map(source, CollectionSetTarget.class);
        assertTrue("Must contain 13", target.items.contains(13L));
        assertTrue("Must contain 29", target.items.contains(29L));
        assertTrue("Must contain 43", target.items.contains(43L));
    }

    @Test
    public void mapCollection() {
        Collection<Person> sourceItems = new ArrayList<Person>();
        sourceItems.add(createPerson("Jan"));
        sourceItems.add(createPerson("Piet"));
        sourceItems.add(createPerson("Joris"));
        sourceItems.add(createPerson("Korneel"));
        List<PersonView> targetItems = (List<PersonView>)beanMapper.map(sourceItems, PersonView.class);
        assertEquals("Jan", targetItems.get(0).name);
        assertEquals("Piet", targetItems.get(1).name);
        assertEquals("Joris", targetItems.get(2).name);
        assertEquals("Korneel", targetItems.get(3).name);
    }

    @Test
    public void emptySource() {
        // Test of correctly mapping
        EmptySource source = new EmptySource();
        source.id = 42;
        source.name = "sourceName";
        source.emptyName = "notEmpty";
        source.bool = true;
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

        EmptyTarget existingTarget = new EmptyTarget();
        existingTarget.id = 42;
        existingTarget.name = "ExistingTargetName";
        existingTarget.bool = true;
        existingTarget.nestedEmptyClass = new NestedEmptyTarget();
        existingTarget.nestedEmptyClass.name = "Hallo";
        existingTarget.nestedEmpty = new NestedEmptyTarget();
        existingTarget.nestedEmpty.name = "existingNestedTarget";

        EmptyTarget mappedTarget = beanMapper.map(source, existingTarget);
        assertEquals(0, mappedTarget.id, 0);// Default for primitive int is 0
        assertNull(mappedTarget.name);
        assertEquals(false, mappedTarget.bool);// Default for primitive boolean is false
        assertNull(mappedTarget.nestedEmptyClass.name);
        assertNull(mappedTarget.nestedEmpty);
    }

    @Test
    public void copyToExistingTargetInstance() {
        Person person = createPerson();
        PersonForm form = createPersonForm();
        person = beanMapper.map(form, person);
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

    /*
            Assert.assertTrue(converter.match(Integer.class, Long.class));
        Assert.assertEquals(Long.valueOf(42), converter.convert(Integer.valueOf(42), Long.class, null));
     */

    @Test
    public void beanIgnore() {
        IgnoreSource ignoreSource = new IgnoreSource();
        ignoreSource.setBothIgnore("bothIgnore");
        ignoreSource.setSourceIgnore("sourceIgnore");
        ignoreSource.setTargetIgnore("targetIgnore");
        ignoreSource.setNoIgnore("noIgnore");

        IgnoreTarget ignoreTarget = beanMapper.map(ignoreSource, IgnoreTarget.class);
        assertNull("bothIgnore -> target should be empty", ignoreTarget.getBothIgnore());
        assertNull("sourceIgnore -> target should be empty", ignoreTarget.getSourceIgnore());
        assertNull("targetIgnore -> target should be empty", ignoreTarget.getTargetIgnore());
        assertEquals("noIgnore", ignoreTarget.getNoIgnore());
    }

    @Test
    public void mappingToOtherNames() {
        SourceWithOtherName source = new SourceWithOtherName();
        source.setBothOtherName1("bothOtherName");
        source.setSourceOtherName1("sourceOtherName");
        source.setTargetOtherName("targetOtherName");
        source.setNoOtherName("noOtherName");

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
        Source entity = new Source();
        Project project = new Project();
        project.setProjectName("projectName");
        project.setId(42L);
        entity.setProject(project);
        entity.setId(1L);
        entity.setName("abstractName");
        entity.setStreet("street");
        entity.setHouseNumber(42);

        Target target = beanMapper.map(entity, Target.class);
        assertEquals(1, target.getId(), 0);
        assertEquals("abstractName", target.getName());
        assertEquals("street", target.getStreet());
        assertEquals(42, target.getHouseNumber(), 0);
        assertEquals(42, target.getProjectId(), 0);
    }

    @Test
    public void testParentClassReversed() {
        Target target = new Target();
        target.setProjectId(42L);
        target.setId(1L);
        target.setName("abstractName");
        target.setStreet("street");
        target.setHouseNumber(42);

        Source source = beanMapper.map(target, Source.class);
        assertEquals(1, source.getId(), 0);
        assertEquals("abstractName", source.getName());
        assertEquals("street", source.getStreet());
        assertEquals(42, source.getHouseNumber(), 0);
        assertEquals(42, source.getProject().getId(), 0);
    }

    @Test
    public void EncapsulateManyToMany() {
        House house = createHouse();

        ResultManyToMany result = beanMapper.map(house, ResultManyToMany.class);
        assertEquals("housename", result.getName());
        assertEquals("denneweg", result.getAddressOfTheHouse().getStreet());
        assertEquals(1, result.getAddressOfTheHouse().getNumber());
        assertEquals("Nederland", result.getAddressOfTheHouse().getCountry().getCountryName());
    }

    @Test
    public void EncapsulateManyToOne() {
        House house = createHouse();

        ResultManyToOne result = beanMapper.map(house, ResultManyToOne.class);
        assertEquals("housename", result.getName());
        assertEquals("denneweg", result.getStreet());
        assertEquals(1, result.getNumber());
        assertEquals("Nederland", result.getCountryName());
    }

    @Test
    public void EncapsulateOneToMany() {
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
        SourceWithPublicFields source = new SourceWithPublicFields();
        source.name = "Henk";
        source.id = 42L;
        source.date = LocalDate.of(2015, 5, 4);
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
        assertEquals(source.nestedClass.laptopNumber, ((NestedTargetClass)target.nestedClass).laptopNumber);
    }

    @Test
    public void sameSourceTwoDiffResults() {
        Entity entity = new Entity();
        entity.setId(1L);
        entity.setName("name");
        entity.setDescription("description");

        ResultOne resultOne = beanMapper.map(entity, ResultOne.class);
        ResultTwo resultTwo = beanMapper.map(entity, ResultTwo.class);

        assertEquals(1L, resultOne.getId(), 0);
        assertEquals("name", resultOne.getName());
        assertEquals(1L, resultTwo.getId(), 0);
        assertEquals("description", resultTwo.getDescription());
    }

    @Test
    public void testIgnoreWhenNotWritable() {
        CodeProject project = new CodeProject();
        CodeProject master = new CodeProject();
        
        master.id = 42L;
        master.name = "master";
        
        project.id = 24L;
        project.name = "project";
        project.master = master;
        
        CodeProjectResult result = beanMapper.map(project, CodeProjectResult.class);
        
        assertEquals(Long.valueOf(24), result.id);
        assertEquals(Long.valueOf(42), result.masterId);
        
        assertNull(result.name); // Ignored because final field and no setter
        assertNull(result.getMasterName()); // Ignored because private field and no setter
        // Ignored isMaster() because no setter
    }

    @Test
    public void convertGetterListToPublicFieldList() {
        SourceWithListGetter source = new SourceWithListGetter();
        source.lines.add("alpha");
        source.lines.add("beta");
        source.lines.add("gamma");

        TargetWithListPublicField target = beanMapper.map(source, TargetWithListPublicField.class);
        assertEquals(3, target.lines.size());
    }

    @Test
    public void constructAtTargetSide() {
        SourceWithoutConstruct source = new SourceWithoutConstruct();
        source.id = 238L;
        source.firstName = "Rick";
        source.infix = "van der";
        source.lastName = "Waal";
        NestedSourceWithoutConstruct nestedClass = new NestedSourceWithoutConstruct();
        nestedClass.street = "boomweg";
        nestedClass.number = 42;
        source.nestedClass = nestedClass;

        TargetWithConstruct target = beanMapper.map(source, TargetWithConstruct.class);
        assertEquals(source.id, target.id, 0);
        assertEquals(source.firstName + " " + source.infix + " " + source.lastName, target.getFullName());
        assertEquals(source.nestedClass.street+source.nestedClass.number, target.nestedClass.streetWithNumber);
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
