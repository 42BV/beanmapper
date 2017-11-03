package io.beanmapper.core.converter.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AnnotationClassTest {

    @Test
    public void nullClass() {
        AnnotationClass annotationClass = new AnnotationClass(null);
        assertEquals(null, annotationClass.getAnnotationClass());
        assertTrue(annotationClass.isEmpty());
    }

    @Test
    public void voidClass() {
        AnnotationClass annotationClass = new AnnotationClass(void.class);
        assertEquals(null, annotationClass.getAnnotationClass());
        assertTrue(annotationClass.isEmpty());
    }

    @Test
    public void normalClass() {
        AnnotationClass annotationClass = new AnnotationClass(String.class);
        assertEquals(String.class, annotationClass.getAnnotationClass());
        assertFalse(annotationClass.isEmpty());
    }

}
