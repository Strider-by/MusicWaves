package by.musicwaves.controller.command.util;


import by.musicwaves.controller.command.exception.ValidationException;

import java.util.List;

public class Validator {

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    } // todo: move

    public static String assertIsValidInteger(String value) throws ValidationException {
        try {
            Integer.parseInt(value);
        } catch (NullPointerException | NumberFormatException ex) {
            throw new ValidationException("Provided value is either empty or not a valid integer value", ex);
        }

        return value;
    }

    public static void assertIsAllowedRequestMethod(String method, List<String> allowedMethods) throws ValidationException {
        if (!allowedMethods.contains(method)) {
            throw new ValidationException("This request method is not allowed");
        }
    }

    public static <T> T assertNonNull(T object) throws ValidationException {
        if (object == null) {
            throw new ValidationException("Provided value can not be null");
        }

        return object;
    }

    public static String assertNonNullOrEmpty(String s) throws ValidationException {
        if (s == null || s.isEmpty()) {
            throw new ValidationException("Provided value can not be null");
        }

        return s;
    }
}
