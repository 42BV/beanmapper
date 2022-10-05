package io.beanmapper.testmodel.construct;

import java.util.List;

import io.beanmapper.annotations.BeanConstruct;

@BeanConstruct("numbers")
public class TargetBeanConstructWithList {

    private final List<Integer> numbers;

    public TargetBeanConstructWithList(final List<Integer> numbers) {
        this.numbers = numbers;
    }

    public List<Integer> getNumbers() {
        return this.numbers;
    }

}
