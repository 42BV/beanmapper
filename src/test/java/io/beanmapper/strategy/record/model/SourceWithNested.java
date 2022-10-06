package io.beanmapper.strategy.record.model;

public record SourceWithNested(String name, NestedSource nested) {

    public record NestedSource(String name) {
    }

}
