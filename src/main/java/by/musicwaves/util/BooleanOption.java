package by.musicwaves.util;

import java.util.Arrays;

public enum BooleanOption {

    UNKNOWN_OPTION(-1, null, null, false),
    EMPTY_OPTION(0, "empty_value", null, true),
    POSITIVE_OPTION(1, "yes", true, true),
    NEGATIVE_OPTION(2, "no", false, true);

    private int id;
    private String propertyKey;
    Boolean value;
    boolean validTagOption;

    BooleanOption(int id, String propertyKey, Boolean value, boolean validTagOption) {
        this.propertyKey = propertyKey;
        this.id = id;
        this.value = value;
        this.validTagOption = validTagOption;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public int getId() {
        return id;
    }

    public Boolean getValue() {
        return value;
    }

    public boolean isValidTagOption() {
        return validTagOption;
    }

    public static BooleanOption getById(int id) {
        return Arrays.stream(values())
                .filter(option -> id == option.id)
                .findAny()
                .orElse(UNKNOWN_OPTION);
    }
}
