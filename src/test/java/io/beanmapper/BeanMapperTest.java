package io.beanmapper;

import io.beanmapper.exceptions.BeanMappingException;
import io.beanmapper.testmodel.defaults.SourceWithDefaults;
import io.beanmapper.testmodel.defaults.TargetWithDefaults;
import io.beanmapper.testmodel.encapsulate.*;
import io.beanmapper.testmodel.encapsulate.sourceAnnotated.Car;
import io.beanmapper.testmodel.encapsulate.sourceAnnotated.CarDriver;
import io.beanmapper.testmodel.encapsulate.sourceAnnotated.Driver;
import io.beanmapper.testmodel.ignore.IgnoreSource;
import io.beanmapper.testmodel.ignore.IgnoreTarget;
import io.beanmapper.testmodel.initiallyunmatchedsource.SourceWithUnmatchedField;
import io.beanmapper.testmodel.initiallyunmatchedsource.TargetWithoutUnmatchedField;
import io.beanmapper.testmodel.multipleunwrap.AllTogether;
import io.beanmapper.testmodel.multipleunwrap.LayerA;
import io.beanmapper.testmodel.nestedclasses.Layer1;
import io.beanmapper.testmodel.nestedclasses.Layer1Result;
import io.beanmapper.testmodel.othername.SourceWithOtherName;
import io.beanmapper.testmodel.othername.TargetWithOtherName;
import io.beanmapper.testmodel.parentClass.Project;
import io.beanmapper.testmodel.parentClass.Source;
import io.beanmapper.testmodel.parentClass.Target;
import io.beanmapper.testmodel.person.Person;
import io.beanmapper.testmodel.person.PersonForm;
import io.beanmapper.testmodel.person.PersonView;
import io.beanmapper.testmodel.publicfields.SourceWithPublicFields;
import io.beanmapper.testmodel.publicfields.TargetWithPublicFields;
import io.beanmapper.testmodel.similarsubclasses.DifferentSource;
import io.beanmapper.testmodel.similarsubclasses.DifferentTarget;
import io.beanmapper.testmodel.similarsubclasses.SimilarSubclass;
import io.beanmapper.testmodel.tostring.SourceWithNonString;
import io.beanmapper.testmodel.tostring.TargetWithString;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BeanMapperTest {

    private BeanMapper beanMapper;
    
    @Before
    public void prepareBeanMapper() {
        beanMapper = new BeanMapper();
        beanMapper.addPackagePrefix(BeanMapper.class);
    }
    
    @Test
    public void copyToNewTargetInstance() throws BeanMappingException {
        Person person = createPerson();
        PersonView personView = beanMapper.map(person, PersonView.class);
        assertEquals("Henk", personView.name);
        assertEquals("Zoetermeer", personView.place);
    }

    @Test
    public void mapCollection() throws BeanMappingException {
        Collection<Person> sourceItems = new ArrayList<>();
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
    public void emptySource() throws BeanMappingException {
        Person source = new Person();
        PersonView target = beanMapper.map(source, PersonView.class);
        assertNull(target.name);
    }

    @Test
    public void copyToExistingTargetInstance() throws BeanMappingException {
        Person person = createPerson();
        PersonForm form = createPersonForm();
        person = beanMapper.map(form, person);
        assertEquals(1984L, (long) person.getId());
        assertEquals("Truus", person.getName());
        assertEquals("XHT-8311-t33l-llac", person.getBankAccount());
        assertEquals("Den Haag", person.getPlace());
    }

    @Test
    public void beanIgnore() throws BeanMappingException {
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
    public void mappingToOtherNames() throws BeanMappingException {
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
    public void nonStringToString() throws BeanMappingException {
        SourceWithNonString obj = new SourceWithNonString();
        obj.setDate(LocalDate.of(2015, 4, 1));
        TargetWithString view = beanMapper.map(obj, TargetWithString.class);
        assertEquals("2015-04-01", view.getDate());
    }

    @Test
    public void beanDefault() throws BeanMappingException {
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
    public void testParentClass() throws BeanMappingException {
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
    public void testParentClassReversed() throws BeanMappingException {
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
    public void EncapsulateManyToMany() throws BeanMappingException {
        House house = createHouse();

        ResultManyToMany result = beanMapper.map(house, ResultManyToMany.class);
        assertEquals("housename", result.getName());
        assertEquals("denneweg", result.getAddressOfTheHouse().getStreet());
        assertEquals(1, result.getAddressOfTheHouse().getNumber());
        assertEquals("Nederland", result.getAddressOfTheHouse().getCountry().getCountryName());
    }

    @Test
    public void EncapsulateManyToOne() throws BeanMappingException {
        House house = createHouse();

        ResultManyToOne result = beanMapper.map(house, ResultManyToOne.class);
        assertEquals("housename", result.getName());
        assertEquals("denneweg", result.getStreet());
        assertEquals(1, result.getNumber());
        assertEquals("Nederland", result.getCountryName());
    }

    @Test
    public void EncapsulateOneToMany() throws BeanMappingException {
        Country country = new Country("Nederland");

        ResultOneToMany result = beanMapper.map(country, ResultOneToMany.class);
        assertEquals("Nederland", result.getResultCountry().getCountryName());
    }

    @Test
    public void sourceAnnotated() throws BeanMappingException {
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
    public void initiallyUnmatchedSourceMustBeUsed() throws BeanMappingException {
        SourceWithUnmatchedField swuf = new SourceWithUnmatchedField();
        swuf.setName("Henk");
        swuf.setCountry("NL");
        TargetWithoutUnmatchedField twuf = beanMapper.map(swuf, new TargetWithoutUnmatchedField());
        assertEquals("Henk", twuf.getName());
        assertEquals("NL", twuf.getNation());
    }

    @Test
    public void nestedClasses() throws BeanMappingException {
        Layer1 layer1 = Layer1.createNestedClassObject();
        Layer1Result result = beanMapper.map(layer1, Layer1Result.class);
        assertEquals("layer1", result.getName1());
        assertEquals("layer2", result.getLayer2().getName2());
        assertEquals("name3", result.getLayer2().getLayer3().getName3());
    }

    @Test
    public void multipleUnwrap() throws BeanMappingException {
        LayerA source = LayerA.create();
        AllTogether target = beanMapper.map(source, AllTogether.class);
        assertEquals("name1", target.getName1());
        assertEquals("name2", target.getName2());
        assertEquals("name3", target.getName3());
    }

    @Test
    public void multipleUnwrapReversed() throws BeanMappingException {
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
    public void publicFields() throws BeanMappingException {
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
    public void similarSubclasses() throws BeanMappingException {
        SimilarSubclass subclass = new SimilarSubclass();
        subclass.name = "Henk";
        DifferentSource source = new DifferentSource();
        source.subclass = subclass;
        DifferentTarget target = beanMapper.map(source, DifferentTarget.class);
        assertEquals(source.subclass, target.subclass);
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
