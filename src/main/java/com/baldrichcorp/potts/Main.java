package com.baldrichcorp.potts;

import com.baldrichcorp.potts.index.MapMultiCriteriaRangeQueryIndex;
import com.baldrichcorp.potts.index.MultiCriteriaRangeQueryIndex;
import com.baldrichcorp.potts.index.query.IndexKeySet;
import com.baldrichcorp.potts.index.query.QueryRange;
import com.baldrichcorp.potts.io.CSVConsumer;
import com.baldrichcorp.potts.io.CSVProducer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        MultiCriteriaRangeQueryIndex<Person, Integer> index = new MapMultiCriteriaRangeQueryIndex<>();

        CSVConsumer<Person> consumer = new CSVConsumer<>(ClassLoader.getSystemClassLoader().getResourceAsStream("persons.csv"),
                ',', record -> Person.of(record.get("name"),
                record.get("email"),
                Integer.valueOf(record.get("pos"))));

        final String NAME_INDEX = "Na";
        final String EMAIL_INDEX = "Em";
        final String NAME_PLUS_EMAIL_INDEX = "NaEm";
        final String EMAIL_PLUS_NAME_INDEX = "EmNa";

        index.define(NAME_INDEX, p -> IndexKeySet.of(p.getName()));
        index.define(EMAIL_INDEX, p -> IndexKeySet.of(p.getEmail()));
        index.define(NAME_PLUS_EMAIL_INDEX, p -> IndexKeySet.of(p.getName(), p.getEmail()));
        index.define(EMAIL_PLUS_NAME_INDEX, p -> IndexKeySet.of(p.getEmail(), p.getName()));

        Instant start = Instant.now();
        consumer.consume().forEach(p -> index.add(p, p.pos));
        System.out.println("Indexed = " + Duration.between(start, Instant.now()));
        start = Instant.now();

        CSVConsumer<Person> queries = new CSVConsumer<>(ClassLoader.getSystemClassLoader().getResourceAsStream("persons.csv"),
                ',', record -> Person.of(record.get("name"),
                record.get("email"),
                Integer.valueOf(record.get("pos"))));

        Map<String, List<?>> response = new HashMap<>();

        List<QueryRange<Integer>> relevantRanges = Arrays.asList(QueryRange.of(1, 10), QueryRange.of(4, 4), QueryRange.of(2, 4));

        List<String> relevantIndices = Arrays.asList(NAME_INDEX, EMAIL_INDEX);
        response.putAll(
                queries.consume()
                        .map(p -> index.query(p, relevantIndices, relevantRanges).getResponseMap())
                        .flatMap(m -> m.entrySet().stream())
                        .collect(Collectors
                                .groupingBy(Map.Entry::getKey,
                                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
        );

        queries = new CSVConsumer<>(ClassLoader.getSystemClassLoader().getResourceAsStream("persons.csv"),
                ',', record -> Person.of(record.get("name"),
                record.get("email"),
                Integer.valueOf(record.get("pos"))));

        response.putAll(
                queries.consume()
                        .map(p -> index.count(p, Arrays.asList(NAME_PLUS_EMAIL_INDEX), relevantRanges).getResponseMap())
                        .flatMap(m -> m.entrySet().stream())
                        .collect(Collectors
                                .groupingBy(Map.Entry::getKey,
                                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
        );

        System.out.printf("Queried = %s\n", Duration.between(start, Instant.now()));
        new CSVProducer().produce(response, "output.csv");
    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class Person {
        private final String name;
        private final String email;
        private final int pos;
    }
}
