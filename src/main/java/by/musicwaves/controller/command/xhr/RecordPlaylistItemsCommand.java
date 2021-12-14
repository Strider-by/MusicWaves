package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.controller.command.exception.ValidationException;
import by.musicwaves.controller.resource.AccessLevel;
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
import java.util.Arrays;
import java.util.Locale;

public class RecordPlaylistItemsCommand extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(RecordPlaylistItemsCommand.class);
    private final static CrossEntityService service = ServiceFactory.getInstance().getCrossEntityService();
    private final static String PARAM_NAME_PLAYLIST_ID = "playlist_id";
    private final static String PARAM_NAME_TRACKS_IDS = "tracks_id[]";

    public RecordPlaylistItemsCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
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
            throw new ValidationException(ex);
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
        sendResultJson(json, response);
    }

}
