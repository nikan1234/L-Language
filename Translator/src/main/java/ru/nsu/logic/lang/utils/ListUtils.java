package ru.nsu.logic.lang.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    public static <T> List<T> cloneAndSet(final List<T> list, final T value, final int index) {
        final List<T> copy = new ArrayList<>(list);
        copy.set(index, value);
        return copy;
    }
}
