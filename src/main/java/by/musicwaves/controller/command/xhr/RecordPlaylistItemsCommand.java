package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.Converter;
import by.musicwaves.dto.PlaylistItemDto;
import by.musicwaves.entity.User;
import by.musicwaves.service.CrossEntityService;
import by.musicwaves.service.ServiceException;
import by.musicwaves.service.ServiceResponse;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RecordPlaylistItemsCommand extends XHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(RecordPlaylistItemsCommand.class);
    private final static CrossEntityService service = CrossEntityService.getInstance();

    private final static String PARAM_NAME_PLAYLIST_ID = "playlist_id";
    private final static String PARAM_NAME_TRACKS_IDS = "tracks_id[]";

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

        int playlistId;
        int[] tracksIds;
        try {
            playlistId = Integer.parseInt(request.getParameter(PARAM_NAME_PLAYLIST_ID));
            String[] tracksIdsStrings = request.getParameterValues(PARAM_NAME_TRACKS_IDS);
            tracksIds = Arrays.stream(tracksIdsStrings)
                    .mapToInt(Integer::parseInt)
                    .toArray();
        } catch (NullPointerException | NumberFormatException ex) {
            LOGGER.warn("Provided request parameters are not valid", ex);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        ServiceResponse<?> serviceResponse;
        try {
            serviceResponse = service.recordPlaylistElements(
                    userId, playlistId, tracksIds, locale);
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
