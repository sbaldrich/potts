package com.baldrichcorp.potts.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 *
 * @param <T>
 * @param <K>
 */
public class ListRangeQueryIndex<T, K extends Comparable<? super K>> implements RangeQueryIndex<T, K> {

    private Map<T, List<K>> index;

    public ListRangeQueryIndex() {
        this.index = new ConcurrentHashMap<>();
    }

    private static <T> int lowerBound(List<? extends Comparable<? super T>> list, T key) {
        int prev = Collections.binarySearch(list, key);
        int cur = prev;
        while (cur >= 0) {
            cur = Collections.binarySearch(list.subList(0, prev), key);
            if (cur >= 0)
                prev = cur;
        }
        return prev;
    }

    private static <T> int upperBound(List<? extends Comparable<? super T>> list, T key) {
        int prev = Collections.binarySearch(list, key);
        int cur = prev;
        while (cur >= 0) {
            cur = Collections.binarySearch(list.subList(prev + 1, list.size()), key);
            if (cur >= 0)
                prev += cur + 1;
        }
        return prev;
    }

    @Override
    public void add(T t, K pos) {
        index.putIfAbsent(t, new ArrayList<K>());
        index.get(t).add(pos);
    }

    @Override
    public int query(T t, K left, K right) {
        List<K> observations = index.get(t);
        if (observations == null || left.compareTo(right) > 0)
            return 0;
        int low = lowerBound(observations, left);
        int high = upperBound(observations, right);
        boolean hit = high >= 0;
        low = low < 0 ? -(low + 1) : low;
        high = high < 0 ? -(high + 1) : high;
        return high - low + (hit ? 1 : 0);
    }

    public int accumulate(K left, K right) {
        LongAdder total = new LongAdder();
        index.entrySet().parallelStream().forEach(e ->
                total.add(query(e.getKey(), left, right))
        );
        return total.intValue();
    }

    @Override
    public int count(K left, K right) {
        LongAdder total = new LongAdder();
        index.entrySet().parallelStream().forEach(e -> {
            if (query(e.getKey(), left, right) > 0)
                total.increment();
        });
        return total.intValue();
    }

}
