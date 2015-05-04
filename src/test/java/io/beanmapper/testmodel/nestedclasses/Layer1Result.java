package io.beanmapper.testmodel.nestedclasses;

public class Layer1Result {

    private String name1;

    private Layer2Result layer2;

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public Layer2Result getLayer2() {
        return layer2;
    }

    public void setLayer2(Layer2Result layer2) {
        this.layer2 = layer2;
    }

    public static Layer1Result createNestedClassObject() {
        Layer1Result layer1 = new Layer1Result();
        layer1.setName1("layer1");
        Layer2Result layer2 = new Layer2Result();
        layer2.setName2("layer2");
        layer1.setLayer2(layer2);
        Layer3Result layer3 = new Layer3Result();
        layer2.setLayer3(layer3);
        return layer1;
    }

}
