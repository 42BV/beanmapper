package nl.mad.beanmapper.testmodel.encapsulate.sourceAnnotated;

public class CarDriver {

    private String name;
    private int wheels;
    private String brand;
    private Monteur monteur;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWheels() {
        return wheels;
    }

    public void setWheels(int wheels) {
        this.wheels = wheels;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Monteur getMonteur() {
        return monteur;
    }

    public void setMonteur(Monteur monteur) {
        this.monteur = monteur;
    }
}
