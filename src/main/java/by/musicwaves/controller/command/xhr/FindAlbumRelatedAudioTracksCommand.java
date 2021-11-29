package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.Converter;
import by.musicwaves.entity.Album;
import by.musicwaves.entity.AudioTrack;
import by.musicwaves.entity.Role;
import by.musicwaves.entity.User;
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

public class FindAlbumRelatedAudioTracksCommand extends XHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(FindAlbumRelatedAudioTracksCommand.class);
    private final static AudioTrackService service = AudioTrackService.getInstance();

    private final static String PARAM_NAME_ALBUM_ID = "album";

    private final static String JSON_TRACKS_ARRAY_NAME = "tracks";

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, CommandException {

        // user must be logged in and it must be an administrator
        User user = getUser(request);
        if (user == null || (user.getRole() != Role.ADMINISTRATOR && user.getRole() != Role.MUSIC_CURATOR)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        int albumId = Converter.toInt(request.getParameter(PARAM_NAME_ALBUM_ID));


        ServiceResponse<List<AudioTrack>> serviceResponse;
        try {
            serviceResponse = service.findAudioTracks(albumId);
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
