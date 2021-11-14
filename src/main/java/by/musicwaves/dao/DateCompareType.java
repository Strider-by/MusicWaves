package by.musicwaves.dao;

import java.util.Arrays;

public enum DateCompareType {

    EQUALS(1, "equals", " = ?"),
    BEFORE(2, "before", " < ?"),
    AFTER(3, "after", " > ?");

    int id;
    String propertyKey;
    String sql;

    static {
        // check that there is no repeated ids
        int initialSize = values().length;
        long uniqueIds = Arrays.stream(values())
                .mapToInt(DateCompareType::getId)
                .distinct()
                .count();

        if (initialSize != uniqueIds) {
            throw new RuntimeException("Found non-unique ids in CompareType enum");
        }
    }

    DateCompareType(int id, String propertyKey, String sql) {
        this.id = id;
        this.propertyKey = propertyKey;
        this.sql = sql;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public String getSql() {
        return sql;
    }

    public int getId() {
        return id;
    }

    public static DateCompareType getById(int id) {
        return Arrays.stream(values())
                .filter(elem -> id == elem.id)
                .findAny()
                .orElse(DateCompareType.EQUALS);
    }
}
