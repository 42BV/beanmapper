package io.beanmapper.utils;

import io.beanmapper.testmodel.enums.Day;
import io.beanmapper.testmodel.enums.WithAbstractMethod;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CanonicalClassNameTest {
    @Test
    public void determineCanonicalClassNameOfOrdinaryEnum() {
        assertEquals("io.beanmapper.testmodel.enums.Day", CanonicalClassName.determineCanonicalClassName(Day.MONDAY.getClass()));
    }

    @Test
    public void determineCanonicalClassNameOfEnumWithAbstractMethod() {
        assertEquals("io.beanmapper.testmodel.enums.WithAbstractMethod",
                CanonicalClassName.determineCanonicalClassName(WithAbstractMethod.ONE_VALUE.getClass()));
    }
}
