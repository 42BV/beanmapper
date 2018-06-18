package io.beanmapper.testmodel.collections.target_is_wrapped;

public class WrappedTarget {

    private final UnwrappedSource element;

    public WrappedTarget(UnwrappedSource element) {
        this.element = element;
    }

    public UnwrappedSource getElement() {
        return element;
    }

}