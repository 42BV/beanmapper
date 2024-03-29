package io.beanmapper.config;

import java.util.ArrayList;
import java.util.List;

public class CollectionFlusher {

    private List<AfterClearFlusher> afterClearFlushers = new ArrayList<>();

    public List<AfterClearFlusher> getAfterClearFlushers() {
        return this.afterClearFlushers;
    }

    public void addAfterClearFlusher(AfterClearFlusher afterClearFlusher) {
        this.afterClearFlushers.add(afterClearFlusher);
    }

    public void flush(boolean flushAfterClear) {
        if (!flushAfterClear) {
            return;
        }
        for (AfterClearFlusher afterClearFlusher : afterClearFlushers) {
            afterClearFlusher.flush();
        }
    }

}
