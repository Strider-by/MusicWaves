package by.musicwaves.controller.command.util;

import by.musicwaves.controller.command.exception.ValidationException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ValidatorTest {

    private final List<String> postMethodOnly = Collections.unmodifiableList(Arrays.asList("post"));
    private final List<String> postAndGetMethods = Collections.unmodifiableList(Arrays.asList("post", "get"));
    private final String postMethodName = "post";
    private final String getMethodName = "get";

    private final Object nonNullObject = new Object();
    private final String emptyString = "";
    private final String nonEmptyString = "not empty";

    @Test
    public void assertIsAllowedRequestMethod_True() throws ValidationException {
        Validator.assertIsAllowedRequestMethod(postMethodName, postAndGetMethods);
    }

    @Test(expected = ValidationException.class)
    public void assertIsAllowedRequestMethod_False() throws ValidationException {
        Validator.assertIsAllowedRequestMethod(getMethodName, postMethodOnly);
    }

    @Test
    public void assertNonNull_True() throws ValidationException {
        Validator.assertNonNull(nonNullObject);
    }

    @Test(expected = ValidationException.class)
    public void assertNonNull_False() throws ValidationException {
        Validator.assertNonNull(null);
    }

    @Test(expected = ValidationException.class)
    public void assertNonNullOrEmpty_EmptyStringTest() throws ValidationException {
        Validator.assertNonNullOrEmpty(emptyString);
    }

    @Test(expected = ValidationException.class)
    public void assertNonNullOrEmpty_NullParameterTest() throws ValidationException {
        Validator.assertNonNullOrEmpty(null);
    }

    @Test
    public void assertNonNullOrEmpty_NonEmptyStringParameterTest() throws ValidationException {
        Validator.assertNonNullOrEmpty(nonEmptyString);
    }
}