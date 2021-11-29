package by.musicwaves.controller.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Converter {

    /**
     * If the param string is null or empty, null will be returned.
     * If string is neither null or empty but cannot be converted to Integer, CommandException will be thrown.
     */
    public static Integer toIntegerPossiblyNullOrEmptyString(String s) throws CommandException {
        if (Validator.isNullOrEmpty(s)) {
            return null;
        } else {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ex) {
                throw new CommandException("Value [" + s + "] can not be converted to Integer", ex);
            }
        }
    }

    public static Boolean toBooleanPossiblyNullOrEmptyString(String s) throws CommandException {
        if (Validator.isNullOrEmpty(s)) {
            return null;
        } else {
            try {
                return Boolean.parseBoolean(s);
            } catch (NumberFormatException ex) {
                throw new CommandException("Value [" + s + "] can not be converted to Boolean", ex);
            }
        }
    }

    public static LocalDate toLocalDatePossiblyNullOrEmptyString(String s) throws CommandException {
        if (Validator.isNullOrEmpty(s)) {
            return null;
        } else {
            try {
                return LocalDate.parse(s);
            } catch (DateTimeParseException ex) {
                throw new CommandException("Value [" + s + "] can not be converted to LocalDate object", ex);
            }
        }
    }

    public static String toNullIfEmpty(String s) {
        return Validator.isNullOrEmpty(s) ? null : s;
    }

    public static int toInt(String s) throws CommandException {
        return Integer.parseInt(
                Validator.assertValidInteger(s)
        );
    }
}
