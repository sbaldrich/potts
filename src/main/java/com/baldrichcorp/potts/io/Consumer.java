package com.baldrichcorp.potts.io;

import java.util.stream.Stream;

/**
 * Simple interface to mark classes that can create a Stream of objects of a given type.
 * @param <T> The type of objects that can be obtained from the implementing class.
 *
 * @author Santiago Baldrich.
 */
public interface Consumer<T> {
    Stream<T> consume();
}
