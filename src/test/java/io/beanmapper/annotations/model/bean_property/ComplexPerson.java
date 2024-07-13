package io.beanmapper.annotations.model.bean_property;

public class ComplexPerson {

    private String f_name;
    private String l_name;
    private String firstName;
    private String lastName;

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getF_name() {
        return f_name;
    }

    public void setL_name(String l_name) {
        this.l_name = l_name;
    }

    public String getL_name() {
        return l_name;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }
}
