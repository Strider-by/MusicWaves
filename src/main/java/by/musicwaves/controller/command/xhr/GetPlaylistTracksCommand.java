package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.exception.ValidationException;
import by.musicwaves.controller.command.util.Converter;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.dto.PlaylistItemDto;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.User;
import by.musicwaves.service.CrossEntityService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

public class GetPlaylistTracksCommand extends AbstractXHRCommand {

    private static final Logger LOGGER = LogManager.getLogger(GetPlaylistTracksCommand.class);
    private static final CrossEntityService service = ServiceFactory.getInstance().getCrossEntityService();
    private static final String PARAM_NAME_PLAYLIST_ID = "playlist_id";
    private static final String JSON_PLAYLIST_ITEMS_ARRAY_NAME = "playlist_items";

    public GetPlaylistTracksCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
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
        sendResultJson(json, response);
    }

    private void appendServiceProvidedData(ServiceResponse<List<PlaylistItemDto>> serviceResponse, JsonSelfWrapper json) {
        // if service fails in his work, no data is provided
        if (!serviceResponse.isSuccess()) {
            return;
        }

        json.openObject(JSON_DATA_OBJECT_NAME);
        json.openArray(JSON_PLAYLIST_ITEMS_ARRAY_NAME);

        for (PlaylistItemDto dto : serviceResponse.getStoredValue()) {
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
