package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.Converter;
import by.musicwaves.dto.PlaylistItemDto;
import by.musicwaves.entity.Playlist;
import by.musicwaves.entity.User;
import by.musicwaves.service.CrossEntityService;
import by.musicwaves.service.PlaylistService;
import by.musicwaves.service.ServiceException;
import by.musicwaves.service.ServiceResponse;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GetPlaylistTracksCommand extends XHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(GetPlaylistTracksCommand.class);
    private final static CrossEntityService service = CrossEntityService.getInstance();

    private final static String PARAM_NAME_PLAYLIST_ID = "playlist_id";
    private final static String JSON_PLAYLIST_ITEMS_ARRAY_NAME = "playlist_items";

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, CommandException {

        // user must be logged in
        User user = getUser(request);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        Locale locale = user.getLanguage().getLocale();
        int userId = user.getId();
        int playlistId = Converter.toInt(request.getParameter(PARAM_NAME_PLAYLIST_ID));


        ServiceResponse<List<PlaylistItemDto>> serviceResponse;
        try {
            serviceResponse = service.getPlaylistTracks(
                    userId, playlistId, locale);
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

    private void appendServiceProvidedData(ServiceResponse<List<PlaylistItemDto>> serviceResponse, JsonSelfWrapper json) {
        // if service fails in his work, no data is provided
        if (!serviceResponse.isSuccess()) {
            return;
        }

        json.openObject(JSON_DATA_OBJECT_NAME);
        json.openArray(JSON_PLAYLIST_ITEMS_ARRAY_NAME);

        for (PlaylistItemDto dto: serviceResponse.getStoredValue()) {
            json.openObject();
            json.appendNumber("item_id", dto.getId());
            json.appendNumber("track_id", dto.getAudioTrackId());
            json.appendString("track_name", dto.getTrackName());
            json.appendBoolean("active", dto.isActive());
            json.closeObject();
        }

        json.closeArray();
        json.closeObject();
    }
}
