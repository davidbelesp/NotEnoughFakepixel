package com.nef.notenoughfakepixel.utils;

import java.util.*;

public class ListUtils {

    private ListUtils() {}

    public static <E> List<E> of() {
        return Collections.emptyList(); // unmodifiable
    }

    @SafeVarargs
    public static <E> List<E> of(E... elements) {
        Objects.requireNonNull(elements, "elements");
        switch (elements.length) {
            case 0:
                return Collections.emptyList();
            case 1:
                return Collections.singletonList(
                        Objects.requireNonNull(elements[0], "element")
                );
            default:
                // reject nulls and copy so callers can't mutate backing storage
                ArrayList<E> copy = new ArrayList<>(elements.length);
                for (int i = 0; i < elements.length; i++) {
                    copy.add(Objects.requireNonNull(elements[i], "element " + i));
                }
                return Collections.unmodifiableList(copy);
        }
    }

    public static <E> List<E> copyOf(Collection<? extends E> c) {
        Objects.requireNonNull(c, "collection");
        if (c.isEmpty()) return Collections.emptyList();
        ArrayList<E> copy = new ArrayList<>(c.size());
        for (E e : c) copy.add(Objects.requireNonNull(e, "element"));
        return Collections.unmodifiableList(copy);
    }

}
