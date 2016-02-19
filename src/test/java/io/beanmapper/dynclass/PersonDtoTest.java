package io.beanmapper.dynclass;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.dynclass.model.Person;
import io.beanmapper.dynclass.model.PersonDto;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class PersonDtoTest {

    private BeanMapper beanMapper;

    @Before
    public void prepareBeanmapper() {
        beanMapper = new BeanMapperBuilder()
                .addPackagePrefix(BeanMapper.class)
                .build();
    }

    @Test
    public void mapToStatic() {
        Person person = createPerson();
        PersonDto personDto = beanMapper.map(person, PersonDto.class);

        assertEquals((Long)42L, personDto.id);
        assertEquals("Henk", personDto.name);
        assertEquals("Koraalrood", personDto.street);
        assertEquals("11f", personDto.houseNumber);
        assertEquals("Zoetermeer", personDto.city);
    }

    @Test
    public void mapToDynamic() throws Exception {
        beanMapper = beanMapper
                .config()
                .setTargetClass(PersonDto.class)
                .setIncludeFields(Arrays.asList("id", "name")) // <<< this triggers the dynamic bean generation
                .build();
        Person person = createPerson();
        Object dynPersonDto = beanMapper.map(person);

        String json = new ObjectMapper().writeValueAsString(dynPersonDto);
        assertEquals("{\"id\":42,\"name\":\"Henk\"}", json);
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
