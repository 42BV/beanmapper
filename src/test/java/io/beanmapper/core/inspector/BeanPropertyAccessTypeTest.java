package io.beanmapper.core.inspector;

import static org.junit.Assert.assertEquals;

import io.beanmapper.core.BeanPropertyAccessType;
import io.beanmapper.core.BeanPropertyMatchupDirection;

import org.junit.Test;

public class BeanPropertyAccessTypeTest {

    private PropertyAccessors propertyAccessors = new PropertyAccessors();

    @Test
    public void fieldInSourceCannotBeAccessed() {
        PropertyAccessor accessor = propertyAccessors.findProperty(NoAccessToField.class, "name");
        assertEquals(BeanPropertyAccessType.NO_ACCESS, BeanPropertyMatchupDirection.SOURCE_TO_TARGET.accessType(accessor));
    }

    @Test
    public void fieldInTargetCannotBeAccessed() {
        PropertyAccessor accessor = propertyAccessors.findProperty(NoAccessToField.class, "name");
        assertEquals(BeanPropertyAccessType.NO_ACCESS, BeanPropertyMatchupDirection.TARGET_TO_SOURCE.accessType(accessor));
    }

    private class NoAccessToField {
        private String name;
    }

    @Test
    public void fieldInSourceAccessedByGetter() {
        PropertyAccessor accessor = propertyAccessors.findProperty(GetterSetterAccessToField.class, "name");
        assertEquals(BeanPropertyAccessType.GETTER, BeanPropertyMatchupDirection.SOURCE_TO_TARGET.accessType(accessor));
    }

    @Test
    public void fieldInTargetAccessedBySetter() {
        PropertyAccessor accessor = propertyAccessors.findProperty(GetterSetterAccessToField.class, "name");
        assertEquals(BeanPropertyAccessType.SETTER, BeanPropertyMatchupDirection.TARGET_TO_SOURCE.accessType(accessor));
    }

    private class GetterSetterAccessToField {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @Test
    public void fieldInSourceAccessedByField() {
        PropertyAccessor accessor = propertyAccessors.findProperty(FieldAccessToField.class, "name");
        assertEquals(BeanPropertyAccessType.FIELD, BeanPropertyMatchupDirection.SOURCE_TO_TARGET.accessType(accessor));
    }

    @Test
    public void fieldInTargetAccessedByField() {
        PropertyAccessor accessor = propertyAccessors.findProperty(FieldAccessToField.class, "name");
        assertEquals(BeanPropertyAccessType.FIELD, BeanPropertyMatchupDirection.TARGET_TO_SOURCE.accessType(accessor));
    }

    private class FieldAccessToField {
        public String name;
    }

    @Test
    public void getterInSourceAccessed() {
        PropertyAccessor accessor = propertyAccessors.findProperty(OnlyGetterSetterNoField.class, "name");
        assertEquals(BeanPropertyAccessType.GETTER, BeanPropertyMatchupDirection.SOURCE_TO_TARGET.accessType(accessor));
    }

    @Test
    public void setterInTargetAccessed() {
        PropertyAccessor accessor = propertyAccessors.findProperty(OnlyGetterSetterNoField.class, "name");
        assertEquals(BeanPropertyAccessType.SETTER, BeanPropertyMatchupDirection.TARGET_TO_SOURCE.accessType(accessor));
    }

    private class OnlyGetterSetterNoField {
        public String getName() { return "frits"; }
        public void setName(String name) { }
    }

}
