/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.inspector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.beanmapper.annotations.BeanProperty;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
class PropertyAccessorTest {

    private BeanWithProperties bean;

    @BeforeEach
    void setUp() {
        bean = new BeanWithProperties();
    }

    @Test
    void testField() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(BeanWithProperties.class, "myField");
        assertEquals("myField", accessor.getName());
        assertEquals(String.class, accessor.getType());
        assertEquals("a", accessor.findAnnotation(BeanProperty.class).name());

        assertNull(bean.myField);
        assertNull(accessor.getValue(bean));

        accessor.setValue(bean, "test");

        assertEquals("test", bean.myField);
        assertEquals("test", accessor.getValue(bean));
    }

    @Test
    void testFieldWithGetter() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(BeanWithProperties.class, "myPropertyWithGetter");
        assertEquals("myPropertyWithGetter", accessor.getName());
        assertEquals(String.class, accessor.getType());
        assertEquals("bb", accessor.findAnnotation(BeanProperty.class).name());

        assertNull(bean.myPropertyWithGetter);
        assertEquals("null getter", accessor.getValue(bean));

        bean.myPropertyWithGetter = "test";

        assertEquals("test getter", accessor.getValue(bean));
    }

    @Test
    void testFieldWithSetter() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(BeanWithProperties.class, "myPropertyWithSetter");
        assertEquals("myPropertyWithSetter", accessor.getName());
        assertEquals(String.class, accessor.getType());
        assertEquals("cc", accessor.findAnnotation(BeanProperty.class).name());

        assertNull(bean.myPropertyWithSetter);
        assertNull(accessor.getValue(bean));

        accessor.setValue(bean, "test");

        assertEquals("test setter", bean.myPropertyWithSetter);
    }

    @Test
    void testFieldWithGetterAndSetter() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(BeanWithProperties.class, "myPropertyWithGetterAndSetter");
        assertEquals("myPropertyWithGetterAndSetter", accessor.getName());
        assertEquals(String.class, accessor.getType());
        assertEquals("dd", accessor.findAnnotation(BeanProperty.class).name());

        assertNull(bean.myPropertyWithGetterAndSetter);
        assertEquals("null getter", accessor.getValue(bean));

        accessor.setValue(bean, "test");

        assertEquals("test setter", bean.myPropertyWithGetterAndSetter);
        assertEquals("test setter getter", accessor.getValue(bean));
    }

    @Test
    void testGetter() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(BeanWithProperties.class, "myGetter");
        assertEquals("myGetter", accessor.getName());
        assertEquals(String.class, accessor.getType());
        assertEquals("e", accessor.findAnnotation(BeanProperty.class).name());

        assertEquals("result", accessor.getValue(bean));
    }

    @Test
    void testSetter() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(BeanWithProperties.class, "mySetter");
        assertEquals("mySetter", accessor.getName());
        assertEquals(String.class, accessor.getType());
        assertEquals("f", accessor.findAnnotation(BeanProperty.class).name());

        accessor.setValue(bean, "Bla");
    }

    @Test
    void testUnknownProperty() {
        assertNull(PropertyAccessors.findProperty(BeanWithProperties.class, "unknown"));
    }

    @SuppressWarnings("unused")
    static class BeanWithProperties {

        @BeanProperty(name = "a")
        public String myField;

        @BeanProperty(name = "b")
        private String myPropertyWithGetter;

        @BeanProperty(name = "c")
        private String myPropertyWithSetter;

        @BeanProperty(name = "d")
        private String myPropertyWithGetterAndSetter;

        @BeanProperty(name = "bb")
        public String getMyPropertyWithGetter() {
            return myPropertyWithGetter + " getter";
        }

        @BeanProperty(name = "cc")
        public void setMyPropertyWithSetter(String myPropertyWithSetter) {
            this.myPropertyWithSetter = myPropertyWithSetter + " setter";
        }

        @BeanProperty(name = "dd")
        public String getMyPropertyWithGetterAndSetter() {
            return myPropertyWithGetterAndSetter + " getter";
        }

        @BeanProperty(name = "ddd")
        public void setMyPropertyWithGetterAndSetter(String myPropertyWithGetterAndSetter) {
            this.myPropertyWithGetterAndSetter = myPropertyWithGetterAndSetter + " setter";
        }

        @BeanProperty(name = "e")
        public String getMyGetter() {
            return "result";
        }

        @BeanProperty(name = "f")
        public void setMySetter(String arg) { /* no action required */ }

    }

}
