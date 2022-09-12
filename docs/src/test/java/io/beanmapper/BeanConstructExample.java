package io.beanmapper;

import static junit.framework.Assert.assertEquals;

import io.beanmapper.annotations.BeanConstruct;
import io.beanmapper.config.BeanMapperBuilder;

import org.junit.jupiter.api.Test;

public class BeanConstructExample {

    @Test
    void mapWithBeanConstruct() {
        Person person = new Person(1L, "Henk", "de", "Vries");

        PersonResult target = new BeanMapperBuilder().build()
                .map(person, PersonResult.class);

        assertEquals("Henk de Vries", target.fullName);
    }

    public static class Person {
        public Long id;
        public String firstName;
        public String prefix;
        public String lastName;

        public Person(Long id, String firstName, String prefix, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.prefix = prefix;
            this.lastName = lastName;
        }
    }

    @BeanConstruct({"firstName", "prefix", "lastName"})
    public static class PersonResult {
        public String fullName;

        public PersonResult(String name1, String name2, String name3) {
            this.fullName = name1 + " " + name2 + " " + name3;
        }
    }
}
