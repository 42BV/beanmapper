package io.beanmapper.core.converter.collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AnnotationClassTest {

    @Test
    void nullClass() {
        AnnotationClass annotationClass = new AnnotationClass(null);
        assertNull(annotationClass.annotationClass());
        assertTrue(annotationClass.isEmpty());
    }

    @Test
    void voidClass() {
        AnnotationClass annotationClass = new AnnotationClass(void.class);
        assertNull(annotationClass.annotationClass());
        assertTrue(annotationClass.isEmpty());
    }

    @Test
    void normalClass() {
        AnnotationClass annotationClass = new AnnotationClass(String.class);
        assertEquals(String.class, annotationClass.annotationClass());
        assertFalse(annotationClass.isEmpty());
    }

}
