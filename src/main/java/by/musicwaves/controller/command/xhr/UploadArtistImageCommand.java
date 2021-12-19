package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.exception.ValidationException;
import by.musicwaves.controller.util.Converter;
import by.musicwaves.controller.util.AccessLevelEnum;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.User;
import by.musicwaves.service.ArtistService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class UploadArtistImageCommand extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(UploadArtistImageCommand.class);
    private final static ArtistService service = ServiceFactory.getInstance().getArtistService();
    private final static String PARAM_NAME_ARTIST_ID = "id";
    private final static String JSON_FILE_NAME_OBJECT_NAME = "file";

    public UploadArtistImageCommand(AccessLevelEnum accessLevelEnum) {
        super(accessLevelEnum);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
        Locale locale = user.getLanguage().getLocale();
        int artistId = Converter.toInt(request.getParameter(PARAM_NAME_ARTIST_ID));

        ServiceResponse<String> serviceResponse;
        try {
            serviceResponse = service.uploadArtistImage(artistId, request, locale);
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

    private void appendServiceProvidedData(ServiceResponse<String> serviceResponse, JsonSelfWrapper json) {
        // if service fails in his work, no data is provided
        if (!serviceResponse.isSuccess()) {
            return;
        }

        json.appendString(JSON_FILE_NAME_OBJECT_NAME, serviceResponse.getStoredValue());
    }
}
