package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.exception.ValidationException;
import by.musicwaves.controller.command.util.Converter;
import by.musicwaves.controller.command.util.Validator;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.AudioTrack;
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

public class CreateAudioTrackCommand extends AbstractXHRCommand {

    private static final Logger LOGGER = LogManager.getLogger(CreateAudioTrackCommand.class);
    private static final String PARAM_NAME_ALBUM_ID = "album_id";
    private static final String PARAM_NAME_NAME = "name";
    private static final String PARAM_NAME_VISIBLE = "visible";
    private static final String JSON_AUDIO_TRACK_OBJECT_NAME = "track";
    private static final AudioTrackService service = ServiceFactory.getInstance().getAudioTrackService();

    public CreateAudioTrackCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
        Locale locale = user.getLanguage().getLocale();

        int albumId = Converter.toInt(request.getParameter(PARAM_NAME_ALBUM_ID));
        String name = Validator.assertNonNull(request.getParameter(PARAM_NAME_NAME));
        Boolean visible = BooleanOption.getById(
                Converter.toIntegerPossiblyNullOrEmptyString(
                        request.getParameter(PARAM_NAME_VISIBLE))).getValue();

        ServiceResponse<AudioTrack> serviceResponse;
        try {
            serviceResponse = service.createAudioTrack(albumId, name, visible, locale);
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

    private void appendServiceProvidedData(ServiceResponse<AudioTrack> serviceResponse, JsonSelfWrapper json) {
        // if service fails in his work, no data is provided
        if (!serviceResponse.isSuccess()) {
            return;
        }

        AudioTrack track = serviceResponse.getStoredValue();
        json.openObject(JSON_DATA_OBJECT_NAME);

        // setting created track properties
        json.openObject(JSON_AUDIO_TRACK_OBJECT_NAME);
        json.appendNumber("id", track.getId());
        json.appendNumber("number", track.getNumber());
        json.appendString("name", track.getName());
        json.appendString("file", track.getFileName());
        json.appendBoolean("visible", track.isVisible());

        json.closeObject();
        json.closeObject();
    }
}
