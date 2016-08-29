package com.baldrichcorp.potts.index;

import com.baldrichcorp.potts.index.query.IndexKeySet;
import com.baldrichcorp.potts.index.query.QueryRange;
import com.baldrichcorp.potts.index.query.RangeQueryResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Map-based implementation of a {@code MultiCriteriaRangeQueryIndex}.
 *
 * @see MultisetRecursiveRangeQueryIndex
 * @param <T> the type of the elements that can be handled by this index.
 * @param <K> the type of the criterion used for comparison in queries.
 *
 * @author Santiago Baldrich.
 */
@Slf4j
public class MapMultiCriteriaRangeQueryIndex<T, K extends Comparable<? super K>> implements MultiCriteriaRangeQueryIndex<T, K> {

    private Map<String, Function<T, IndexKeySet>> generators;
    private Map<String, MultisetRecursiveRangeQueryIndex<Object, K>> index;

    public MapMultiCriteriaRangeQueryIndex() {
        index = new ConcurrentHashMap<>();
        generators = new ConcurrentHashMap<>();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void add(final String indexIdentifier, T t, Function<T, IndexKeySet> keyGenerator, K pos) {
        checkIndexPresent(indexIdentifier);
        generators.put(indexIdentifier, keyGenerator);
        index.put(indexIdentifier, new MultisetRecursiveRangeQueryIndex<>());
        index.get(indexIdentifier).add(keyGenerator.apply(t), pos);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void add(String indexIdentifier, T t, K pos) {
        checkIndexAbsent(indexIdentifier);
        index.get(indexIdentifier).add(generators.get(indexIdentifier).apply(t), pos);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void add(T t, K pos) {
        generators.keySet().parallelStream().forEach(k -> {
            index.get(k).add(generators.get(k).apply(t), pos);
        });
    }

    /**
     * @inheritDoc
     */
    @Override
    public void define(String indexIdentifier, Function<T, IndexKeySet> keyGenerator) {
        checkIndexPresent(indexIdentifier);
        generators.put(indexIdentifier, keyGenerator);
        index.put(indexIdentifier, new MultisetRecursiveRangeQueryIndex<>());
    }

    /**
     * @inheritDoc
     */
    @Override
    public int query(final String indexIdentifier, T t, K start, K end) {
        checkIndexAbsent(indexIdentifier);
        return index.get(indexIdentifier).query(generators.get(indexIdentifier).apply(t), start, end);
    }

    /**
     * @inheritDoc
     */
    @Override
    public RangeQueryResponse query(T t, QueryRange<K>... ranges) {
        return Stream.of(ranges).map(range -> {
            RangeQueryResponse response = new RangeQueryResponse(range);
            generators.keySet().stream().forEach(k -> response.add(
                    k, index.get(k).query(generators.get(k).apply(t), range.getStart(), range.getEnd())
            ));
            return response;
        }).reduce(RangeQueryResponse::merge).orElse(null);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int count(String indexIdentifier, T t, K start, K end) {
        checkIndexAbsent(indexIdentifier);
        return index.get(indexIdentifier).count(generators.get(indexIdentifier).apply(t).drop(), start, end);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int count(String indexIdentifier, IndexKeySet keys, K start, K end) {
        checkIndexAbsent(indexIdentifier);
        return index.get(indexIdentifier).count(keys, start, end);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int accumulate(String indexIdentifier, T t, K start, K end) {
        checkIndexAbsent(indexIdentifier);
        return index.get(indexIdentifier).count(generators.get(indexIdentifier).apply(t).drop(), start, end);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int accumulate(String indexIdentifier, IndexKeySet keys, K start, K end) {
        checkIndexAbsent(indexIdentifier);
        return index.get(indexIdentifier).count(keys, start, end);
    }

    /**
     * Check whether an index with the given id is absent and throw an {@code IllegalArgumentException} if true.
     * @param id
     */
    private void checkIndexAbsent(String id) {
        if (!generators.containsKey(id))
            throw new IllegalArgumentException(
                    String.format("There is no index with identifier '%s'", id));
    }

    /**
     * Check whether an index with the given id has already been defined and throw an {@code IllegalStateException} if true.
     * @param id
     */
    private void checkIndexPresent(String id) {
        if (generators.containsKey(id)) {
            throw new IllegalStateException(
                    String.format("An index with identifier '%s' has already been defined.", id));
        }
    }

}