package io.beanmapper.strategy;

public interface MapStrategy {

    <S, T> T map(S source);

}
