package by.musicwaves.dao.util;

import java.util.Arrays;

public enum SimilarityType {

    EQUALS(1, "equals", " = ? "),
    CONTAINS(2, "contains", " LIKE ? ");

    int id;
    String propertyKey;
    String sql;

    static {
        // check that there is no repeated ids
        int initialSize = values().length;
        long uniqueIds = Arrays.stream(values())
                .mapToInt(SimilarityType::getId)
                .distinct()
                .count();

        if (initialSize != uniqueIds) {
            throw new RuntimeException("Found non-unique ids in EqualityType enum");
        }
    }

    SimilarityType(int id, String propertyKey, String sql) {
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

    public static SimilarityType getById(int id) {
        return Arrays.stream(values())
                .filter(elem -> id == elem.id)
                .findAny()
                .orElse(SimilarityType.EQUALS);
    }
}
