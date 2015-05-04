package nl.mad.beanmapper.testmodel.encapsulate;

import nl.mad.beanmapper.annotations.BeanProperty;

public class ResultCountry {

    @BeanProperty(name = "name")
    private String countryName;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
