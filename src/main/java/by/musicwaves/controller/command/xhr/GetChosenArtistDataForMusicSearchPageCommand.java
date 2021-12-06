package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.Converter;
import by.musicwaves.dto.FoundAlbumForMusicSearchPageDTO;
import by.musicwaves.dto.FoundTrackForMusicSearchPageDTO;
import by.musicwaves.dto.MusicSearchPageResultsQuantityContainer;
import by.musicwaves.entity.Artist;
import by.musicwaves.entity.User;
import by.musicwaves.service.CrossEntityService;
import by.musicwaves.service.ServiceException;
import by.musicwaves.service.ServiceResponse;
import by.musicwaves.util.JsonSelfWrapper;
import by.musicwaves.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class GetChosenArtistDataForMusicSearchPageCommand extends XHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(GetChosenArtistDataForMusicSearchPageCommand.class);
    private final static CrossEntityService service = CrossEntityService.getInstance();

    private final static String PARAM_NAME_ARTIST_ID = "artist_id";
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

        int artistId = Converter.toInt(request.getParameter(PARAM_NAME_ARTIST_ID));
        int limit = Converter.toInt(request.getParameter(PARAM_NAME_LIMIT));
        int page = Converter.toInt(request.getParameter(PARAM_NAME_PAGE_NUMBER));
        int offset = (page - 1) * limit;

        if (offset < 0 || limit < 0) {
            throw new CommandException("Invalid limit or offset parameter");
        }


        ServiceResponse<Pair<Artist, List<FoundAlbumForMusicSearchPageDTO>>> serviceResponse;
        try {
            serviceResponse = service.findChosenArtistAlbumsForMusicSearchPage(
                    userId,
                    artistId,
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
            ServiceResponse<Pair<Artist, List<FoundAlbumForMusicSearchPageDTO>>> serviceResponse,
            JsonSelfWrapper json) {

        Artist artist = serviceResponse.getStoredValue().getFirstValue();

        json.openObject("artist");
        json.appendString("name", artist.getName());
        json.appendString("image", artist.getImageName());
        json.closeObject();

        List<FoundAlbumForMusicSearchPageDTO> albums = serviceResponse.getStoredValue().getSecondValue();

        json.openArray("albums");
        for (FoundAlbumForMusicSearchPageDTO dto : albums) {
            json.openObject();
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
