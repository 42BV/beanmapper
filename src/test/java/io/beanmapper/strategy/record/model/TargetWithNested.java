package io.beanmapper.strategy.record.model;

public record TargetWithNested(String name, NestedTarget nested) {

    public record NestedTarget(String name) {
    }

}
