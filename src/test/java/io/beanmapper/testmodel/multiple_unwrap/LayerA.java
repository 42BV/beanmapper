package io.beanmapper.testmodel.multiple_unwrap;

import io.beanmapper.annotations.BeanUnwrap;

public class LayerA {

    private String name1;

    @BeanUnwrap
    private LayerB layerB;

    public static LayerA create() {
        LayerA layerA = new LayerA();
        layerA.setName1("name1");
        LayerB layerB = new LayerB();
        layerA.setLayerB(layerB);
        layerB.setName2("name2");
        LayerC layerC = new LayerC();
        layerB.setLayerC(layerC);
        layerC.setName3("name3");
        return layerA;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public LayerB getLayerB() {
        return layerB;
    }

    public void setLayerB(LayerB layerB) {
        this.layerB = layerB;
    }
}
