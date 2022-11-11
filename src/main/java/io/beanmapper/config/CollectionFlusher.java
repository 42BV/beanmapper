package io.beanmapper.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionFlusher {

    private final List<AfterClearFlusher> afterClearFlushers = new ArrayList<>();

    public List<AfterClearFlusher> getAfterClearFlushers() {
        return Collections.unmodifiableList(this.afterClearFlushers);
    }

    public void addAfterClearFlusher(AfterClearFlusher afterClearFlusher) {
        this.afterClearFlushers.add(afterClearFlusher);
    }

    /**
     * @deprecated If we know that flushAfterClear is false when we call flush, just don't call flush.
     * @param flushAfterClear
     */
    @Deprecated(since = "v4.1.2", forRemoval = true)
    public void flush(boolean flushAfterClear) {
        if (!flushAfterClear) {
            return;
        }
        for (AfterClearFlusher afterClearFlusher : afterClearFlushers) {
            afterClearFlusher.flush();
        }
    }

    /**
     * Calls the {@link AfterClearFlusher#flush()}-method of every AfterClearFlusher registered to this instance.
     */
    public void flush() {
        this.afterClearFlushers.forEach(AfterClearFlusher::flush);
    }

}
