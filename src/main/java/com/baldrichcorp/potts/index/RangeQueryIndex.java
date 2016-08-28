package com.baldrichcorp.potts.index;

/**
 * The {@code RangeQueryIndex} allows to indexing elements of a given type using a comparable criterion and
 * making queries on these indices.
 * @param <T> The type of the elements to index.
 * @param <K> Comparable criterion for queries.
 */
public interface RangeQueryIndex<T, K extends Comparable<? super K>> {

    void add(T t, K pos);

    int query(T t, K left, K right);

    int accumulate(K left, K right);

    int count(K left, K right);
}
