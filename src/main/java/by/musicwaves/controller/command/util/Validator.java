package by.musicwaves.controller.command.util;


import by.musicwaves.controller.command.exception.CommandException;

import java.util.List;

public class Validator {

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    } // todo: move

    public static String assertIsValidInteger(String value) throws CommandException {
        try {
            Integer.parseInt(value);
        } catch (NullPointerException | NumberFormatException ex) {
            throw new CommandException("Provided value is either empty or not a valid integer value", ex);
        }

        return value;
    }

    public static void assertIsAllowedRequestMethod(String method, List<String> allowedMethods) throws CommandException {
        if (!allowedMethods.contains(method)) {
            throw new CommandException("This request method is not allowed");
        }
    }

    public static <T> T assertNonNull(T object) throws CommandException {
        if (object == null) {
            throw new CommandException("Provided value can not be null");
        }

        return object;
    }

    public static String assertNonNullOrEmpty(String s) throws CommandException {
        if (s == null || s.isEmpty()) {
            throw new CommandException("Provided value can not be null");
        }

        return s;
    }
}
