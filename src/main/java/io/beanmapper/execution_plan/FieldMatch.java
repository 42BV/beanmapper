package io.beanmapper.execution_plan;

import java.lang.reflect.Field;

public record FieldMatch(Field sourceField, Field targetField) {
}
