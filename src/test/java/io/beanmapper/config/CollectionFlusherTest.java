package io.beanmapper.config;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class CollectionFlusherTest {

    private Counter counter;

    @Before
    public void setup() {
        counter = new Counter();
    }

    @Test
    public void createAndAddFlushersCallFlush() {
        CollectionFlusher flusher = new CollectionFlusher();
        flusher.addAfterClearFlusher(new MyAfterClearFlusher(counter));
        flusher.addAfterClearFlusher(new MyAfterClearFlusher(counter));
        assertEquals(2, flusher.getAfterClearFlushers().size());
        flusher.flush(true);
        assertEquals(2, counter.get());
    }

    @Test
    public void createAndAddFlushersCallFlushNotToBeCalled() {
        CollectionFlusher flusher = new CollectionFlusher();
        flusher.addAfterClearFlusher(new MyAfterClearFlusher(counter));
        flusher.addAfterClearFlusher(new MyAfterClearFlusher(counter));
        assertEquals(2, flusher.getAfterClearFlushers().size());
        flusher.flush(false);
        assertEquals(0, counter.get());
    }

    class Counter {
        private int count = 0;
        public void add() { count++; }
        public int get() { return count; }
    }

    class MyAfterClearFlusher implements AfterClearFlusher {
        private final Counter counter;

        MyAfterClearFlusher(Counter counter) {
            this.counter = counter;
        }

        @Override public void flush() {
            counter.add();
        }
    }

}
