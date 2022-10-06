package io.beanmapper.strategy.record.model.inheritance;

public class Layer1 {

    public final int id;

    public Layer1(final int id) {
        this.id = id;
    }

    public Layer1() {
        this(42);
    }

}
