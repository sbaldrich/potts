package com.baldrichcorp.potts.index;

import com.baldrichcorp.potts.index.query.IndexKeySet;

import java.util.HashMap;
import java.util.Map;

/**
 * A Recursive {@code RangeQueryIndex} that allows searching and indexing using an arbitrary number
 * of keys represented by a {@code IndexKeySet}.
 *
 * @param <T> the type of the elements that can be added to the index.
 * @param <K> the type of the criterion used for comparison in queries.
 *
 * @author Santiago Baldrich.
 */
public class MultisetRecursiveRangeQueryIndex<T, K extends Comparable<? super K>> implements RecursiveRangeQueryIndex<T, K> {

    private RangeQueryIndex<Object, K> index = new MultiSetRangeQueryIndex<>();

    private Map<Object, MultisetRecursiveRangeQueryIndex<T, K>> branches = new HashMap<>();

    /**
     * @inheritDoc
     */
    @Override
    public void add(IndexKeySet keys, K pos) {
        if (keys.isLast()) {
            index.add(keys.pop(), pos);
            return;
        }
        branches.putIfAbsent(keys.peek(), new MultisetRecursiveRangeQueryIndex<>());
        branches.get(keys.pop()).add(keys, pos);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int query(IndexKeySet keys, K start, K end) {
        if (keys.isLast()) {
            return index.query(keys.pop(), start, end);
        }
        if (!branches.containsKey(keys.peek()))
            return 0;
        return branches.get(keys.pop()).query(keys, start, end);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int accumulate(IndexKeySet keys, K start, K end) {
        if (!keys.hasNext()) {
            return index.accumulate(start, end);
        }
        if (!branches.containsKey(keys.peek()))
            return 0;
        return branches.get(keys.pop()).accumulate(keys, start, end);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int count(IndexKeySet keys, K start, K end) {
        if (!keys.hasNext()) {
            return index.count(start, end);
        }
        if (!branches.containsKey(keys.peek()))
            return 0;
        return branches.get(keys.pop()).count(keys, start, end);
    }

}
