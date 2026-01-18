package io.beanmapper.strategy.record.model.computed_getter;

/**
 * Target record that expects 'url' to be mapped from source's computed getter.
 */
public record TargetRecordWithUrl(
    Long id,
    String name,
    String url
) {}
