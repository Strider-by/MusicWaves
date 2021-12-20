package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.exception.ValidationException;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.User;
import by.musicwaves.service.PlaylistService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Locale;

public class DeleteMultiplePlaylistsCommand extends AbstractXHRCommand {

    private static final Logger LOGGER = LogManager.getLogger(DeleteMultiplePlaylistsCommand.class);
    private static final PlaylistService service = ServiceFactory.getInstance().getPlaylistService();
    private static final String PARAM_NAME_IDS = "id[]";

    public DeleteMultiplePlaylistsCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
        Locale locale = user.getLanguage().getLocale();
        int userId = user.getId();
        int[] playlistsIds;
        try {
            String[] playlistsIdStrings = request.getParameterValues(PARAM_NAME_IDS);
            playlistsIds = Arrays.stream(playlistsIdStrings)
                    .mapToInt(Integer::parseInt)
                    .toArray();
        } catch (NullPointerException | NumberFormatException ex) {
            throw new ValidationException(ex);
        }

        ServiceResponse<?> serviceResponse;
        try {
            serviceResponse = service.deleteMultiplePlaylists(userId, playlistsIds, locale);
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
