package by.musicwaves.entity.ancillary;

import java.util.Arrays;
import java.util.Locale;

public enum Language {
    DEFAULT("???", "Not defined", 0, Locale.ENGLISH, false),
    BELARUSIAN("БЕЛ", "Беларуская", 1, new Locale("be"), true),
    ENGLISH("ENG", "English", 2, Locale.ENGLISH, true),
    RUSSIAN("РУС", "Русский", 3, new Locale("ru"), true);

    private String shortNativeName;
    private String nativeName;
    private int databaseId;
    private Locale locale;
    private boolean validOption;

    Language(String shortNativeName, String nativeName, int databaseId, Locale locale, boolean validOption) {

        this.shortNativeName = shortNativeName;
        this.nativeName = nativeName;
        this.databaseId = databaseId;
        this.locale = locale;
        this.validOption = validOption;
    }

    // check that used id-s are unique
    static {
        int rolesCount = values().length;
        long uniqueIdCount = Arrays.stream(values())
                .mapToInt(lang -> lang.databaseId)
                .distinct()
                .count();

        if (rolesCount != uniqueIdCount) {
            throw new RuntimeException("Non-unique database id found during Language enum initialization");
        }

    }

    public static Language getByDatabaseId(int id) {
        return Arrays.stream(values())
                .filter(language -> language.databaseId == id)
                .findAny()
                .orElse(Language.DEFAULT);
    }

    public static Language getByNativeName(String name) {
        return Arrays.stream(values())
                .filter(language -> language.nativeName.equalsIgnoreCase(name))
                .findAny()
                .orElse(Language.DEFAULT);
    }

    
    // getters and setters //
    
    public int getDatabaseId() {
        return databaseId;
    }

    public String getShortNativeName() {
        return shortNativeName;
    }

    public void setShortNativeName(String shortNativeName) {
        this.shortNativeName = shortNativeName;
    }

    public String getNativeName() {
        return nativeName;
    }

    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public boolean isValidOption() {
        return validOption;
    }

    public void setValidOption(boolean validOption) {
        this.validOption = validOption;
    }

}
