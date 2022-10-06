package io.beanmapper.testmodel.record;

public class PersonResultRecordWithNestedResult {

    public int id;
    public PersonResultRecordWithNestedResult.PersonResult result;

    public static class PersonResult {
        public String name;
    }

}
