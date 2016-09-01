package com.baldrichcorp.potts.io;

import com.baldrichcorp.potts.index.query.RangeQueryResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


/**
 * The {@code CSVProducer} class provides methods for obtaining a csv file from a {@code Map<String, List<?>>} such as
 * the one returned by the {@link RangeQueryResponse#getResponseMap()} method.
 *
 * @author Santiago Baldrich.
 */
@Slf4j
public class CSVProducer {

    static final String DEFAULT_CSV_DELIMITER = "\t";
    static final String NOT_APPLICABLE = "NA";
    private final String delimiter;

    public CSVProducer(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Initialize a new CSVProducer with the default delimiter. {@value #DEFAULT_CSV_DELIMITER}
     */
    public CSVProducer() {
        this.delimiter = DEFAULT_CSV_DELIMITER;
    }

    /**
     * Write a CSV file from a {@code Map<String, List<?>>} using the defined delimiter.
     *
     * @see RangeQueryResponse#getResponseMap().
     * @param responseMap a Map containing the information to write to the file.
     * @param path the route to write the csv file into.
     */
    @SuppressWarnings("unchecked")
    public void produce(Map<String, List<?>> responseMap, Comparator<String> ordering, String path) {
        try (BufferedWriter writer = new BufferedWriter(Files.newBufferedWriter(Paths.get(path)))) {
            String[] keys = ordering == null ? responseMap.keySet().stream().toArray(String[] :: new) :
                    responseMap.keySet().stream().sorted(ordering).toArray(String[] :: new);
            writer.append(Arrays.stream(keys).collect(Collectors.joining(delimiter, "", "\n")));
            Iterator<String>[] iterators = Arrays.stream(keys).map(responseMap :: get).map(Iterable::iterator).toArray(Iterator[]::new);
            while (iterators[0].hasNext()) {
                writer.append(
                        Arrays.stream(iterators)
                                .map(n -> {
                                    Object next = n.next();
                                    return next == null ? NOT_APPLICABLE : String.valueOf(next);
                                })
                                .collect(Collectors.joining(delimiter, "", "\n")));
            }
        } catch (IOException ex) {
            log.error("Couldn't produce csv", ex);
        }
    }

    public void produce(Map<String, List<?>> responseMap, String path) {
        produce(responseMap, null, path);
    }

}
