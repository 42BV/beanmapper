package io.beanmapper.annotations;

import static org.junit.jupiter.api.Assertions.*;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.model.bean_property.record.DeepNestedResultRecord;
import io.beanmapper.annotations.model.bean_property.record.NestedSource;
import io.beanmapper.annotations.model.bean_property.record.NestedResultRecord;
import io.beanmapper.annotations.model.bean_property.record.SimpleResultRecord;
import io.beanmapper.config.BeanMapperBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BeanPropertyRecordTest {

    private BeanMapper beanMapper;

    @BeforeEach
    void setUp() {
        beanMapper = new BeanMapperBuilder()
                .setApplyStrictMappingConvention(false)
                .addPackagePrefix(BeanMapper.class)
                .build();
    }

    @Test
    @DisplayName("@BeanProperty on record component should map nested property")
    void beanPropertyOnRecordComponentShouldMapNestedProperty() {
        var parent = new NestedSource(1L, "Parent", null);
        var child = new NestedSource(2L, "Child", parent);

        var result = beanMapper.map(child, NestedResultRecord.class);

        assertEquals(2L, result.id());
        assertEquals("Child", result.name());
        assertEquals(1L, result.parentId(), "@BeanProperty('parent.id') should map parent.id");
        assertEquals("Parent", result.parentName(), "@BeanProperty('parent.name') should map parent.name");
    }

    @Test
    @DisplayName("@BeanProperty with null parent should return null for nested properties")
    void beanPropertyWithNullParentShouldReturnNull() {
        var orphan = new NestedSource(3L, "Orphan", null);

        var result = beanMapper.map(orphan, NestedResultRecord.class);

        assertEquals(3L, result.id());
        assertEquals("Orphan", result.name());
        assertNull(result.parentId(), "Nested property should be null when parent is null");
        assertNull(result.parentName(), "Nested property should be null when parent is null");
    }

    @Test
    @DisplayName("@BeanProperty should support deep nested paths (a.b.c)")
    void beanPropertyShouldSupportDeepNestedPaths() {
        var grandParent = new NestedSource(1L, "GrandParent", null);
        var parent = new NestedSource(2L, "Parent", grandParent);
        var child = new NestedSource(3L, "Child", parent);

        var result = beanMapper.map(child, DeepNestedResultRecord.class);

        assertEquals(3L, result.id());
        assertEquals("Child", result.name());
        assertEquals(1L, result.grandParentId(), "@BeanProperty('parent.parent.id') should map grandparent.id");
    }

    @Test
    @DisplayName("Records without @BeanProperty should continue to work (backward compatibility)")
    void recordsWithoutBeanPropertyShouldWork() {
        var source = new NestedSource(42L, "Simple", null);

        var result = beanMapper.map(source, SimpleResultRecord.class);

        assertEquals(42L, result.id());
        assertEquals("Simple", result.name());
    }
}
