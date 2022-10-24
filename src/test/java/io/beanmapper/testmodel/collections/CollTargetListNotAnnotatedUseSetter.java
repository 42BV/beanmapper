package io.beanmapper.testmodel.collections;

import java.util.ArrayList;
import java.util.List;

public class CollTargetListNotAnnotatedUseSetter {

    private List<String> list = new ArrayList<>();

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

}
