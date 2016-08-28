package com.baldrichcorp.potts.index;

import java.util.HashMap;
import java.util.Map;

/**
 * A Recursive {@code RangeQueryIndex} that allows searching and indexing using an arbitrary number
 * of keys represented by a {@code IndexKeySet}.
 * @param <T>
 * @param <K>
 */
public class RecursiveRangeQueryIndex<T, K extends Comparable<? super K>> {

    private RangeQueryIndex<Object, K> index = new ListRangeQueryIndex<>();

    private Map<Object, RecursiveRangeQueryIndex<T, K>> branches = new HashMap<>();

    public void add(IndexKeySet keys, K pos) {
        if (keys.isLast()) {
            index.add(keys.pop(), pos);
            return;
        }
        branches.putIfAbsent(keys.peek(), new RecursiveRangeQueryIndex<>());
        branches.get(keys.pop()).add(keys, pos);
    }

    public int query(IndexKeySet keys, K start, K end) {
        if (keys.isLast()) {
            return index.query(keys.pop(), start, end);
        }
        if (!branches.containsKey(keys.peek()))
            return 0;
        return branches.get(keys.pop()).query(keys, start, end);
    }

    public int accumulate(IndexKeySet keys, K start, K end) {
        if (!keys.hasNext()) {
            return index.accumulate(start, end);
        }
        if (!branches.containsKey(keys.peek()))
            return 0;
        return branches.get(keys.pop()).accumulate(keys, start, end);
    }

    public int count(IndexKeySet keys, K start, K end) {
        if (!keys.hasNext()) {
            return index.count(start, end);
        }
        if (!branches.containsKey(keys.peek()))
            return 0;
        return branches.get(keys.pop()).count(keys, start, end);
    }

}
