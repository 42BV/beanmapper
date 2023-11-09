package io.beanmapper.testmodel.enums;

public enum WithAbstractMethod {
    ONE_VALUE() {
        @Override
        public void someAbstractMethod() {}
    };
    public abstract void someAbstractMethod();

    public String getName() {
        return name();
    }
}
