package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.controller.command.exception.ValidationException;
import by.musicwaves.controller.command.util.Converter;
import by.musicwaves.controller.command.util.Validator;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.Album;
import by.musicwaves.entity.User;
import by.musicwaves.service.AlbumService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;
import by.musicwaves.util.BooleanOption;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class CreateAlbumCommand extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(CreateAlbumCommand.class);
    private final static AlbumService service = ServiceFactory.getInstance().getAlbumService();
    private final static String PARAM_NAME_ARTIST_ID = "artist";
    private final static String PARAM_NAME_NAME = "name";
    private final static String PARAM_NAME_YEAR = "year";
    private final static String PARAM_NAME_VISIBLE = "visible";
    private final static String JSON_ALBUM_OBJECT_NAME = "album";

    public CreateAlbumCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
        Locale locale = user.getLanguage().getLocale();
        int artistId = Converter.toInt(request.getParameter(PARAM_NAME_ARTIST_ID));
        int year = Converter.toInt(request.getParameter(PARAM_NAME_YEAR));
        String name = Validator.assertNonNull(request.getParameter(PARAM_NAME_NAME));
        Boolean visible = BooleanOption.getById(
                Converter.toInt(request.getParameter(PARAM_NAME_VISIBLE))).getValue();

        ServiceResponse<Album> serviceResponse;
        try {
            serviceResponse = service.createAlbum(artistId, name, year, visible, locale);
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

    private void appendServiceProvidedData(ServiceResponse<Album> serviceResponse, JsonSelfWrapper json) {
        // if service fails in his work, no data is provided
        if (!serviceResponse.isSuccess()) {
            return;
        }

        Album album = serviceResponse.getStoredValue();
        json.openObject(JSON_DATA_OBJECT_NAME);

        // setting created album properties
        json.openObject(JSON_ALBUM_OBJECT_NAME);
        json.appendNumber("id", album.getId());
        json.appendString("name", album.getName());
        json.appendNumber("year", album.getYear());
        json.appendString("image", album.getImageName());
        json.appendBoolean("visible", album.isVisible());

        json.closeObject();
        json.closeObject();
    }
}
