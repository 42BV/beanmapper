package io.beanmapper.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CollectionFlusherTest {

    private Counter counter;

    @BeforeEach
    public void setup() {
        counter = new Counter();
    }

    @Test
    void createAndAddFlushersCallFlush() {
        CollectionFlusher flusher = new CollectionFlusher();
        flusher.addAfterClearFlusher(new MyAfterClearFlusher(counter));
        flusher.addAfterClearFlusher(new MyAfterClearFlusher(counter));
        assertEquals(2, flusher.getAfterClearFlushers().size());
        flusher.flush(true);
        assertEquals(2, counter.get());
    }

    @Test
    void createAndAddFlushersCallFlushNotToBeCalled() {
        CollectionFlusher flusher = new CollectionFlusher();
        flusher.addAfterClearFlusher(new MyAfterClearFlusher(counter));
        flusher.addAfterClearFlusher(new MyAfterClearFlusher(counter));
        assertEquals(2, flusher.getAfterClearFlushers().size());
        flusher.flush(false);
        assertEquals(0, counter.get());
    }

    static class Counter {
        private int count = 0;

        public void add() {count++;}

        public int get() {return count;}
    }

    static class MyAfterClearFlusher implements AfterClearFlusher {
        private final Counter counter;

        MyAfterClearFlusher(Counter counter) {
            this.counter = counter;
        }

        @Override
        public void flush() {
            counter.add();
        }
    }

}
