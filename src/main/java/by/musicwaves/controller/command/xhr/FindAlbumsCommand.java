package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.util.Converter;
import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.Album;
import by.musicwaves.entity.Role;
import by.musicwaves.entity.User;
import by.musicwaves.service.AlbumService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;
import by.musicwaves.util.BooleanOption;
import by.musicwaves.util.JsonSelfWrapper;
import by.musicwaves.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class FindAlbumsCommand extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(FindAlbumsCommand.class);
    private final static AlbumService service = ServiceFactory.getInstance().getAlbumService();

    private final static String PARAM_NAME_ARTIST_ID = "artist";
    private final static String PARAM_NAME_NAME = "name";
    private final static String PARAM_NAME_YEAR = "year";
    private final static String PARAM_NAME_VISIBLE = "visible";

    private final static String JSON_ALBUMS_ARRAY_NAME = "albums";

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, CommandException {

        // user must be logged in and it must be an administrator
        User user = getUser(request);
        if (user == null || (user.getRole() != Role.ADMINISTRATOR && user.getRole() != Role.MUSIC_CURATOR)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        //can be null
        Integer year = Converter.toIntegerPossiblyNullOrEmptyString(request.getParameter(PARAM_NAME_YEAR));
        int artistId = Converter.toInt(request.getParameter(PARAM_NAME_ARTIST_ID));
        // can come as a valid value, empty string or don't come at all
        // if it comes as empty string or don't come at all, store it as null
        String name = Converter.toNullIfEmpty(request.getParameter(PARAM_NAME_NAME));
        Boolean visible = BooleanOption.getById(
                Converter.toIntegerPossiblyNullOrEmptyString(
                        request.getParameter(PARAM_NAME_VISIBLE)))
                .getValue();

        // processed parameters must be presented and be valid integer values
        // if not - CommandException will be thrown
        int pageNumber = Converter.toInt(request.getParameter(AbstractXHRCommand.PARAM_NAME_PAGE_NUMBER));
        int recordsPerPage = Converter.toInt(request.getParameter(AbstractXHRCommand.PARAM_NAME_RECORDS_PER_PAGE));


        ServiceResponse<Pair<Integer, List<Album>>> serviceResponse;
        try {
            serviceResponse = service.findAlbums(
                    artistId, name, year, visible,
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

    private void appendServiceProvidedData(ServiceResponse<Pair<Integer, List<Album>>> serviceResponse, JsonSelfWrapper json) {
        // if service fails in his work, no data is provided
        if (!serviceResponse.isSuccess()) {
            return;
        }

        json.openObject(JSON_DATA_OBJECT_NAME);

        // setting overall search result quantity without pagination limitations
        json.appendNumber("overall_quantity", serviceResponse.getStoredValue().getFirstValue());

        // setting list of instances found for requested page
        json.openArray(JSON_ALBUMS_ARRAY_NAME);

        for (Album album : serviceResponse.getStoredValue().getSecondValue()) {
            json.openObject();
            json.appendNumber("id", album.getId());
            json.appendString("name", album.getName());
            json.appendNumber("year", album.getYear());
            json.appendString("image", album.getImageName());
            json.appendBoolean("visible", album.isVisible());
            json.closeObject();
        }

        json.closeArray();
        json.closeObject();
    }
}
