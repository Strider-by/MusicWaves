package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.exception.ValidationException;
import by.musicwaves.controller.command.util.Converter;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.dto.AlbumDto;
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

public class FindAlbumsForMusicSearchPageCommand extends AbstractXHRCommand {

    private static final Logger LOGGER = LogManager.getLogger(FindAlbumsForMusicSearchPageCommand.class);
    private static final CrossEntityService service = ServiceFactory.getInstance().getCrossEntityService();
    private static final String PARAM_NAME_SEARCH_STRING = "search_string";
    private static final String PARAM_NAME_PAGE_NUMBER = "page";
    private static final String PARAM_NAME_LIMIT = "limit";

    public FindAlbumsForMusicSearchPageCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
        Locale locale = user.getLanguage().getLocale();
        int userId = user.getId();
        String searchString = request.getParameter(PARAM_NAME_SEARCH_STRING);
        int limit = Converter.toInt(request.getParameter(PARAM_NAME_LIMIT));
        int page = Converter.toInt(request.getParameter(PARAM_NAME_PAGE_NUMBER));
        int offset = (page - 1) * limit;

        if (offset < 0 || limit < 0) {
            throw new ValidationException("Limit and offset can't be less than 0");
        }

        ServiceResponse<MusicSearchResultsContainer<List<AlbumDto>>> serviceResponse;
        try {
            serviceResponse = service.findAlbumsForMusicSearchPage(
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
            ServiceResponse<MusicSearchResultsContainer<List<AlbumDto>>> serviceResponse,
            JsonSelfWrapper json) {

        json.openObject("results_quantity");
        json.appendNumber("artists", serviceResponse.getStoredValue().getArtistsFound());
        json.appendNumber("albums", serviceResponse.getStoredValue().getAlbumsFound());
        json.appendNumber("tracks", serviceResponse.getStoredValue().getAudioTracksFound());
        json.closeObject();

        json.openArray("albums");
        for (AlbumDto dto : serviceResponse.getStoredValue().getStoredValue()) {
            json.openObject();
            json.appendNumber("artist_id", dto.getArtistId());
            json.appendString("artist_name", dto.getArtistName());
            json.appendString("artist_image", dto.getArtistImageName());

            json.appendNumber("album_id", dto.getAlbumId());
            json.appendString("album_name", dto.getAlbumName());
            json.appendString("album_image", dto.getAlbumImageName());
            json.appendNumber("album_year", dto.getAlbumYear());
            json.appendBoolean("favourite", dto.isFavourite());
            json.appendNumber("tracks_count_album_has", dto.getTracksCountAlbumHas());
            json.closeObject();
        }
        json.closeArray();

    }
}
