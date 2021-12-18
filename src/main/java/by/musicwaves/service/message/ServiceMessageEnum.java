package by.musicwaves.service.message;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Provides values that can be used to get access to localized service messages
 */
public enum ServiceMessageEnum {

    LOGIN_IS_AVAILABLE,
    LOGIN_IS_NOT_AVAILABLE,
    REGISTRATION_IS_COMPLETED,
    ACCOUNT_HAS_BEEN_DELETED;

    private static final String resourceBundleBasename = "internationalization.messages";
    private static final Locale defaultLocale = Locale.ENGLISH;


    public String getMessage(Locale locale) {
        locale = locale == null ? defaultLocale : locale;
        return ResourceBundle.getBundle(resourceBundleBasename, locale).getString(this.name().toLowerCase());
    }
}
