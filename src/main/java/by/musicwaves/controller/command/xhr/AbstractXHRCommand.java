package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.AbstractCommand;
import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public abstract class AbstractXHRCommand extends AbstractCommand implements XHRCommand {

    public static final String PARAM_NAME_PAGE_NUMBER = "page_number";
    public static final String PARAM_NAME_RECORDS_PER_PAGE = "records_per_page";
    public final static String PARAM_NAME_FIELD_TO_BE_SORTED_BY = "sort_by";
    public final static String PARAM_NAME_SORT_ORDER_ID = "sort_order_id";
    protected final static String JSON_DATA_OBJECT_NAME = "data";
    private final static Logger LOGGER = LogManager.getLogger(AbstractXHRCommand.class);
    private final static String JSON_SERVICE_MESSAGES_ARRAY_NAME = "messages";
    private final static String JSON_SERVICE_ERROR_MESSAGES_ARRAY_NAME = "error_messages";
    private final static String JSON_SERVICE_ERROR_CODES_ARRAY_NAME = "error_codes";
    private final static String JSON_SERVICE_IS_SUCCESS_FIELD_NAME = "success";

    public AbstractXHRCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    protected void processAccessForbiddenState(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        sendBadRequestError(response);
    }

    protected void appendServiceMessages(ServiceResponse serviceResponse, JsonSelfWrapper json) {

        List<String> messages = serviceResponse.getMessages();
        json.openArray(JSON_SERVICE_MESSAGES_ARRAY_NAME);
        for (String message : messages) {
            json.appendString(message);
        }
        json.closeArray();

        List<String> errorCodes = serviceResponse.getErrorCodes();
        json.openArray(JSON_SERVICE_ERROR_CODES_ARRAY_NAME);
        for (String errorCode : errorCodes) {
            json.appendString(errorCode);
        }
        json.closeArray();

        List<String> errorMessages = serviceResponse.getErrorMessages();
        json.openArray(JSON_SERVICE_ERROR_MESSAGES_ARRAY_NAME);
        for (String errorMessage : errorMessages) {
            json.appendString(errorMessage);
        }
        json.closeArray();
    }

    protected void appendServiceExecutionResult(ServiceResponse serviceResponse, JsonSelfWrapper json) {
        json.appendBoolean(JSON_SERVICE_IS_SUCCESS_FIELD_NAME, serviceResponse.isSuccess());
    }

    protected void sendResultJson(JsonSelfWrapper json, HttpServletResponse response) throws CommandException {
        try {
            response.getWriter().write(json.toString());
        } catch (IOException ex) {
            throw new CommandException(ex);
        }
    }
}
