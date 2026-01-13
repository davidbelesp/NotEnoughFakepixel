package com.nef.notenoughfakepixel.utils;

import java.util.*;

/**
 * Utility class providing static factory methods for creating unmodifiable lists.
 * <p>
 * This class serves as a Java 8 compatible backport of {@code List.of()} and
 * {@code List.copyOf()} features introduced in later Java versions.
 * <p>
 * <b>Key Behaviors:</b>
 * <ul>
 * <li><b>Unmodifiable:</b> The returned lists cannot be mutated (added to, removed from, or replaced).</li>
 * <li><b>Null-Hostile:</b> Attempting to create a list containing {@code null} elements will throw a {@link NullPointerException}.</li>
 * <li><b>Optimization:</b> Primitive arrays are handled via manual loops rather than Streams for better performance.</li>
 * </ul>
 */
public class ListUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private ListUtils() {}

    /**
     * Returns an unmodifiable list containing zero elements.
     *
     * @param <E> the {@code List}'s element type
     * @return an empty {@code List}
     */
    public static <E> List<E> of() {
        return Collections.emptyList();
    }

    /**
     * Returns an unmodifiable list containing an arbitrary number of elements.
     *
     * @param <E> the {@code List}'s element type
     * @param elements the elements to be contained in the list
     * @return an unmodifiable {@code List} containing the specified elements
     * @throws NullPointerException if the array is null or contains any null elements
     */
    @SafeVarargs
    public static <E> List<E> of(E... elements) {
        Objects.requireNonNull(elements, "elements array cannot be null");
        switch (elements.length) {
            case 0:
                return Collections.emptyList();
            case 1:
                return Collections.singletonList(
                        Objects.requireNonNull(elements[0], "element at index 0 cannot be null")
                );
            default:
                ArrayList<E> list = new ArrayList<>(elements.length);
                for (int i = 0; i < elements.length; i++) {
                    list.add(Objects.requireNonNull(elements[i], "element at index " + i + " cannot be null"));
                }
                return Collections.unmodifiableList(list);
        }
    }

    /**
     * Returns an unmodifiable List containing the elements of the given Collection.
     * <p>
     * The given Collection must not be null, and must not contain any null elements.
     * If the given Collection is subsequently modified, the returned List will not reflect such modifications.
     *
     * @param <E> the {@code List}'s element type
     * @param c a {@code Collection} from which elements are drawn, must be non-null
     * @return an unmodifiable {@code List} containing the elements of the given Collection
     * @throws NullPointerException if the collection is null or contains any null elements
     */
    public static <E> List<E> copyOf(Collection<? extends E> c) {
        Objects.requireNonNull(c, "collection cannot be null");
        if (c.isEmpty()) return Collections.emptyList();
        ArrayList<E> list = new ArrayList<>(c);
        for (E element : list) {
            if (element == null) throw new NullPointerException("collection contains null elements");
        }

        return Collections.unmodifiableList(list);
    }

    /**
     * Returns an unmodifiable List containing the elements of the given array.
     *
     * @param <E> the {@code List}'s element type
     * @param array the array to copy elements from, must be non-null
     * @return an unmodifiable {@code List} containing the elements of the given array
     * @throws NullPointerException if the array is null or contains any null elements
     */
    public static <E> List<E> copyOf(E[] array) {
        return of(array);
    }

    /**
     * Returns an unmodifiable list containing the elements of the given int array.
     * <p>
     * The elements are autoboxed into {@code Integer} objects.
     *
     * @param elements the int array to convert
     * @return an unmodifiable {@code List<Integer>} (empty if input is null or empty)
     */
    public static List<Integer> of(int[] elements) {
        if (elements == null || elements.length == 0) return Collections.emptyList();

        // OPTIMIZATION: Manual loop is ~3x faster than Arrays.stream().boxed()
        ArrayList<Integer> list = new ArrayList<>(elements.length);
        for (int e : elements) {
            list.add(e); // Autoboxing
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Returns an unmodifiable list containing the elements of the given long array.
     * <p>
     * The elements are autoboxed into {@code Long} objects.
     *
     * @param elements the long array to convert
     * @return an unmodifiable {@code List<Long>} (empty if input is null or empty)
     */
    public static List<Long> of(long[] elements) {
        if (elements == null || elements.length == 0) return Collections.emptyList();

        ArrayList<Long> list = new ArrayList<>(elements.length);
        for (long e : elements) {
            list.add(e);
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Returns an unmodifiable list containing the elements of the given double array.
     * <p>
     * The elements are autoboxed into {@code Double} objects.
     *
     * @param elements the double array to convert
     * @return an unmodifiable {@code List<Double>} (empty if input is null or empty)
     */
    public static List<Double> of(double[] elements) {
        if (elements == null || elements.length == 0) return Collections.emptyList();

        ArrayList<Double> list = new ArrayList<>(elements.length);
        for (double e : elements) {
            list.add(e);
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Returns an unmodifiable list containing the elements of the given boolean array.
     * <p>
     * The elements are autoboxed into {@code Boolean} objects.
     *
     * @param elements the boolean array to convert
     * @return an unmodifiable {@code List<Boolean>} (empty if input is null or empty)
     */
    public static List<Boolean> of(boolean[] elements) {
        if (elements == null || elements.length == 0) return Collections.emptyList();

        ArrayList<Boolean> list = new ArrayList<>(elements.length);
        for (boolean e : elements) {
            list.add(e);
        }
        return Collections.unmodifiableList(list);
    }

}
