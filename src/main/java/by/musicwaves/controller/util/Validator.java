package by.musicwaves.controller.util;


import by.musicwaves.controller.exception.ValidationException;

import java.util.List;

/**
 * Utility class that helps to validate requests parameters.
 * If parameter isn't valid, {@link ValidationException} exception is thrown.
 */
public class Validator {

    /**
     * Checks if provided string with request method name is included in list with request methods that are allowed for
     * the specific command that called this method. If not - {@link ValidationException is thrown}.
     *
     * @param method         name of the actually used request method
     * @param allowedMethods list of the methods names that are allowed
     * @throws ValidationException
     */
    public static void assertIsAllowedRequestMethod(String method, List<String> allowedMethods) throws ValidationException {
        if (!allowedMethods.contains(method)) {
            throw new ValidationException("This request method is not allowed");
        }
    }

    /**
     * Checks if provided parameter is not null. If it is - {@link ValidationException} is thrown.
     * This method returns provided parameter as the return value so it can be used at once.
     *
     * @param object the object to check
     * @return the same object this method receives as a parameter
     * @throws ValidationException if provided parameter is null
     */
    public static <T> T assertNonNull(T object) throws ValidationException {
        if (object == null) {
            throw new ValidationException("Provided value can not be null");
        }

        return object;
    }

    /**
     * Checks if provided string is neither null nor empty . If it is - {@link ValidationException is thrown.}
     * This method returns provided parameter as the return value so it can be used at once.
     *
     * @param s the string to check
     * @return the same string this method receives as a parameter
     * @throws ValidationException if provided string  is either null or empty
     */
    public static String assertNonNullOrEmpty(String s) throws ValidationException {
        if (s == null || s.isEmpty()) {
            throw new ValidationException("Provided value can not be null");
        }

        return s;
    }
}
