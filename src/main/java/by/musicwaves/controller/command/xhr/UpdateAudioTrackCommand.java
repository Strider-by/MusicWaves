package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.controller.command.exception.ValidationException;
import by.musicwaves.controller.command.util.Converter;
import by.musicwaves.controller.command.util.Validator;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.User;
import by.musicwaves.service.AudioTrackService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;
import by.musicwaves.util.BooleanOption;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class UpdateAudioTrackCommand extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(UpdateAudioTrackCommand.class);
    private final static AudioTrackService service = ServiceFactory.getInstance().getAudioTrackService();
    private final static String PARAM_NAME_ID = "id";
    private final static String PARAM_NAME_NAME = "name";
    private final static String PARAM_NAME_VISIBLE = "visible";

    public UpdateAudioTrackCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
        Locale locale = user.getLanguage().getLocale();
        int id = Converter.toInt(request.getParameter(PARAM_NAME_ID));
        String name = Validator.assertNonNullOrEmpty(request.getParameter(PARAM_NAME_NAME));
        Boolean visible = BooleanOption.getById( // todo: fix possible null value
                Converter.toIntegerPossiblyNullOrEmptyString(
                        request.getParameter(PARAM_NAME_VISIBLE))).getValue();


        ServiceResponse<?> serviceResponse;
        try {
            serviceResponse = service.updateAudioTrack(
                    id, name, visible, locale);
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
