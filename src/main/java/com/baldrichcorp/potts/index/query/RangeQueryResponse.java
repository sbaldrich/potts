package com.baldrichcorp.potts.index.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class represents the response to a query made to a {@code RangeQueryIndex}, it is most useful when used in
 * batch or complex queries (queries made to multiple sub-indices on a {@code MultiCriteriaRangeQueryIndex}, for example.
 *
 * @author Santiago Baldrich
 */
@ToString
@RequiredArgsConstructor
@Getter
@Slf4j
public class RangeQueryResponse {

    private final QueryType type;
    private Map<Entry<String, QueryRange>, Integer> response = new ConcurrentHashMap<>();

    public RangeQueryResponse add(String indexId, QueryRange range, int answer) {
        this.response.put(new SimpleEntry<>(indexId, range), answer);
        return this;
    }

    public Integer get(String indexId, QueryRange range) {
        return this.response.get(new SimpleEntry<>(indexId, range));
    }

    public RangeQueryResponse merge(RangeQueryResponse that) {
        RangeQueryResponse merged = new RangeQueryResponse(this.type);
        merged.response = new ConcurrentHashMap<>(this.response);
        merged.response.putAll(that.response);
        return merged;
    }

    public Map<String, Integer> getResponseMap() {
        Map<String, Integer> responseMap = new HashMap<>();
        response.entrySet().stream().forEach(e -> responseMap.put(
                String.format("%s_%s_%s", e.getKey().getKey(),
                        this.type, e.getKey().getValue().getName()), e.getValue()));
        return responseMap;
    }

    public enum QueryType {
        JOINT("J"), COMBINATION("C");

        private final String symbol;

        QueryType(String symbol) {
            this.symbol = symbol;
        }

        public String toString() {
            return this.symbol;
        }
    }
}
