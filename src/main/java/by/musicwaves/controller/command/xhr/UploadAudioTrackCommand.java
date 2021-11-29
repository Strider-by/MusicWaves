package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.Converter;
import by.musicwaves.entity.Artist;
import by.musicwaves.entity.Role;
import by.musicwaves.entity.User;
import by.musicwaves.service.ArtistService;
import by.musicwaves.service.AudioTrackService;
import by.musicwaves.service.ServiceException;
import by.musicwaves.service.ServiceResponse;
import by.musicwaves.util.BooleanOption;
import by.musicwaves.util.JsonSelfWrapper;
import by.musicwaves.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UploadAudioTrackCommand extends XHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(UploadAudioTrackCommand.class);
    private final static AudioTrackService service = AudioTrackService.getInstance();

    private final static String PARAM_NAME_TRACK_ID = "id";
    private final static String JSON_FILE_NAME_OBJECT_NAME = "file";

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, CommandException {

        User user = getUser(request);
        if (user == null || (user.getRole() != Role.ADMINISTRATOR && user.getRole() != Role.MUSIC_CURATOR)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        Locale locale = user.getLanguage().getLocale();
        int trackId = Converter.toInt(request.getParameter(PARAM_NAME_TRACK_ID));

        ServiceResponse<String> serviceResponse;
        try {
            serviceResponse = service.uploadAudioTrack(trackId, request, locale);
        } catch (ServiceException ex) {
            throw new CommandException(ex);
        }

        JsonSelfWrapper json = new JsonSelfWrapper();
        json.openJson();

        appendServiceProvidedData(serviceResponse, json);
        appendServiceExecutionResult(serviceResponse, json);
        appendServiceMessages(serviceResponse, json);

        json.closeJson();
        response.getWriter().write(json.toString());
    }

    private void appendServiceProvidedData(ServiceResponse<String> serviceResponse, JsonSelfWrapper json) {
        // if service fails in his work, no data is provided
        if (!serviceResponse.isSuccess()) {
            return;
        }

        json.appendString(JSON_FILE_NAME_OBJECT_NAME, serviceResponse.getStoredValue());
    }
}
