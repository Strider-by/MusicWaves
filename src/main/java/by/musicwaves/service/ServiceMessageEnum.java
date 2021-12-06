package by.musicwaves.service;

import java.util.Locale;
import java.util.ResourceBundle;

public enum ServiceMessageEnum {

    LOGIN_IS_AVAILABLE,
    LOGIN_IS_NOT_AVAILABLE;

    private static String resourceBundleBasename = "internationalization.messages";
    private static Locale defaultLocale = Locale.ENGLISH;


    public String getMessage(Locale locale) {
        locale = locale == null ? defaultLocale : locale;
        return ResourceBundle.getBundle(resourceBundleBasename, locale).getString(this.name().toLowerCase());
    }
}
