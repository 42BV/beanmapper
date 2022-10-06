package io.beanmapper.strategy.record.model.annotation.bean_alias.form;

import io.beanmapper.annotations.BeanAlias;

public class FormWithId_Name_Place {

    @BeanAlias("name")
    public String abc;
    @BeanAlias("id")
    public int def;
    @BeanAlias("place")
    public String ghi;

    public FormWithId_Name_Place(final int id, final String name, String place) {
        this.def = id;
        this.abc = name;
        this.ghi = place;
    }

    public FormWithId_Name_Place() {
        this(42, "Henk", "Zoetermeer");
    }

}
