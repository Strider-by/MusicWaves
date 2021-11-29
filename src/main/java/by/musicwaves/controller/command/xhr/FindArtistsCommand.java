package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.Converter;
import by.musicwaves.entity.Artist;
import by.musicwaves.entity.Role;
import by.musicwaves.entity.User;
import by.musicwaves.service.ServiceException;
import by.musicwaves.service.ServiceResponse;
import by.musicwaves.service.ArtistService;
import by.musicwaves.util.BooleanOption;
import by.musicwaves.util.JsonSelfWrapper;
import by.musicwaves.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class FindArtistsCommand extends XHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(FindArtistsCommand.class);
    private final static ArtistService service = ArtistService.getInstance();

    private final static String PARAM_NAME_NAME = "name";
    private final static String PARAM_NAME_NAME_SEARCH_TYPE_ID = "name_search_type_id";
    private final static String PARAM_NAME_VISIBLE = "visible";

    private final static String JSON_ARTISTS_ARRAY_NAME = "artists";

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, CommandException {
        LOGGER.debug("FindArtistsCommand#execute reached");

        User user = getUser(request);
        if (user == null || (user.getRole() != Role.ADMINISTRATOR && user.getRole() != Role.MUSIC_CURATOR)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // can come as a valid value, empty string or don't come at all
        // if it comes as empty string or don't come at all, store it as null
        String name = Converter.toNullIfEmpty(request.getParameter(PARAM_NAME_NAME));
        Boolean visible = BooleanOption.getById(
                Converter.toIntegerPossiblyNullOrEmptyString(
                        request.getParameter(PARAM_NAME_VISIBLE)))
                .getValue();

        // processed parameters must be presented and be valid integer values
        // if not - CommandException will be thrown
        int nameSearchTypeId = Converter.toInt(request.getParameter(PARAM_NAME_NAME_SEARCH_TYPE_ID));
        int pageNumber = Converter.toInt(request.getParameter(XHRCommand.PARAM_NAME_PAGE_NUMBER));
        int recordsPerPage = Converter.toInt(request.getParameter(XHRCommand.PARAM_NAME_RECORDS_PER_PAGE));


        ServiceResponse<Pair<Integer, List<Artist>>> serviceResponse;
        try {
            serviceResponse = service.findArtists(
                    name, nameSearchTypeId, visible,
                    pageNumber, recordsPerPage);
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
