package io.beanmapper.strategy.record.model;

public record FormRecordWithId_Name_Place_BankAccount(int id, String name, String place, String bankAccount) {

    public FormRecordWithId_Name_Place_BankAccount() {
        this(42, "Henk", "Zoetermeer", "NL00ABCD0000000000");
    }

}
