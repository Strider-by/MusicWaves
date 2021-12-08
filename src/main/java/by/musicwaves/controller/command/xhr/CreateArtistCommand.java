package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.util.Converter;
import by.musicwaves.controller.command.util.Validator;
import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.Artist;
import by.musicwaves.entity.Role;
import by.musicwaves.entity.User;
import by.musicwaves.service.ArtistService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;
import by.musicwaves.util.BooleanOption;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

public class CreateArtistCommand extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(CreateArtistCommand.class);
    private final static ArtistService service = ServiceFactory.getInstance().getArtistService();

    private final static String PARAM_NAME_NAME = "name";
    private final static String PARAM_NAME_VISIBLE = "visible";

    private final static String JSON_ARTIST_OBJECT_NAME = "artist";

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, CommandException {

        // user must be logged in and it must be an administrator
        User user = getUser(request);
        if (user == null || (user.getRole() != Role.ADMINISTRATOR && user.getRole() != Role.MUSIC_CURATOR)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        Locale locale = user.getLanguage().getLocale();

        // can come as a valid value, empty string or don't come at all
        // if it comes as empty string or don't come at all, store it as null
        String name = Validator.assertNonNullOrEmpty(request.getParameter(PARAM_NAME_NAME));
        Boolean visible = BooleanOption.getById(
                Converter.toIntegerPossiblyNullOrEmptyString(
                        request.getParameter(PARAM_NAME_VISIBLE)))
                .getValue();


        ServiceResponse<Artist> serviceResponse;
        try {
            serviceResponse = service.createArtist(
                    name, visible, locale);
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

    private void appendServiceProvidedData(ServiceResponse<Artist> serviceResponse, JsonSelfWrapper json) {
        // if service fails in his work, no data is provided
        if (!serviceResponse.isSuccess()) {
            return;
        }

        Artist artist = serviceResponse.getStoredValue();
        json.openObject(JSON_DATA_OBJECT_NAME);

        // setting created artist properties
        json.openObject(JSON_ARTIST_OBJECT_NAME);
        json.appendNumber("id", artist.getId());
        json.appendString("name", artist.getName());
        json.appendString("image", artist.getImageName());
        json.appendBoolean("visible", artist.isVisible());

        json.closeObject();
        json.closeObject();
    }
}
