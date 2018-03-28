package io.beanmapper.annotations;

public interface LogicSecuredCheck<S,T> {

    boolean isAllowed(S source, T target);

}
