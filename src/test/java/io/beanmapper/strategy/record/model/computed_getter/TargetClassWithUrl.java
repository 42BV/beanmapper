package io.beanmapper.strategy.record.model.computed_getter;

/**
 * Target class (not record) that expects 'url' to be mapped from source's computed getter.
 * Used to demonstrate that BeanMapper CAN map computed getters to classes.
 */
public class TargetClassWithUrl {

    public Long id;
    public String name;
    public String url;
}
