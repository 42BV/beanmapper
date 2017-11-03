package io.beanmapper.testmodel.nested_classes;

public class Layer1 {

    private String name1;

    private Layer2 layer2;

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public Layer2 getLayer2() {
        return layer2;
    }

    public void setLayer2(Layer2 layer2) {
        this.layer2 = layer2;
    }

    public static Layer1 createNestedClassObject() {
        Layer1 layer1 = new Layer1();
        layer1.setName1("layer1");
        Layer2 layer2 = new Layer2();
        layer2.setName2("layer2");
        layer1.setLayer2(layer2);
        Layer3 layer3 = new Layer3();
        layer3.setName3("name3");
        layer2.setLayer3(layer3);
        return layer1;
    }

}
