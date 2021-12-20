package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.exception.ValidationException;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.Playlist;
import by.musicwaves.entity.User;
import by.musicwaves.service.PlaylistService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

public class GetUserPlaylistsCommand extends AbstractXHRCommand {

    private static final Logger LOGGER = LogManager.getLogger(GetUserPlaylistsCommand.class);
    private static final PlaylistService service = ServiceFactory.getInstance().getPlaylistService();
    private static final String JSON_PLAYLIST_ITEMS_ARRAY_NAME = "playlists";

    public GetUserPlaylistsCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
        Locale locale = user.getLanguage().getLocale();
        int userId = user.getId();

        ServiceResponse<List<Playlist>> serviceResponse;
        try {
            serviceResponse = service.getUserPlaylists(
                    userId, locale);
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

    private void appendServiceProvidedData(ServiceResponse<List<Playlist>> serviceResponse, JsonSelfWrapper json) {
        // if service fails in his work, no data is provided
        if (!serviceResponse.isSuccess()) {
            return;
        }

        json.openObject(JSON_DATA_OBJECT_NAME);
        json.openArray(JSON_PLAYLIST_ITEMS_ARRAY_NAME);

        for (Playlist playlist : serviceResponse.getStoredValue()) {
            json.openObject();
            json.appendNumber("id", playlist.getId());
            json.appendString("name", playlist.getName());
            json.closeObject();
        }

        json.closeArray();
        json.closeObject();
    }
}
