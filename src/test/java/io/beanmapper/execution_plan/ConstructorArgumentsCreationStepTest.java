package io.beanmapper.execution_plan;

import io.beanmapper.config.BeanPair;
import io.beanmapper.core.BeanMatch;
import io.beanmapper.core.BeanMatchStore;
import io.beanmapper.testmodel.person.PersonForm;
import io.beanmapper.testmodel.person.PersonResult;

import org.junit.jupiter.api.BeforeEach;

class ConstructorArgumentsCreationStepTest {

    private BeanPair beanPair;
    private BeanMatchStore beanMatchStore;
    private BeanMatch beanMatch;
    private PersonForm personForm;

    @BeforeEach
    void setUp() {
        this.beanPair = new BeanPair(PersonForm.class, PersonResult.class);
        this.beanMatchStore = new BeanMatchStore(null, null);
        this.beanMatch = this.beanMatchStore.getBeanMatch(this.beanPair);
        this.personForm = new PersonForm();
        this.personForm.setName("Henk");
        this.personForm.setPlace("Zoetermeer");
    }

}
