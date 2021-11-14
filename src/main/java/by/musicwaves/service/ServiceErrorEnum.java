package by.musicwaves.service;

import java.util.Locale;
import java.util.ResourceBundle;

public enum ServiceErrorEnum {

    LOGIN_DOES_NOT_FIT_LIMITATIONS,
    INVALID_PASSWORD,
    PASSWORD_DOES_NOT_FIT_LIMITATIONS,
    LOGIN_ALREADY_IN_USE,
    INVALID_LOGIN_CREDENTIALS,
    INVALID_INVITE_CODE,
    PASSWORDS_ARE_NOT_EQUAL,
    FAILED_TO_CREATE_USER_USING_PROVIDED_LOGIN,
    UNKNOWN_LANGUAGE_ID,
    INVALID_ROLE_PARAMETER_VALUE;

    private static String resourceBundleBasename = "internationalization.errors";
    private static Locale defaultLocale = Locale.ENGLISH;


    public String getMessage(Locale locale) {
        locale = locale == null ? defaultLocale : locale;
        return ResourceBundle.getBundle(resourceBundleBasename, locale).getString(this.name().toLowerCase());
    }

    public String getErrorCode() {
        return this.name();
    }
}
