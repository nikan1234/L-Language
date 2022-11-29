package ru.nsu.logic.lang.compilator;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public class LLangProgram {
    public interface Unit {}

    @AllArgsConstructor
    public static class SingleUnit implements Unit{
        @Getter
        @Setter
        private Object value;

        @Override
        public String toString() {
            return value.toString();
        }
    }

    @AllArgsConstructor
    public static class ListUnit implements Unit {
        @Setter
        @Getter
        List<Unit> children;

        @Override
        public String toString() {
            String childrenStr = children.stream().map(Unit::toString).collect(Collectors.joining(", "));
            return "(" + childrenStr + ")";
        }
    }

    @Getter
    private final ListUnit classes;
    @Getter
    private final ListUnit functions;
}
