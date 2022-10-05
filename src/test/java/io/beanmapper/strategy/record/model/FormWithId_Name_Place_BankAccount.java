package io.beanmapper.strategy.record.model;

import io.beanmapper.strategy.record.model.annotation.bean_alias.form.FormWithId_Name_Place;

public class FormWithId_Name_Place_BankAccount extends FormWithId_Name_Place {

    public final String bankAccount;

    public FormWithId_Name_Place_BankAccount(final int id, final String name, final String place, final String bankAccount) {
        super(id, name, place);
        this.bankAccount = bankAccount;
    }

    public FormWithId_Name_Place_BankAccount() {
        this(42, "Henk", "Zoetermeer", "NL00ABCD0000000000");
    }
}
