package by.musicwaves.dao.util;

import java.util.Arrays;

public enum SortOrder {

    ASCENDING(1, "asc", " ASC "),
    DESCENDING(2, "desc", " DESC ");

    static {
        // check that there is no repeated ids
        int initialSize = values().length;
        long uniqueIds = Arrays.stream(values())
                .mapToInt(SortOrder::getId)
                .distinct()
                .count();

        if (initialSize != uniqueIds) {
            throw new RuntimeException("Found non-unique ids in SortOrder enum");
        }
    }

    int id;
    String propertyKey;
    String sqlEquivalent;

    SortOrder(int id, String propertyKey, String sqlEquivalent) {
        this.id = id;
        this.propertyKey = propertyKey;
        this.sqlEquivalent = sqlEquivalent;
    }

    public static SortOrder getById(int id) {
        return Arrays.stream(values())
                .filter(elem -> id == elem.id)
                .findAny()
                .orElse(SortOrder.ASCENDING);
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public String getSqlEquivalent() {
        return sqlEquivalent;
    }

    public int getId() {
        return id;
    }
}
