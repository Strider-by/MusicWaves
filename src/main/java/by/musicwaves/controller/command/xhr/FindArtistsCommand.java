package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.controller.command.exception.ValidationException;
import by.musicwaves.controller.command.util.Converter;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.Artist;
import by.musicwaves.entity.User;
import by.musicwaves.service.ArtistService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;
import by.musicwaves.util.BooleanOption;
import by.musicwaves.util.JsonSelfWrapper;
import by.musicwaves.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

public class FindArtistsCommand extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(FindArtistsCommand.class);
    private final static ArtistService service = ServiceFactory.getInstance().getArtistService();
    private final static String PARAM_NAME_NAME = "name";
    private final static String PARAM_NAME_NAME_SEARCH_TYPE_ID = "name_search_type_id";
    private final static String PARAM_NAME_VISIBLE = "visible";
    private final static String JSON_ARTISTS_ARRAY_NAME = "artists";

    public FindArtistsCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
        Locale locale = user.getLanguage().getLocale();
        // can come as a valid value or an empty string
        // if it comes as empty string - store it as null
        String name = Converter.toNullIfEmpty(request.getParameter(PARAM_NAME_NAME));
        Boolean visible = BooleanOption.getById(
                Converter.toIntegerPossiblyNullOrEmptyString(
                        request.getParameter(PARAM_NAME_VISIBLE))).getValue();

        // processed parameters must be presented and be valid integer values
        // if not - CommandException will be thrown
        int nameSearchTypeId = Converter.toInt(request.getParameter(PARAM_NAME_NAME_SEARCH_TYPE_ID));
        int pageNumber = Converter.toInt(request.getParameter(AbstractXHRCommand.PARAM_NAME_PAGE_NUMBER));
        int recordsPerPage = Converter.toInt(request.getParameter(AbstractXHRCommand.PARAM_NAME_RECORDS_PER_PAGE));


        ServiceResponse<Pair<Integer, List<Artist>>> serviceResponse;
        try {
            serviceResponse = service.findArtists(
                    name, nameSearchTypeId, visible,
                    pageNumber, recordsPerPage, locale);
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

    private void appendServiceProvidedData(ServiceResponse<Pair<Integer, List<Artist>>> serviceResponse, JsonSelfWrapper json) {
        // if service fails in his work, no data is provided
        if (!serviceResponse.isSuccess()) {
            return;
        }

        json.openObject(JSON_DATA_OBJECT_NAME);

        // setting overall search result quantity without pagination limitations
        json.appendNumber("overall_quantity", serviceResponse.getStoredValue().getFirstValue());

        // setting list of instances found for requested page
        json.openArray(JSON_ARTISTS_ARRAY_NAME);

        for (Artist artist : serviceResponse.getStoredValue().getSecondValue()) {
            json.openObject();
            json.appendNumber("id", artist.getId());
            json.appendString("name", artist.getName());
            json.appendString("image", artist.getImageName());
            json.appendBoolean("visible", artist.isVisible());
            json.closeObject();
        }

        json.closeArray();
        json.closeObject();
    }
}
