package io.beanmapper.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.beanmapper.BeanMapper;
import io.beanmapper.testmodel.person.Person;
import io.beanmapper.testmodel.person.PersonForm;
import io.beanmapper.testmodel.person.PersonResult;

import org.junit.Before;
import org.junit.Test;

public class StrictMappingPropertiesTest {

    private CoreConfiguration coreConfiguration;

    @Before
    public void setup() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .build();
        coreConfiguration = (CoreConfiguration)beanMapper.getConfiguration();
    }

    @Test
    public void setSourceAsStrict() {
        StrictMappingProperties properties = new StrictMappingProperties(
                coreConfiguration.getBeanUnproxy(),
                coreConfiguration.getStrictSourceSuffix(),
                coreConfiguration.getStrictTargetSuffix(),
                true);
        BeanPair beanPair = properties.createBeanPair(PersonForm.class, Person.class);
        assertTrue(beanPair.isSourceStrict());
        assertFalse(beanPair.isTargetStrict());
    }

    @Test
    public void setTargetAsStrict() {
        StrictMappingProperties properties = new StrictMappingProperties(
                coreConfiguration.getBeanUnproxy(),
                coreConfiguration.getStrictSourceSuffix(),
                coreConfiguration.getStrictTargetSuffix(),
                true);
        BeanPair beanPair = properties.createBeanPair(Person.class, PersonResult.class);
        assertTrue(beanPair.isTargetStrict());
        assertFalse(beanPair.isSourceStrict());
    }

    @Test
    public void doNotApplyStrictMapping() {
        StrictMappingProperties properties = new StrictMappingProperties(
                coreConfiguration.getBeanUnproxy(),
                coreConfiguration.getStrictSourceSuffix(),
                coreConfiguration.getStrictTargetSuffix(),
                false);
        BeanPair beanPair = properties.createBeanPair(PersonForm.class, PersonResult.class);
        assertFalse(beanPair.isTargetStrict());
        assertFalse(beanPair.isSourceStrict());
    }

    @Test
    public void noSuffixesToUse() {
        StrictMappingProperties properties = new StrictMappingProperties(
                coreConfiguration.getBeanUnproxy(),
                null,
                null,
                true);
        BeanPair beanPair = properties.createBeanPair(PersonForm.class, PersonResult.class);
        assertFalse(beanPair.isTargetStrict());
        assertFalse(beanPair.isSourceStrict());
    }

}
