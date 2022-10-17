package io.beanmapper.config;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionFlusher {

    private Collection<AfterClearFlusher> afterClearFlushers = new ArrayList<>();

    public Iterable<AfterClearFlusher> getAfterClearFlushers() {
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
