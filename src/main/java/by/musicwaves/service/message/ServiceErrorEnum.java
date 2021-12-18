package by.musicwaves.service.message;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Provides values that can be used to get access to localized service error messages
 */
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
    INVALID_ROLE_PARAMETER_VALUE,
    INVALID_ARTIST_NAME,
    INVALID_ALBUM_NAME,
    INVALID_TRACK_NAME;

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
