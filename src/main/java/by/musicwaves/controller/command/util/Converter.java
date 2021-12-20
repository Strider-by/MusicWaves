package by.musicwaves.controller.command.util;

import by.musicwaves.controller.exception.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Utility class that helps to convert requests parameters to required types.
 * If parameter can't be converted, it is considered to be bad and Validation exception is thrown.
 */
public class Converter {

    /**
     * If the param string is null or empty, null will be returned.
     * If string is neither null nor empty but cannot be converted to Integer, ValidationException will be thrown.
     */
    public static Integer toIntegerPossiblyNullOrEmptyString(String s) throws ValidationException {
        if (s == null || s.isEmpty()) {
            return null;
        } else {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ex) {
                throw new ValidationException("Value [" + s + "] can not be converted to Integer", ex);
            }
        }
    }

    /**
     * If the param string is null or empty, null will be returned.
     * If string is neither null nor empty but cannot be converted to LocalDate, ValidationException will be thrown.
     */
    public static LocalDate toLocalDatePossiblyNullOrEmptyString(String s) throws ValidationException {
        if (isNullOrEmpty(s)) {
            return null;
        } else {
            try {
                return LocalDate.parse(s);
            } catch (DateTimeParseException ex) {
                throw new ValidationException("Value [" + s + "] can not be converted to LocalDate object", ex);
            }
        }
    }

    public static String toNullIfEmpty(String s) {
        return isNullOrEmpty(s) ? null : s;
    }

    /**
     * Converts given string parameter to an int value. If it can't be done, parameter is judged as an invalid one
     * and ValidationException is thrown.
     *
     * @param s string to be converted to int
     * @return int representation of the given string parameter
     * @throws ValidationException if string parameter cannot be converted to int
     */
    public static int toInt(String s) throws ValidationException {
        try {
            return Integer.parseInt(s);
        } catch (NullPointerException | NumberFormatException ex) {
            throw new ValidationException("Provided parameter [" + s + "] can't be converted to int", ex);
        }
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
