package io.beanmapper.core.inspector;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.beanmapper.core.BeanPropertyAccessType;
import io.beanmapper.core.BeanPropertyMatchupDirection;

import org.junit.jupiter.api.Test;

class BeanPropertyAccessTypeTest {

    @Test
    void fieldInSourceCannotBeAccessed() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(NoAccessToField.class, "name");
        assertEquals(BeanPropertyAccessType.NO_ACCESS, BeanPropertyMatchupDirection.SOURCE_TO_TARGET.accessType(accessor));
    }

    @Test
    void fieldInTargetCannotBeAccessed() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(NoAccessToField.class, "name");
        assertEquals(BeanPropertyAccessType.NO_ACCESS, BeanPropertyMatchupDirection.TARGET_TO_SOURCE.accessType(accessor));
    }

    private class NoAccessToField {
        private String name;
    }

    @Test
    void fieldInSourceAccessedByGetter() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(GetterSetterAccessToField.class, "name");
        assertEquals(BeanPropertyAccessType.GETTER, BeanPropertyMatchupDirection.SOURCE_TO_TARGET.accessType(accessor));
    }

    @Test
    void fieldInTargetAccessedBySetter() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(GetterSetterAccessToField.class, "name");
        assertEquals(BeanPropertyAccessType.SETTER, BeanPropertyMatchupDirection.TARGET_TO_SOURCE.accessType(accessor));
    }

    @Test
    void fieldInSourceAccessedByField() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(FieldAccessToField.class, "name");
        assertEquals(BeanPropertyAccessType.FIELD, BeanPropertyMatchupDirection.SOURCE_TO_TARGET.accessType(accessor));
    }

    @Test
    void fieldInTargetAccessedByField() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(FieldAccessToField.class, "name");
        assertEquals(BeanPropertyAccessType.FIELD, BeanPropertyMatchupDirection.TARGET_TO_SOURCE.accessType(accessor));
    }

    @Test
    void getterInSourceAccessed() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(OnlyGetterSetterNoField.class, "name");
        assertEquals(BeanPropertyAccessType.GETTER, BeanPropertyMatchupDirection.SOURCE_TO_TARGET.accessType(accessor));
    }

    private class FieldAccessToField {
        public String name;
    }

    @Test
    void setterInTargetAccessed() {
        PropertyAccessor accessor = PropertyAccessors.findProperty(OnlyGetterSetterNoField.class, "name");
        assertEquals(BeanPropertyAccessType.SETTER, BeanPropertyMatchupDirection.TARGET_TO_SOURCE.accessType(accessor));
    }

    private class GetterSetterAccessToField {
        private String name;

        public String getName() {return name;}

        public void setName(String name) {this.name = name;}
    }

    private class OnlyGetterSetterNoField {
        public String getName() {return "frits";}

        public void setName(String name) {}
    }

}
