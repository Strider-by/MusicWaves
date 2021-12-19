package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.exception.ValidationException;
import by.musicwaves.controller.util.Converter;
import by.musicwaves.controller.util.Validator;
import by.musicwaves.controller.util.AccessLevelEnum;
import by.musicwaves.dto.ArtistDto;
import by.musicwaves.dto.MusicSearchResultsContainer;
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
import java.util.List;
import java.util.Locale;

public class FindArtistsForMusicSearchPageCommand extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(FindArtistsForMusicSearchPageCommand.class);
    private final static CrossEntityService service = ServiceFactory.getInstance().getCrossEntityService();
    private final static String PARAM_NAME_SEARCH_STRING = "search_string";
    private final static String PARAM_NAME_PAGE_NUMBER = "page";
    private final static String PARAM_NAME_LIMIT = "limit";

    public FindArtistsForMusicSearchPageCommand(AccessLevelEnum accessLevelEnum) {
        super(accessLevelEnum);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
        Locale locale = user.getLanguage().getLocale();
        int userId = user.getId();
        String searchString = Validator.assertNonNull(request.getParameter(PARAM_NAME_SEARCH_STRING));
        int limit = Converter.toInt(request.getParameter(PARAM_NAME_LIMIT));
        int page = Converter.toInt(request.getParameter(PARAM_NAME_PAGE_NUMBER));
        int offset = (page - 1) * limit;

        if (offset < 0 || limit < 0) {
            throw new CommandException("Invalid limit or offset parameter");
        }

        ServiceResponse<MusicSearchResultsContainer<List<ArtistDto>>> serviceResponse;
        try {
            serviceResponse = service.findArtistsForMusicSearchPage(
                    searchString,
                    userId,
                    limit,
                    offset,
                    locale);
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

    private void appendServiceProvidedData(
            ServiceResponse<MusicSearchResultsContainer<List<ArtistDto>>> serviceResponse,
            JsonSelfWrapper json) {

        json.openObject("results_quantity");
        json.appendNumber("artists", serviceResponse.getStoredValue().getArtistsFound());
        json.appendNumber("albums", serviceResponse.getStoredValue().getAlbumsFound());
        json.appendNumber("tracks", serviceResponse.getStoredValue().getAudioTracksFound());
        json.closeObject();

        json.openArray("artists");
        for (ArtistDto dto : serviceResponse.getStoredValue().getStoredValue()) {
            json.openObject();
            json.appendNumber("artist_id", dto.getArtistId());
            json.appendString("artist_name", dto.getArtistName());
            json.appendString("artist_image", dto.getArtistImageName());
            json.appendBoolean("favourite", dto.isFavourite());
            json.appendNumber("albums_count_artist_has", dto.getAlbumsCountArtistHas());
            json.closeObject();
        }
        json.closeArray();
    }
}
