package com.baldrichcorp.potts.index;

import com.baldrichcorp.potts.index.query.IndexKeySet;

/**
 * A Recursive {@code RangeQueryIndex} that allows searching and indexing using an arbitrary number
 * of keys represented by {@code IndexKeySet}s.
 *
 * @param <T> the type of the elements that can be added to the index.
 * @param <K> the type of the criterion used for comparison in queries.
 *
 * @author Santiago Baldrich.
 */
public interface RecursiveRangeQueryIndex<T, K extends Comparable<? super K>> {

    /**
     * Add a new element to the index.
     *
     * @param keys the keys that represent the location of the new element in the index.
     * @param pos  the position of the new element in the index.
     */
    void add(IndexKeySet keys, K pos);

    /**
     * Count the number of observations of <em>t</em> that fall within the given range.
     *
     * @param keys  the keys that represent the element to look for.
     * @param start the lower bound of the query.
     * @param end   the upper bound of the query.
     * @return the number of observations of <em>t</em> that fall within the range <em>[start,end]</em>.
     */
    int query(IndexKeySet keys, K start, K end);

    /**
     * Add up the number of observations of all elements in the index fall within the given range.
     *
     * @param keys  the keys that represent the element to look for
     * @param start the lower bound of the query.
     * @param end   the upper bound of the query.
     * @return the sum of all observations of all elements that fall within the range <em>[start,end]</em>.
     */
    int accumulate(IndexKeySet keys, K start, K end);

    /**
     * Count the number of distinct elements in the index that have at least one observation that falls within the given range.
     *
     * @param keys  the keys that represent the element to look for
     * @param start the lower bound of the query.
     * @param end   the upper bound of the query.
     * @return the number of distinct elements in the index that have at least one observation that falls within the given range.
     */
    int count(IndexKeySet keys, K start, K end);
}