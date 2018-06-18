package io.beanmapper.testmodel.collections.target_is_wrapped;

import java.util.ArrayList;
import java.util.List;

public class TargetWithWrappedItems {

    private List<WrappedTarget> items = new ArrayList<>();

    public List<WrappedTarget> getItems() {
        return items;
    }

    public void setItems(List<WrappedTarget> items) {
        this.items = items;
    }

}
