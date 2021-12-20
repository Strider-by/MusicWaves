package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.exception.ValidationException;
import by.musicwaves.controller.command.util.Converter;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.AudioTrack;
import by.musicwaves.entity.User;
import by.musicwaves.service.AudioTrackService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

public class FindAlbumRelatedAudioTracksCommand extends AbstractXHRCommand {

    private static final Logger LOGGER = LogManager.getLogger(FindAlbumRelatedAudioTracksCommand.class);
    private static final AudioTrackService service = ServiceFactory.getInstance().getAudioTrackService();
    private static final String PARAM_NAME_ALBUM_ID = "album";
    private static final String JSON_TRACKS_ARRAY_NAME = "tracks";

    public FindAlbumRelatedAudioTracksCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
        Locale locale = user.getLanguage().getLocale();
        int albumId = Converter.toInt(request.getParameter(PARAM_NAME_ALBUM_ID));

        ServiceResponse<List<AudioTrack>> serviceResponse;
        try {
            serviceResponse = service.findAudioTracks(albumId, locale);
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

    private void appendServiceProvidedData(ServiceResponse<List<AudioTrack>> serviceResponse, JsonSelfWrapper json) {
        // if service fails in his work, no data is provided
        if (!serviceResponse.isSuccess()) {
            return;
        }

        json.openObject(JSON_DATA_OBJECT_NAME);

        // setting list of instances found for requested page
        json.openArray(JSON_TRACKS_ARRAY_NAME);

        for (AudioTrack track : serviceResponse.getStoredValue()) {
            json.openObject();
            json.appendNumber("id", track.getId());
            json.appendString("name", track.getName());
            json.appendString("file", track.getFileName());
            json.appendNumber("number", track.getNumber());
            json.appendBoolean("visible", track.isVisible());
            json.closeObject();
        }

        json.closeArray();
        json.closeObject();
    }
}
