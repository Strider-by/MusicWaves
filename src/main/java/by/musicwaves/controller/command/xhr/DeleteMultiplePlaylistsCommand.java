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
import java.util.Arrays;
import java.util.Locale;

public class DeleteMultiplePlaylistsCommand extends XHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(DeleteMultiplePlaylistsCommand.class);
    private final static PlaylistService service = PlaylistService.getInstance();
    private final static String PARAM_NAME_IDS = "id[]";

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
        int[] playlistsIds;
        try {
            String[] playlistsIdStrings = request.getParameterValues(PARAM_NAME_IDS);
            playlistsIds = Arrays.stream(playlistsIdStrings)
                    .mapToInt(Integer::parseInt)
                    .toArray();
        } catch (NullPointerException | NumberFormatException ex) {
            LOGGER.warn("wrong parameters", ex);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }


        ServiceResponse<?> serviceResponse;
        try {
            serviceResponse = service.deleteMultiplePlaylists(
                    userId, playlistsIds, locale);
        } catch (ServiceException ex) {
            throw new CommandException(ex);
        }

        JsonSelfWrapper json = new JsonSelfWrapper();
        json.openJson();

        appendServiceExecutionResult(serviceResponse, json);
        appendServiceMessages(serviceResponse, json);

        json.closeJson();
        response.getWriter().write(json.toString());
    }

}
