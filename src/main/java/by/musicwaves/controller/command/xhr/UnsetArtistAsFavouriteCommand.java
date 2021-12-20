package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.exception.ValidationException;
import by.musicwaves.controller.command.util.Converter;
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

public class UnsetArtistAsFavouriteCommand extends AbstractXHRCommand {

    private static final Logger LOGGER = LogManager.getLogger(UnsetArtistAsFavouriteCommand.class);
    private static final CrossEntityService service = ServiceFactory.getInstance().getCrossEntityService();
    private static final String PARAM_NAME_ARTIST_ID = "artist_id";

    public UnsetArtistAsFavouriteCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
        int userId = user.getId();
        int artistId = Converter.toInt(request.getParameter(PARAM_NAME_ARTIST_ID));

        ServiceResponse<?> serviceResponse;
        try {
            serviceResponse = service.unsetArtistAsFavourite(
                    userId,
                    artistId);
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
