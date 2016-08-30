package com.baldrichcorp.potts.index;

import com.google.common.collect.BoundType;
import com.google.common.collect.TreeMultiset;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * Implementation of a {@code RangeQueryIndex} based on MultiSets.
 *
 * @param <T> The type of the elements that this index can hold.
 * @param <K> The type of the criterion used for comparison in queries.
 * @author Santiago Baldrich
 */
public class MultiSetRangeQueryIndex<T, K extends Comparable<? super K>> implements RangeQueryIndex<T, K> {

    private Map<T, TreeMultiset<K>> index;
    public MultiSetRangeQueryIndex() {
        this.index = new HashMap<>();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void add(T t, K pos) {
        index.putIfAbsent(t, TreeMultiset.<K> create());
        index.get(t).add(pos);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int query(T t, K left, K right) {
        TreeMultiset<K> observations = index.get(t);
        if (observations == null || left.compareTo(right) > 0)
            return 0;
        return observations.subMultiset(left, BoundType.CLOSED, right, BoundType.CLOSED).size();
    }

    /**
     * @inheritDoc
     */
    @Override
    public int accumulate(K left, K right) {
        LongAdder total = new LongAdder();
        index.entrySet().parallelStream().forEach(e ->
                total.add(query(e.getKey(), left, right))
        );
        return total.intValue();
    }

    /**
     * @inheritDoc
     */
    @Override
    public int count(K left, K right) {
        return index.keySet().stream().mapToInt(k -> query(k, left, right) > 0 ? 1 : 0).sum();
    }
}
