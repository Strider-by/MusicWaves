package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.entity.User;
import by.musicwaves.service.ServiceResponse;
import by.musicwaves.util.JsonSelfWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public abstract class XHRCommand {

    public static final String PARAM_NAME_PAGE_NUMBER = "page_number";
    public static final String PARAM_NAME_RECORDS_PER_PAGE = "records_per_page";
    public final static String PARAM_NAME_FIELD_TO_BE_SORTED_BY = "sort_by";
    public final static String PARAM_NAME_SORT_ORDER_ID = "sort_order_id";
    private final static String SESSION_ATTRIBUTE_NAME_USER = "user";

    protected final static String JSON_DATA_OBJECT_NAME = "data";
    private final static String JSON_SERVICE_MESSAGES_OBJECT_NAME = "messages";
    private final static String JSON_SERVICE_ERROR_MESSAGES_OBJECT_NAME = "error_messages";
    private final static String JSON_SERVICE_ERROR_CODES_OBJECT_NAME = "error_codes";
    private final static String JSON_SERVICE_IS_SUCCESS_FIELD_NAME = "success";

    public abstract void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, IOException;

    protected User getUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(SESSION_ATTRIBUTE_NAME_USER);
        return user;
    }

    protected void appendServiceMessages(ServiceResponse serviceResponse, JsonSelfWrapper json) {

        List<String> messages = serviceResponse.getMessages();
        json.openObject(JSON_SERVICE_MESSAGES_OBJECT_NAME);
        for(String message : messages) {
            json.appendString(message);
        }
        json.closeObject();

        List<String> errorCodes = serviceResponse.getErrorCodes();
        json.openObject(JSON_SERVICE_ERROR_CODES_OBJECT_NAME);
        for(String errorCode : errorCodes) {
            json.appendString(errorCode);
        }
        json.closeObject();

        List<String> errorMessages = serviceResponse.getErrorMessages();
        json.openObject(JSON_SERVICE_ERROR_MESSAGES_OBJECT_NAME);
        for(String errorMessage : errorMessages) {
            json.appendString(errorMessage);
        }
        json.closeObject();
    }

    protected void appendServiceExecutionResult(ServiceResponse serviceResponse, JsonSelfWrapper json) {
        json.appendBoolean(JSON_SERVICE_IS_SUCCESS_FIELD_NAME, serviceResponse.isSuccess());
    }
}
