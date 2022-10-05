package io.beanmapper.strategy.record.model.inheritance;

public class Layer2 extends Layer1 {

    public final String name;

    public Layer2(final int id, final String name) {
        super(id);
        this.name = name;
    }

    public Layer2() {
        this.name = "Henk";
    }

}
