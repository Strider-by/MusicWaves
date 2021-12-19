package by.musicwaves.controller.command.util;

import by.musicwaves.controller.exception.ValidationException;
import by.musicwaves.controller.util.Converter;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class ConverterTest {

    private final String emptyString = "";

    private final String validIntegerString = "12345";
    private final Integer validIntegerStringParseResult = 12345;
    private final String invalidIntegerString = "..01asd";

    private final String validLocalDateString = LocalDate.MIN.toString();
    private final LocalDate validLocalDateParseResult = LocalDate.MIN;
    private final String invalidLocalDateString = "1123MonSept1984";


    private final String nonEmptyString = "not empty";

    @Test
    public void toIntegerPossiblyNullOrEmptyString_NullParameterCheck() throws ValidationException {
        assertNull(Converter.toIntegerPossiblyNullOrEmptyString(null));
    }

    @Test
    public void toIntegerPossiblyNullOrEmptyString_EmptyStringParameterCheck() throws ValidationException {
        assertNull(Converter.toIntegerPossiblyNullOrEmptyString(emptyString));
    }

    @Test
    public void toIntegerPossiblyNullOrEmptyString_ValidIntegerStringParameterCheck() throws ValidationException {
        assertEquals(validIntegerStringParseResult, Converter.toIntegerPossiblyNullOrEmptyString(validIntegerString));
    }

    @Test(expected = ValidationException.class)
    public void toIntegerPossiblyNullOrEmptyString_InvalidIntegerStringParameterCheck() throws ValidationException {
        Converter.toIntegerPossiblyNullOrEmptyString(invalidIntegerString);
    }

    @Test
    public void toLocalDatePossiblyNullOrEmptyString_NullParameterCheck() throws ValidationException {
        assertNull(Converter.toLocalDatePossiblyNullOrEmptyString(null));
    }

    @Test
    public void toLocalDatePossiblyNullOrEmptyString_EmptyStringParameterCheck() throws ValidationException {
        assertNull(Converter.toLocalDatePossiblyNullOrEmptyString(emptyString));
    }

    @Test
    public void toLocalDatePossiblyNullOrEmptyString_ValidStringParameterCheck() throws ValidationException {
        assertEquals(validLocalDateParseResult, Converter.toLocalDatePossiblyNullOrEmptyString(validLocalDateString));
    }

    @Test(expected = ValidationException.class)
    public void toLocalDatePossiblyNullOrEmptyString_InvalidStringParameterCheck() throws ValidationException {
        Converter.toLocalDatePossiblyNullOrEmptyString(invalidLocalDateString);
    }

    @Test
    public void toNullIfEmpty_EmptyStringParameter() {
        assertNull(Converter.toNullIfEmpty(emptyString));
    }

    @Test
    public void toNullIfEmpty_NullParameter() {
        assertNull(Converter.toNullIfEmpty(null));
    }

    @Test
    public void toNullIfEmpty_NonEmptyStringParameter() {
        assertNotNull(Converter.toNullIfEmpty(nonEmptyString));
        assertEquals(nonEmptyString, Converter.toNullIfEmpty(nonEmptyString));
    }

    @Test(expected = ValidationException.class)
    public void toInt_NullParameter() throws ValidationException {
        Converter.toInt(null);
    }

    @Test(expected = ValidationException.class)
    public void toInt_InvalidIntegerStringParameter() throws ValidationException {
        Converter.toInt(invalidIntegerString);
    }

    @Test
    public void toInt_ValidIntegerStringParameter() throws ValidationException {
       assertEquals(validIntegerStringParseResult, Integer.valueOf(Converter.toInt(validIntegerString)));
    }
}