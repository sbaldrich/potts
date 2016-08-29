package com.baldrichcorp.potts.index.query;

/**
 * Class {@code IndexKeySet} represents a set of keys that can be used
 * to traverse an index for querying or adding new elements. A key can be constituted by
 * instances of any class, therefore all methods return an Object reference. This shouldn't represent a problem
 * since all indexes must be able to handle them properly.
 * <p>
 * The KeySet works as a wrapper for an {@code Object} array and pointers to the start and end of the array. Each operation
 * works by moving these indices around.
 *
 * @author Santiago Baldrich.
 */
public class IndexKeySet {

    int left;
    int right;
    private Object[] keys;

    private IndexKeySet(Object[] keys) {
        this.keys = keys;
        this.left = 0;
        this.right = keys.length;
    }

    public static IndexKeySet of(Object... keys) {
        return new IndexKeySet(keys);
    }

    /**
     * Get and remove the next key.
     *
     * @return the Object that represents the next key.
     */
    public Object pop() {
        return hasNext() ? keys[left++] : null;
    }

    /**
     * Get the next key without removing it.
     *
     * @return the Object that represents the next key.
     */
    public Object peek() {
        return keys[left];
    }

    /**
     * Remove the last key from the key set.
     *
     * @return this instance.
     */
    public IndexKeySet drop() {
        return drop(1);
    }

    /**
     * Remove the last n keys from the key set.
     *
     * @param n the number of keys to drop.
     * @return this instance.
     */
    public IndexKeySet drop(int n) {
        right--;
        return this;
    }

    /**
     * Returns a boolean that represents whether there are more
     * keys left to extract.
     *
     * @return {@code true} if there are more keys left, {@code false} otherwise.
     */
    public boolean hasNext() {
        return right > left;
    }

    /**
     * Returns a boolean that indicated whether this is the last
     * key left for extraction.
     *
     * @return {@code true} if this is the last key, {@code false} otherwise.
     */
    public boolean isLast() {
        return right - left == 1;
    }

}