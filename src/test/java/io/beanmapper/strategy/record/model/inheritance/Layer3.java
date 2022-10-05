package io.beanmapper.strategy.record.model.inheritance;

public class Layer3 extends Layer2 {

    public final String place;

    public Layer3(final int id, final String name, final String place) {
        super(id, name);
        this.place = place;
    }

    public Layer3() {
        this.place = "Zoetermeer";
    }

}
