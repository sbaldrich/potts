package com.baldrichcorp.potts.index.query;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a range of interest for querying a {@code RangeQueryIndex}.
 *
 * @author Santiago Baldrich
 */
@ToString
@Getter
@Slf4j
public class QueryRange<K extends Comparable<? super K>> {

    final K start;
    final K end;
    final String name;

    /**
     * Initializes a newly created {@code QueryRange} with the given parameters as attributes.
     * The optional name is commonly used to adequately display the results of batch queries.
     *
     * @param name  the name of the range, if null one will be provided using the start and end limits.
     * @param start lower bound of the query range.
     * @param end   upper bound of the query range.
     */
    private QueryRange(String name, K start, K end) {
        this.name = name == null ? String.format("[%s-%s]", start, end) : name;
        this.start = start;
        this.end = end;
    }

    /**
     * Create a new QueryRange from the given arguments.
     *
     * @param name  the name of the range, if null one will be provided using the start and end limits.
     * @param start lower bound of the query range.
     * @param end   upper bound of the query range.
     * @param <K>   the type of the elements that conform the lower and upper bounds. Must implement {@code Comparable}.
     * @return a QueryRange object with the given parameters as attributes.
     */
    public static <K extends Comparable<? super K>> QueryRange<K> of(String name, K start, K end) {
        return new QueryRange<>(name, start, end);
    }

    /**
     * Create a new QueryRange from the given arguments. The name is automatically constructed using the lower and
     * upper bounds of the range.
     *
     * @param start lower bound of the query range.
     * @param end   upper bound of the query range.
     * @param <K>   the type of the elements that conform the lower and upper bounds. Must implement {@code Comparable}.
     * @return a QueryRange object with the given parameters as attributes.
     */
    public static <K extends Comparable<? super K>> QueryRange<K> of(K start, K end) {
        return new QueryRange<>(null, start, end);
    }
}