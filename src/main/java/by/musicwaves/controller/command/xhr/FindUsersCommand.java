package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.exception.ValidationException;
import by.musicwaves.controller.util.Converter;
import by.musicwaves.controller.util.AccessLevelEnum;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.User;
import by.musicwaves.service.UserService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;
import by.musicwaves.util.JsonSelfWrapper;
import by.musicwaves.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class FindUsersCommand extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(FindUsersCommand.class);
    private final static UserService service = ServiceFactory.getInstance().getUserService();
    private final static String PARAM_NAME_ID = "id";
    private final static String PARAM_NAME_LOGIN = "login";
    private final static String PARAM_NAME_ROLE_ID = "role_id";
    private final static String PARAM_NAME_REGISTER_DATE = "register_date";
    private final static String PARAM_NAME_LOGIN_SEARCH_TYPE_ID = "login_search_type_id";
    private final static String PARAM_NAME_REGISTER_DATE_COMPARE_TYPE_ID = "register_date_compare_type_id";
    private final static String JSON_USERS_ARRAY_NAME = "users";

    public FindUsersCommand(AccessLevelEnum accessLevelEnum) {
        super(accessLevelEnum);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
        Locale locale = user.getLanguage().getLocale();
        // These parameters can come both as an empty value or a valid integer value
        // So we treat them properly.
        // todo: check usage and change to non-null-value-required
        Integer id = Converter.toIntegerPossiblyNullOrEmptyString(request.getParameter(PARAM_NAME_ID));
        Integer roleId = Converter.toIntegerPossiblyNullOrEmptyString(request.getParameter(PARAM_NAME_ROLE_ID));
        LocalDate registerDate = Converter.toLocalDatePossiblyNullOrEmptyString(request.getParameter(PARAM_NAME_REGISTER_DATE));

        // can come as a valid login value or an empty string
        // if it comes as empty string, store it as null
        String login = Converter.toNullIfEmpty(request.getParameter(PARAM_NAME_LOGIN));

        // processed parameters must be presented and be valid integer values
        // if not - CommandException will be thrown
        int fieldIdToBeSortedBy = Converter.toInt(request.getParameter(AbstractXHRCommand.PARAM_NAME_FIELD_TO_BE_SORTED_BY));
        int loginSearchTypeId = Converter.toInt(request.getParameter(PARAM_NAME_LOGIN_SEARCH_TYPE_ID));
        int registerDateCompareTypeId = Converter.toInt(request.getParameter(PARAM_NAME_REGISTER_DATE_COMPARE_TYPE_ID));
        int sortOrderId = Converter.toInt(request.getParameter(AbstractXHRCommand.PARAM_NAME_SORT_ORDER_ID));
        int pageNumber = Converter.toInt(request.getParameter(AbstractXHRCommand.PARAM_NAME_PAGE_NUMBER));
        int recordsPerPage = Converter.toInt(request.getParameter(AbstractXHRCommand.PARAM_NAME_RECORDS_PER_PAGE));


        ServiceResponse<Pair<Integer, List<User>>> serviceResponse;
        try {
            serviceResponse = service.findUsers(
                    id, login, loginSearchTypeId, roleId,
                    registerDate, registerDateCompareTypeId,
                    fieldIdToBeSortedBy, sortOrderId,
                    pageNumber, recordsPerPage, locale);
        } catch (ServiceException ex) {
            throw new CommandException(ex);
        }

        JsonSelfWrapper json = new JsonSelfWrapper();
        json.openJson();

        appendServiceProvidedData(serviceResponse, json);
        appendServiceExecutionResult(serviceResponse, json);
        appendServiceMessages(serviceResponse, json);

        json.closeJson();
        sendResultJson(json, response);
    }

    private void appendServiceProvidedData(ServiceResponse<Pair<Integer, List<User>>> serviceResponse, JsonSelfWrapper json) {
        // if service fails in his work, no data is provided
        if (!serviceResponse.isSuccess()) {
            return;
        }

        json.openObject(JSON_DATA_OBJECT_NAME);

        // setting overall search result quantity without pagination limitations
        json.appendNumber("overall_quantity", serviceResponse.getStoredValue().getFirstValue());

        // setting list of users found for requested page
        json.openArray(JSON_USERS_ARRAY_NAME);

        for (User user : serviceResponse.getStoredValue().getSecondValue()) {
            json.openObject();
            json.appendNumber("id", user.getId());
            json.appendString("login", user.getLogin());
            json.appendNumber("role", user.getRole().getDatabaseId());
            json.appendString("registered", user.getCreated().toLocalDate().toString());
            json.closeObject();
        }

        json.closeArray();
        json.closeObject();
    }
}
