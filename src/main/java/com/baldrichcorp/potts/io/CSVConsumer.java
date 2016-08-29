package com.baldrichcorp.potts.io;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class allows for getting objects of the given type from a CSV file.
 *
 * @param <T> the type of objects this class can obtain from the file.
 *
 * @author Santiago Baldrich.
 */
@Slf4j
public class CSVConsumer<T> implements Consumer<T> {

    private Iterable<CSVRecord> records;
    private Function<CSVRecord, T> parser;

    /**
     * Initializes a new {@code CSVConsumer} with the given parameters.
     *
     * @param reader    the reader to get the objects from.
     * @param delimiter the delimiter of the source csv file.
     * @param parser    A function to obtain objects of type {@code T} from a {@CSVRecord}.
     */
    public CSVConsumer(Reader reader, char delimiter, Function<CSVRecord, T> parser) {
        this.parser = parser;
        try {
            records = CSVFormat.RFC4180.withDelimiter(delimiter).withFirstRecordAsHeader().parse(reader);
        } catch (IOException ex) {
            log.error("Couldn't load csv file: {}.", ex.getMessage());
        }
    }

    /**
     * Initializes a new {@code CSVConsumer} with the given parameters.
     *
     * @param stream    a stream to get the objects from.
     * @param delimiter the delimiter of the source csv file.
     * @param parser    A function to obtain objects of type {@code T} from a {@CSVRecord}.
     */
    public CSVConsumer(InputStream stream, char delimiter, Function<CSVRecord, T> parser) {
        this(new InputStreamReader(stream), delimiter, parser);
    }

    /**
     * Obtains a Stream of objects of type {@code T} using the provided parsing function.
     * @return a {@code Stream<T>}
     */
    @Override
    public Stream<T> consume() {
        return StreamSupport.stream(records.spliterator(), false).map(parser::apply);
    }

}
