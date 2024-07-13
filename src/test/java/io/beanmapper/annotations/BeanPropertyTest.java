package io.beanmapper.annotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.model.bean_property.ComplexInvalidPersonForm;
import io.beanmapper.annotations.model.bean_property.ComplexPerson;
import io.beanmapper.annotations.model.bean_property.ComplexPersonResult;
import io.beanmapper.annotations.model.bean_property.InvalidPersonForm;
import io.beanmapper.annotations.model.bean_property.Person;
import io.beanmapper.annotations.model.bean_property.PersonForm;
import io.beanmapper.annotations.model.bean_property.PersonResult;
import io.beanmapper.annotations.model.bean_property.ShadowedPersonResult;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.exceptions.DuplicateBeanPropertyTargetException;
import io.beanmapper.exceptions.FieldShadowingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BeanPropertyTest {

    private BeanMapper beanMapper;

    @BeforeEach
    void prepareBeanMapper() {
        beanMapper = new BeanMapperBuilder()
                .setApplyStrictMappingConvention(false)
                .addPackagePrefix(BeanMapper.class)
                .build();
    }

    @Test
    void testMultipleBeanPropertiesFormToEntity() {
        var personForm = new PersonForm("Henk", "Jan");
        var person = assertDoesNotThrow(() -> beanMapper.map(personForm, Person.class));
        assertEquals("Henk Jan", person.getName());
    }

    @Test
    void testMultipleBeanPropertiesEntityToResult() {
        var person = new Person("Henk Jan");
        var personResult = assertDoesNotThrow(() -> beanMapper.map(person, PersonResult.class));
        assertEquals("Henk Jan", personResult.fullName);
    }

    @Test
    void testMultipleBeanPropertiesFormToResult() {
        var personForm = new PersonForm("Henk", "Jan");
        var personResult = assertDoesNotThrow(() -> beanMapper.map(personForm, PersonResult.class));
        assertEquals("Henk Jan", personResult.fullName);
    }

    @Test
    void testInvalidBeanPropertiesShouldThrow() {
        var personForm = new InvalidPersonForm("Henk", "Jan");
        assertThrows(DuplicateBeanPropertyTargetException.class, () -> beanMapper.map(personForm, Person.class));
    }

    @Test
    void testComplexInvalidBeanPropertiesShouldThrow() {
        var personForm = new ComplexInvalidPersonForm("Henk", "Jan");
        assertThrows(DuplicateBeanPropertyTargetException.class, () -> beanMapper.map(personForm, ComplexPerson.class));
        assertThrows(DuplicateBeanPropertyTargetException.class, () -> beanMapper.map(personForm, ComplexPersonResult.class));
    }

    @Test
    void testBeanPropertyShadowingDetected() {
        var shadedPersonForm = new PersonForm("Henk", "Jan");
        assertThrows(FieldShadowingException.class, () -> beanMapper.map(shadedPersonForm, ShadowedPersonResult.class));
    }
}
