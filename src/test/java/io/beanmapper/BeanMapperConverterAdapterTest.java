/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper;

import io.beanmapper.testmodel.person.Person;
import io.beanmapper.testmodel.person.PersonView;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.convert.support.DefaultConversionService;

public class BeanMapperConverterAdapterTest {
    
    @Test
    public void testConvert() {
        BeanMapper beanMapper = new BeanMapper();
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new BeanMapperConverterAdapter(beanMapper));
        Person person = new Person();
        person.setName("Jan");
        PersonView personView = conversionService.convert(person, PersonView.class);
        Assert.assertEquals("Jan", personView.name);
    }

}
