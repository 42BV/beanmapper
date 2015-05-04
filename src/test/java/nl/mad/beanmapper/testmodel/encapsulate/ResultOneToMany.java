package nl.mad.beanmapper.testmodel.encapsulate;


import nl.mad.beanmapper.annotations.BeanUnwrap;

public class ResultOneToMany {

    @BeanUnwrap
    public ResultCountry resultCountry;

    public ResultCountry getResultCountry() {
        return resultCountry;
    }

    public void setResultCountry(ResultCountry resultCountry) {
        this.resultCountry = resultCountry;
    }
}
