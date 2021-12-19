package by.musicwaves.dto;

import by.musicwaves.service.message.ServiceErrorEnum;
import by.musicwaves.service.message.ServiceMessageEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServiceResponse<T> {

    private boolean success = true;
    private T storedValue;
    private List<String> messages = new ArrayList<>();
    private List<String> errorMessages = new ArrayList<>();
    private List<String> errorCodes = new ArrayList<>();

    public ServiceResponse() {
    }

    public ServiceResponse(T valueToStore) {
        this.storedValue = valueToStore;
    }

    public T getStoredValue() {
        return storedValue;
    }

    public void setStoredValue(T instance) {
        this.storedValue = instance;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void addErrorMessage(String message) {
        this.errorMessages.add(message);
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }

    public void addErrorCode(String errorCode) {
        this.errorCodes.add(errorCode);
    }

    public void addErrorOccurrence(String code, String message) {
        addErrorCode(code);
        addErrorMessage(message);
    }

    public void addErrorOccurrence(ServiceErrorEnum serviceErrorEnum, Locale locale) {
        setSuccess(false);
        addErrorCode(serviceErrorEnum.getErrorCode());
        addErrorMessage(serviceErrorEnum.getMessage(locale));
    }

    public void addMessageOccurrence(ServiceMessageEnum message, Locale locale) {
        addMessage(message.getMessage(locale));
    }

    public List<String> getErrorCodes() {
        return errorCodes;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public List<String> getMessages() {
        return messages;
    }


    @Override
    public String toString() {
        return "ServiceResponse{" +
                "success=" + success +
                ", storedValue=" + storedValue +
                ", messages=" + messages +
                ", errorCodes=" + errorCodes +
                ", errorMessages=" + errorMessages +
                '}';
    }
}
