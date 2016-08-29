package com.baldrichcorp.potts.index.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
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
    private final QueryRange range;
    private Map<String, Integer> response = new ConcurrentHashMap<>();

    public RangeQueryResponse add(String indexId, int answer) {
        this.response.put(responseKey(range, indexId), answer);
        return this;
    }

    public RangeQueryResponse get(String indexId) {
        this.response.get(indexId);
        return this;
    }

    private String responseKey(QueryRange range, String indexId){
        return String.format("%s_%s", indexId, range.getName());
    }

    public RangeQueryResponse merge(RangeQueryResponse that){
        RangeQueryResponse merged = new RangeQueryResponse(null);
        merged.response = new ConcurrentHashMap<>(this.response);
        merged.response.putAll(that.response);
        return merged;
    }

    public Map<String, Integer> getResponseMap(){
        return Collections.unmodifiableMap(response);
    }
}
