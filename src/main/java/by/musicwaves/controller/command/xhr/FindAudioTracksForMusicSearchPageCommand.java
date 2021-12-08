package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.util.Converter;
import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.dto.AudioTrackDto;
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
import java.io.IOException;
import java.util.List;

public class FindAudioTracksForMusicSearchPageCommand extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(FindAudioTracksForMusicSearchPageCommand.class);
    private final static CrossEntityService service = ServiceFactory.getInstance().getCrossEntityService();

    private final static String PARAM_NAME_SEARCH_STRING = "search_string";
    private final static String PARAM_NAME_PAGE_NUMBER = "page";
    private final static String PARAM_NAME_LIMIT = "limit";

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, CommandException {

        // user must be logged in
        User user = getUser(request);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        int userId = user.getId();

        String searchString = request.getParameter(PARAM_NAME_SEARCH_STRING);
        int limit = Converter.toInt(request.getParameter(PARAM_NAME_LIMIT));
        int page = Converter.toInt(request.getParameter(PARAM_NAME_PAGE_NUMBER));
        int offset = (page - 1) * limit;

        if (offset < 0 || limit < 0) {
            throw new CommandException("Invalid limit or offset parameter");
        }


        ServiceResponse<MusicSearchResultsContainer<List<AudioTrackDto>>> serviceResponse;
        try {
            serviceResponse = service.findTracksForMusicSearchPage(
                    searchString,
                    userId,
                    limit,
                    offset);
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

    private void appendServiceProvidedData(
            ServiceResponse<MusicSearchResultsContainer<List<AudioTrackDto>>> serviceResponse,
            JsonSelfWrapper json) {

        json.openObject("results_quantity");
        json.appendNumber("artists", serviceResponse.getStoredValue().getArtistsFound());
        json.appendNumber("albums", serviceResponse.getStoredValue().getAlbumsFound());
        json.appendNumber("tracks", serviceResponse.getStoredValue().getAudioTracksFound());
        json.closeObject();

        json.openArray("tracks");
        for (AudioTrackDto dto : serviceResponse.getStoredValue().getStoredValue()) {
            json.openObject();
            json.appendNumber("artist_id", dto.getArtistId());
            json.appendString("artist_name", dto.getArtistName());
            json.appendString("artist_image", dto.getArtistImageName());

            json.appendNumber("album_id", dto.getAlbumId());
            json.appendString("album_name", dto.getAlbumName());
            json.appendString("album_image", dto.getAlbumImageName());
            json.appendNumber("album_year", dto.getAlbumYear());

            json.appendNumber("track_id", dto.getTrackId());
            json.appendString("track_name", dto.getTrackName());
            json.appendString("track_file", dto.getTrackFileName());
            json.appendNumber("track_number", dto.getTrackNumber());
            json.appendBoolean("favourite", dto.isFavourite());
            json.closeObject();
        }
        json.closeArray();

    }
}
