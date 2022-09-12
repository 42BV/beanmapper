package io.beanmapper.dynclass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.dynclass.model.Person;
import io.beanmapper.dynclass.model.PersonDto;
import io.beanmapper.dynclass.model.PersonForm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class PersonDtoTest {

    private BeanMapper beanMapper;

    @BeforeEach
    void prepareBeanmapper() {
        beanMapper = new BeanMapperBuilder()
                .addPackagePrefix(BeanMapper.class)
                .build();
    }

    @Test
    void mapToStatic() {
        Person person = createPerson();
        PersonDto personDto = beanMapper.map(person, PersonDto.class);

        assertEquals((Long) 42L, personDto.id);
        assertEquals("Henk", personDto.getName());
        assertEquals("Koraalrood", personDto.street);
        assertEquals("11f", personDto.number);
        assertEquals("Zoetermeer", personDto.city);
    }

    @Test
    void mapToDynamic() throws Exception {
        beanMapper = beanMapper
                .wrap()
                .setTargetClass(PersonDto.class)
                .downsizeTarget(Arrays.asList("id", "name")) // <<< this triggers the dynamic bean generation
                .build();
        Person person = createPerson();
        Object dynPersonDto = beanMapper.map(person);

        String json = new ObjectMapper().writeValueAsString(dynPersonDto);
        assertEquals("{\"id\":42,\"name\":\"Henk\"}", json);
    }

    @Test
    void mapDynamicOverNewSource() {
        PersonForm personForm = new PersonForm(null, "Geellaan", "33", null);

        Person person = beanMapper.wrap()
                //only map street & number to the new person.
                .downsizeSource(Arrays.asList("street", "houseNumber"))
                .build()
                .map(personForm, Person.class);

        assertNull(person.getId());
        assertNull(person.getName());
        assertEquals("Geellaan", person.getStreet());
        assertEquals("33", person.getHouseNumber());
        assertNull(person.getCity());
        assertNull(person.getBankAccount());
    }

    @Test
    void mapDynamicOverExistingSource() {
        PersonForm personForm = new PersonForm(null, "Geellaan", "33", null);
        Person person = createPerson();

        person = beanMapper.wrap()
                //only map street & number over the existing person.
                .downsizeSource(Arrays.asList("street", "houseNumber"))
                .build()
                .map(personForm, person);

        assertEquals(42L, person.getId(), 0);
        assertEquals("Henk", person.getName());
        assertEquals("Geellaan", person.getStreet());
        assertEquals("33", person.getHouseNumber());
        assertEquals("Zoetermeer", person.getCity());
        assertEquals("NLABN123998877665544", person.getBankAccount());
    }

    private Person createPerson() {
        Person person = new Person();
        person.setId(42L);
        person.setName("Henk");
        person.setStreet("Koraalrood");
        person.setHouseNumber("11f");
        person.setCity("Zoetermeer");
        person.setBankAccount("NLABN123998877665544");
        return person;
    }

}
