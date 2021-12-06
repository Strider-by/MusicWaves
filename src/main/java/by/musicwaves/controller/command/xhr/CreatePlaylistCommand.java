package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.Validator;
import by.musicwaves.entity.Playlist;
import by.musicwaves.entity.User;
import by.musicwaves.service.PlaylistService;
import by.musicwaves.service.ServiceException;
import by.musicwaves.service.ServiceResponse;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

public class CreatePlaylistCommand extends XHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(CreatePlaylistCommand.class);
    private final static PlaylistService service = PlaylistService.getInstance();

    private final static String PARAM_NAME_NAME = "name";
    private final static String JSON_PLAYLIST_OBJECT_NAME = "playlist";

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
        String playlistName = Validator.assertNonNullOrEmpty(request.getParameter(PARAM_NAME_NAME));


        ServiceResponse<Playlist> serviceResponse;
        try {
            serviceResponse = service.createPlaylist(
                    userId, playlistName, locale);
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

    private void appendServiceProvidedData(ServiceResponse<Playlist> serviceResponse, JsonSelfWrapper json) {
        // if service fails in his work, no data is provided
        if (!serviceResponse.isSuccess()) {
            return;
        }

        Playlist playlist = serviceResponse.getStoredValue();
        json.openObject(JSON_DATA_OBJECT_NAME);

        // setting created artist properties
        json.openObject(JSON_PLAYLIST_OBJECT_NAME);
        json.appendNumber("id", playlist.getId());
        json.appendString("name", playlist.getName());

        json.closeObject();
        json.closeObject();
    }
}
