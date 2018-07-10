/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.inspector;

import io.beanmapper.annotations.BeanProperty;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class PropertyAccessorTest {
    
    private BeanWithProperties bean;
    
    @Before
    public void setUp() {
        bean = new BeanWithProperties();
    }

    @Test
    public void testField() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(BeanWithProperties.class, "myField");
        Assert.assertEquals("myField", accessor.getName());
        Assert.assertEquals(String.class, accessor.getType());
        Assert.assertEquals("a", accessor.findAnnotation(BeanProperty.class).name());

        Assert.assertNull(bean.myField);
        Assert.assertNull(accessor.getValue(bean));
        
        accessor.setValue(bean, "test");
        
        Assert.assertEquals("test", bean.myField);
        Assert.assertEquals("test", accessor.getValue(bean));
    }

    @Test
    public void testFieldWithGetter() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(BeanWithProperties.class, "myPropertyWithGetter");
        Assert.assertEquals("myPropertyWithGetter", accessor.getName());
        Assert.assertEquals(String.class, accessor.getType());
        Assert.assertEquals("bb", accessor.findAnnotation(BeanProperty.class).name());

        Assert.assertNull(bean.myPropertyWithGetter);
        Assert.assertEquals("null getter", accessor.getValue(bean));

        bean.myPropertyWithGetter = "test";

        Assert.assertEquals("test getter", accessor.getValue(bean));
    }

    @Test
    public void testFieldWithSetter() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(BeanWithProperties.class, "myPropertyWithSetter");
        Assert.assertEquals("myPropertyWithSetter", accessor.getName());
        Assert.assertEquals(String.class, accessor.getType());
        Assert.assertEquals("cc", accessor.findAnnotation(BeanProperty.class).name());

        Assert.assertNull(bean.myPropertyWithSetter);
        Assert.assertNull(accessor.getValue(bean));

        accessor.setValue(bean, "test");

        Assert.assertEquals("test setter", bean.myPropertyWithSetter);
    }

    @Test
    public void testFieldWithGetterAndSetter() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(BeanWithProperties.class, "myPropertyWithGetterAndSetter");
        Assert.assertEquals("myPropertyWithGetterAndSetter", accessor.getName());
        Assert.assertEquals(String.class, accessor.getType());
        Assert.assertEquals("dd", accessor.findAnnotation(BeanProperty.class).name());

        Assert.assertNull(bean.myPropertyWithGetterAndSetter);
        Assert.assertEquals("null getter", accessor.getValue(bean));

        accessor.setValue(bean, "test");

        Assert.assertEquals("test setter", bean.myPropertyWithGetterAndSetter);
        Assert.assertEquals("test setter getter", accessor.getValue(bean));
    }

    @Test
    public void testGetter() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(BeanWithProperties.class, "myGetter");
        Assert.assertEquals("myGetter", accessor.getName());
        Assert.assertEquals(String.class, accessor.getType());
        Assert.assertEquals("e", accessor.findAnnotation(BeanProperty.class).name());

        Assert.assertEquals("result", accessor.getValue(bean));
    }

    @Test
    public void testSetter() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(BeanWithProperties.class, "mySetter");
        Assert.assertEquals("mySetter", accessor.getName());
        Assert.assertEquals(String.class, accessor.getType());
        Assert.assertEquals("f", accessor.findAnnotation(BeanProperty.class).name());

        accessor.setValue(bean, "Bla");
    }

    @Test
    public void testUnknownProperty() {
        Assert.assertNull(PropertyAccessors.findProperty(BeanWithProperties.class, "unknown"));
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
