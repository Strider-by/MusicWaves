package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.exception.ValidationException;
import by.musicwaves.controller.command.util.Converter;
import by.musicwaves.controller.command.util.Validator;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.User;
import by.musicwaves.service.AlbumService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;
import by.musicwaves.util.BooleanOption;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class UpdateAlbumCommand extends AbstractXHRCommand {

    private static final Logger LOGGER = LogManager.getLogger(UpdateAlbumCommand.class);
    private static final AlbumService service = ServiceFactory.getInstance().getAlbumService();
    private static final String PARAM_NAME_ID = "id";
    private static final String PARAM_NAME_NAME = "name";
    private static final String PARAM_NAME_YEAR = "year";
    private static final String PARAM_NAME_VISIBLE = "visible";

    public UpdateAlbumCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
        Locale locale = user.getLanguage().getLocale();
        int id = Converter.toInt(request.getParameter(PARAM_NAME_ID));
        int year = Converter.toInt(request.getParameter(PARAM_NAME_YEAR));
        String name = Validator.assertNonNullOrEmpty(request.getParameter(PARAM_NAME_NAME));
        Boolean visible = BooleanOption.getById( // todo: fix possible null value
                Converter.toIntegerPossiblyNullOrEmptyString(
                        request.getParameter(PARAM_NAME_VISIBLE))).getValue();


        ServiceResponse<?> serviceResponse;
        try {
            serviceResponse = service.updateAlbum(
                    id, name, year, visible, locale);
        } catch (ServiceException ex) {
            throw new CommandException(ex);
        }

        JsonSelfWrapper json = new JsonSelfWrapper();
        json.openJson();

        appendServiceExecutionResult(serviceResponse, json);
        appendServiceMessages(serviceResponse, json);

        json.closeJson();
        sendResultJson(json, response);
    }
}
