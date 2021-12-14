package by.musicwaves.controller.command.util;

import by.musicwaves.controller.command.exception.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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

    public static LocalDate toLocalDatePossiblyNullOrEmptyString(String s) throws ValidationException {
        if (Validator.isNullOrEmpty(s)) {
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
        return Validator.isNullOrEmpty(s) ? null : s;
    }

    public static int toInt(String s) throws ValidationException {
        return Integer.parseInt(
                Validator.assertIsValidInteger(s)
        );
    }
}
