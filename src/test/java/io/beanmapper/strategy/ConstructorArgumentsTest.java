package io.beanmapper.strategy;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.beanmapper.config.BeanPair;
import io.beanmapper.core.BeanMatch;
import io.beanmapper.core.BeanMatchStore;
import io.beanmapper.core.inspector.BeanPropertySelector;
import io.beanmapper.testmodel.person.PersonForm;
import io.beanmapper.testmodel.person.PersonResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConstructorArgumentsTest {

    private BeanPair beanPair;
    private BeanMatchStore beanMatchStore;
    private BeanMatch beanMatch;
    private PersonForm personForm;

    @BeforeEach
    void setUp() {
        this.beanPair = new BeanPair(PersonForm.class, PersonResult.class);
        this.beanMatchStore = new BeanMatchStore(null, null, new BeanPropertySelector());
        this.beanMatch = this.beanMatchStore.getBeanMatch(this.beanPair);
        this.personForm = new PersonForm();
        this.personForm.setName("Henk");
        this.personForm.setPlace("Zoetermeer");
    }

    @Test
    void testPassNullAsConstructorArgs_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new ConstructorArguments(this.personForm, this.beanMatch, null));
    }
}
