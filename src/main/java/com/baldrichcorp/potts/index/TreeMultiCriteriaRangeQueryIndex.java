package com.baldrichcorp.potts.index;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Recursive implementation of a {@Code MultiCriteriaRangeQueryIndex}.
 * @param <T>
 * @param <K>
 */
@Slf4j
public class TreeMultiCriteriaRangeQueryIndex<T, K extends Comparable<? super K>> implements MultiCriteriaRangeQueryIndex<T, K> {

    private Map<String, Function<T, IndexKeySet>> generators;
    private Map<String, RecursiveRangeQueryIndex<Object, K>> index;

    public TreeMultiCriteriaRangeQueryIndex() {
        index = new ConcurrentHashMap<>();
        generators = new ConcurrentHashMap<>();
    }

    public static void main(String[] args) {
        MultiCriteriaRangeQueryIndex<Person, Integer> index = new TreeMultiCriteriaRangeQueryIndex<>();

        Person jon = Person.of("jon", "anemail");
        Person arya = Person.of("arya", "anemail");

        final String NAME_INDEX = "name";
        final String EMAIL_INDEX = "email";
        final String NAME_PLUS_EMAIL_INDEX = "nameandemail";
        final String EMAIL_PLUS_NAME_INDEX = "emailandname";

        index.define(NAME_INDEX, p -> IndexKeySet.of(p.getName()));
        index.define(EMAIL_INDEX, p -> IndexKeySet.of(p.getEmail()));
        index.define(NAME_PLUS_EMAIL_INDEX, p -> IndexKeySet.of(p.getName(), p.getEmail()));
        index.define(EMAIL_PLUS_NAME_INDEX, p -> IndexKeySet.of(p.getEmail(), p.getName()));

        Instant start = Instant.now();
        IntStream.range(1,10000000).forEach(i -> index.add(jon, i));
        IntStream.range(1,10000000).forEach(i -> index.add(arya, i));
        System.out.println(Duration.between(start, Instant.now()));



        System.out.println(index.query(NAME_INDEX, jon, 1, 4069));
        System.out.println(index.query(NAME_INDEX, arya, 1, 90000));
        System.out.println(index.query(EMAIL_INDEX, arya, 1, 18923));
        System.out.println(index.query(NAME_PLUS_EMAIL_INDEX, jon, 2342, 12351));
        System.out.println(index.query(NAME_PLUS_EMAIL_INDEX, arya, 5000, 48999));

        System.out.println(index.count(EMAIL_PLUS_NAME_INDEX, IndexKeySet.of("anemail"), 1, 4));

    }

    @Override
    public void add(final String indexIdentifier, T t, Function<T, IndexKeySet> keyGenerator, K pos) {
        checkIndexPresent(indexIdentifier);
        generators.put(indexIdentifier, keyGenerator);
        index.put(indexIdentifier, new RecursiveRangeQueryIndex<>());
        index.get(indexIdentifier).add(keyGenerator.apply(t), pos);
    }

    @Override
    public void add(String indexIdentifier, T t, K pos) {
        checkIndexAbsent(indexIdentifier);
        index.get(indexIdentifier).add(generators.get(indexIdentifier).apply(t), pos);
    }

    @Override
    public void add(T t, K pos) {
        generators.keySet().parallelStream().forEach(k -> {
            index.get(k).add(generators.get(k).apply(t), pos);
        });
    }

    @Override
    public void define(String indexIdentifier, Function<T, IndexKeySet> keyGenerator) {
        checkIndexPresent(indexIdentifier);
        generators.put(indexIdentifier, keyGenerator);
        index.put(indexIdentifier, new RecursiveRangeQueryIndex<>());
    }

    @Override
    public int query(final String indexIdentifier, T t, K start, K end) {
        checkIndexAbsent(indexIdentifier);
        return index.get(indexIdentifier).query(generators.get(indexIdentifier).apply(t), start, end);
    }

    @Override
    public int count(String indexIdentifier, T t, K start, K end) {
        checkIndexAbsent(indexIdentifier);
        return index.get(indexIdentifier).count(generators.get(indexIdentifier).apply(t).trim(), start, end);
    }

    @Override
    public int count(String indexIdentifier, IndexKeySet keys , K start, K end) {
        checkIndexAbsent(indexIdentifier);
        return index.get(indexIdentifier).count(keys, start, end);
    }

    @Override
    public int accumulate(String indexIdentifier, T t, K start, K end) {
        checkIndexAbsent(indexIdentifier);
        return index.get(indexIdentifier).count(generators.get(indexIdentifier).apply(t).trim(), start, end);
    }

    @Override
    public int accumulate(String indexIdentifier, IndexKeySet keys, K start, K end) {
        checkIndexAbsent(indexIdentifier);
        return index.get(indexIdentifier).count(keys, start, end);
    }


    private void checkIndexAbsent(String id){
        if(!generators.containsKey(id))
            throw new IllegalArgumentException(
                    String.format("There is no index with identifier '%s'", id));
    }

    private void checkIndexPresent(String id){
        if (generators.containsKey(id)) {
            throw new IllegalStateException(
                    String.format("An index with identifier '%s' has already been defined.", id));
        }
    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class Person {
        private final String name;
        private final String email;
    }
}
