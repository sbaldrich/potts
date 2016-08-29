package com.baldrichcorp.potts.index;

/**
 * The {@code RangeQueryIndex} allows to indexing elements of a given type using a comparable criterion and
 * making queries on these indices.
 *
 * @param <T> The type of the elements to index.
 * @param <K> Comparable criterion for queries.
 *
 * @author Santiago Baldrich.
 */
public interface RangeQueryIndex<T, K extends Comparable<? super K>> {

    /**
     * Add a new element to the index at the given position.
     *
     * @param t   the element to add.
     * @param pos the position of the new element.
     */
    void add(T t, K pos);

    /**
     * Count the number of observations of <em>t</em> that fall within the given range.
     *
     * @param t     the element to look for.
     * @param left  the lower bound of the query.
     * @param right the upper bound of the query.
     * @return the number of observations of <em>t</em> that fall within the range <em>[start,end]</em>.
     */
    int query(T t, K left, K right);

    /**
     * Add up the number of observations of all elements in the index fall within the given range.
     *
     * @param left  the lower bound of the query.
     * @param right the upper bound of the query.
     * @return the sum of all observations of all elements that fall within the range <em>[start,end]</em>.
     */
    int accumulate(K left, K right);

    /**
     * Count the number of distinct elements in the index that have at least one observation that falls within the given range.
     *
     * @param left  the lower bound of the query.
     * @param right the upper bound of the query.
     * @return the number of distinct elements in the index that have at least one observation that falls within the given range.
     */
    int count(K left, K right);
}
