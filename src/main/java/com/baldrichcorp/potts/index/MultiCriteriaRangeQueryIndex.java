package com.baldrichcorp.potts.index;

import com.baldrichcorp.potts.index.query.IndexKeySet;
import com.baldrichcorp.potts.index.query.QueryRange;
import com.baldrichcorp.potts.index.query.RangeQueryResponse;

import java.util.List;
import java.util.function.Function;

/**
 * The {@code MultiCriteriaRangeQueryIndex} class provides a way of defining multiple indices for a particular
 * type of objects along with functions that determine the operations to obtain the indexing attributes. Each index and
 * its generator function (the function used to obtain {@code IndexKeySet}s for inner operations) are identified by strings.
 *
 * @param <T> The type of the elements that this index can hold.
 * @param <K> The type criterion used for comparison in queries.
 *
 * @author Santiago Baldrich.
 */
public interface MultiCriteriaRangeQueryIndex<T, K extends Comparable<? super K>> {

    /**
     * Add an element to the sub-index with the given identifier.
     * @param indexIdentifier the identifier of the index to add the element into.
     * @param t the element to add.
     * @param keyGenerator generator function that determines the order and the attributes to extract from <em>t</em>.
     * @param pos the position to add the element into.
     */
    void add(final String indexIdentifier, T t, Function<T, IndexKeySet> keyGenerator, K pos);

    /**
     * Add an element to the sub-index with the given identifier. If the index with the given identifier does not exist,
     * behavior is undefined.
     * @param indexIdentifier the identifier of the index to add the element into.
     * @param t the element to add.
     * @param pos the position to add the element into.
     */
    void add(final String indexIdentifier, T t, K pos);

    /**
     * Add an element to all existing sub-indices.
     * @param t the element to add.
     * @param pos the position to add the element into.
     */
    void add(T t, K pos);

    /**
     * Define a new sub-index, behavior is undefined if the particular identifier has already been used.
     * @param indexIdentifier the identifier of the new sub-index.
     * @param keyGenerator generator function.
     */
    void define(String indexIdentifier, Function<T, IndexKeySet> keyGenerator);

    /**
     * Find the number of occurrences of the given element on the specified index that fall within the given range.
     * @param indexIdentifier the identifier of the sub-index to query.
     * @param t the element to search for.
     * @param start lower bound of the query range.
     * @param end upper bound of the query range.
     * @return the number of occurrences of <em>t</em> that fall in the given range.
     */
    int query(final String indexIdentifier, T t, K start, K end);

    /**
     * Perform a query on multiple ranges and return a {@code RangeQueryResponse} with the results.
     * @param t the object from which the indexing keys will be extracted.
     * @param range the ranges on which to perform the queries.
     * @return A {@code RangeQueryResponse} with the results.
     */
    RangeQueryResponse query(final T t, QueryRange<K>... range);

    /**
     * Perform queries on multiple indexes and multiple ranges (cross product) and return a {@code RangeQueryResponse}
     * with the results.
     *
     * @see #count(String, Object, Comparable, Comparable)
     *
     * @param t the object from which the indexing keys will be extracted.
     * @param indexIds the ids of the indices to query.
     * @param ranges the ranges on which to perform the queries.
     * @return
     */
    RangeQueryResponse query(final T t, List<String> indexIds, List<QueryRange<K>> ranges);

    /**
     * Perform counts on multiple indexes and multiple ranges (cross product) and return a {@code RangeQueryResponse}
     * with the results.
     *
     * @see #count(String, Object, Comparable, Comparable)
     *
     * @param t the object from which the indexing keys will be extracted.
     * @param indexIds the ids of the indices to query.
     * @param ranges the ranges on which to perform the queries.
     * @return
     */
    RangeQueryResponse count(final T t, List<String> indexIds, List<QueryRange<K>> ranges);

    /**
     * Count the number of different instances that fall within the given range and have the longest proper prefix of
     * the keyset obtained from this element as prefix.
     * @param indexIdentifier the identifier of the sub-index.
     * @param t the element from where to obtain the prefix.
     * @param start lower bound of the query range.
     * @param end upper bound of the query range.
     * @return the number of different instances <em>t</em> that fall in the given range.
     */
    int count(final String indexIdentifier, T t, K start, K end);

    /**
     * Perform a count on multiple ranges and return a {@code RangeQueryResponse} with the results.
     * @param t the object from which the indexing keys will be extracted.
     * @param range the ranges on which to perform the queries.
     * @return A {@code RangeQueryResponse} with the results.
     */
    RangeQueryResponse count(final T t, QueryRange<K>... range);

    /**
     * Accumulate all occurrences of from all instances that fall within the given range and have the longest proper prefix of
     * the keyset obtained from this element as a prefix.
     * @param indexIdentifier the identifier of the sub-index.
     * @param keys the keys to use for the search.
     * @param start lower bound of the query range.
     * @param end upper bound of the query range.
     * @return the number of different instances <em>t</em> that fall in the given range.
     */
    int count(final String indexIdentifier, IndexKeySet keys, K start, K end);

    /**
     * Accumulate all occurrences of from all instances that fall within the given range and have the longest proper prefix of
     * the keyset obtained from this element as a prefix.
     * @param indexIdentifier the identifier of the sub-index.
     * @param t the element from where to obtain the prefix.
     * @param start lower bound of the query range.
     * @param end upper bound of the query range.
     * @return the number of different instances <em>t</em> that fall in the given range.
     */
    int accumulate(final String indexIdentifier, T t, K start, K end);

    /**
     * Accumulate all occurrences of from all instances that fall within the given range and have the longest proper prefix of
     * the keyset obtained from this element as a prefix.
     * @param indexIdentifier the identifier of the sub-index.
     * @param keys the keys to use for the search.
     * @param start lower bound of the query range.
     * @param end upper bound of the query range.
     * @return the number of different instances <em>t</em> that fall in the given range.
     */
    int accumulate(final String indexIdentifier, IndexKeySet keys, K start, K end);

}
