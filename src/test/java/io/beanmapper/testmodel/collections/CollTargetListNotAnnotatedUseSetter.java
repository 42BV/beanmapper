package io.beanmapper.testmodel.collections;

import java.util.ArrayList;
import java.util.List;

public class CollTargetListNotAnnotatedUseSetter {

    private List<String> list = new ArrayList<>();

    public void setList(List<String> list) {
        this.list = list;
    }

    public List<String> getList() {
        return list;
    }

}
