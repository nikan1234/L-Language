package ru.nsu.logic.lang.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class FileLocation implements Comparable<FileLocation> {
    @Getter
    private final int row;
    @Getter
    private final int column;

    public FileLocation(final int row, final int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public int compareTo(final FileLocation other) {
        if (row < other.row)
            return -1;
        if (row > other.row)
            return 1;
        return Integer.compare(column, other.column);
    }

    @Override
    public String toString() {
        return row + ":" + column;
    }
}
