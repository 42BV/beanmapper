package io.beanmapper.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.config.BeanPair;
import io.beanmapper.core.inspector.BeanPropertySelector;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;

import org.junit.jupiter.api.Test;

class BeanMatchStoreConcurrentTest {

    private static final int THREADS = 32;
    private static final int DISTINCT_SOURCE_CLASSES = 48;
    private static final int COLD_CACHE_ROUNDS = 20;
    private static final int MAPPING_ROUNDS = 8;

    @Test
    void shouldNotThrowWhenGettingBeanMatchesConcurrently() throws Exception {
        List<BeanPair> pairs = generatedPairs(DISTINCT_SOURCE_CLASSES);

        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        try {
            for (int round = 0; round < COLD_CACHE_ROUNDS; round++) {
                BeanMatchStore store = new BeanMatchStore(null, null, new BeanPropertySelector());
                CountDownLatch start = new CountDownLatch(1);
                List<Future<BeanMatch>> futures = new ArrayList<>();
                for (int thread = 0; thread < THREADS; thread++) {
                    for (BeanPair pair : pairs) {
                        futures.add(pool.submit(awaitAndGetMatch(start, store, pair)));
                    }
                }
                start.countDown();
                for (Future<BeanMatch> future : futures) {
                    assertNotNull(future.get());
                }
                for (BeanPair pair : pairs) {
                    assertSame(store.getBeanMatch(pair), store.getBeanMatch(pair));
                }
            }
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void shouldNotThrowWhenMappingNestedCollectionsConcurrently() throws Exception {
        List<Note> notes = List.of(note("alpha"), note("beta"), note("gamma"));

        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        try {
            for (int round = 0; round < MAPPING_ROUNDS; round++) {
                BeanMapper mapper = new BeanMapperBuilder()
                        .addPackagePrefix(BeanMapper.class)
                        .setApplyStrictMappingConvention(false)
                        .build();
                CountDownLatch start = new CountDownLatch(1);
                List<Future<List<NoteResult>>> futures = new ArrayList<>();
                for (int thread = 0; thread < THREADS; thread++) {
                    futures.add(pool.submit(() -> {
                        start.await();
                        return mapper.map(notes, NoteResult.class);
                    }));
                }
                start.countDown();
                for (Future<List<NoteResult>> future : futures) {
                    List<NoteResult> result = future.get();
                    assertEquals(3, result.size());
                    assertEquals("alpha", result.getFirst().text);
                    assertEquals("author-alpha", result.getFirst().author.name);
                    assertEquals(2, result.getFirst().author.tags.size());
                }
            }
        } finally {
            pool.shutdownNow();
        }
    }

    private static List<BeanPair> generatedPairs(int count) throws Exception {
        ClassPool classPool = new ClassPool(true);
        List<BeanPair> pairs = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            CtClass source = classPool.makeClass("io.beanmapper.core.GeneratedSource" + i);
            source.addField(CtField.make("public java.lang.String value;", source));
            CtClass target = classPool.makeClass("io.beanmapper.core.GeneratedTarget" + i);
            target.addField(CtField.make("public java.lang.String value;", target));
            pairs.add(new BeanPair(
                    source.toClass(BeanMatchStoreConcurrentTest.class),
                    target.toClass(BeanMatchStoreConcurrentTest.class)));
        }
        return pairs;
    }

    private static Callable<BeanMatch> awaitAndGetMatch(CountDownLatch start, BeanMatchStore store, BeanPair pair) {
        return () -> {
            start.await();
            return store.getBeanMatch(pair);
        };
    }

    private static Note note(String text) {
        Note note = new Note();
        note.text = text;
        note.author = new Author();
        note.author.name = "author-" + text;
        note.author.tags = new LinkedHashSet<>(List.of(tag(text + "-1"), tag(text + "-2")));
        return note;
    }

    private static Tag tag(String label) {
        Tag tag = new Tag();
        tag.label = label;
        return tag;
    }

    public static class Note {
        public String text;
        public Author author;
    }

    public static class Author {
        public String name;
        public Set<Tag> tags;
    }

    public static class Tag {
        public String label;
    }

    public static class NoteResult {
        public String text;
        public AuthorResult author;
    }

    public static class AuthorResult {
        public String name;
        public Set<TagResult> tags;
    }

    public static class TagResult {
        public String label;
    }
}
