package io.beanmapper.testmodel.multipleunwrap;

import io.beanmapper.annotations.BeanUnwrap;

public class LayerB {

    private String name2;

    @BeanUnwrap
    private LayerC layerC;

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public LayerC getLayerC() {
        return layerC;
    }

    public void setLayerC(LayerC layerC) {
        this.layerC = layerC;
    }
}
