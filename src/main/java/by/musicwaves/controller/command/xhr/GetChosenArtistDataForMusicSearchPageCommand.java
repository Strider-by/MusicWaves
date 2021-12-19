package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.exception.ValidationException;
import by.musicwaves.controller.util.Converter;
import by.musicwaves.controller.util.AccessLevelEnum;
import by.musicwaves.dto.AlbumDto;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.Artist;
import by.musicwaves.entity.User;
import by.musicwaves.service.CrossEntityService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;
import by.musicwaves.util.JsonSelfWrapper;
import by.musicwaves.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

public class GetChosenArtistDataForMusicSearchPageCommand extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(GetChosenArtistDataForMusicSearchPageCommand.class);
    private final static CrossEntityService service = ServiceFactory.getInstance().getCrossEntityService();
    private final static String PARAM_NAME_ARTIST_ID = "artist_id";
    private final static String PARAM_NAME_PAGE_NUMBER = "page";
    private final static String PARAM_NAME_LIMIT = "limit";

    public GetChosenArtistDataForMusicSearchPageCommand(AccessLevelEnum accessLevelEnum) {
        super(accessLevelEnum);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
        Locale locale = user.getLanguage().getLocale();
        int userId = user.getId();
        int artistId = Converter.toInt(request.getParameter(PARAM_NAME_ARTIST_ID));
        int limit = Converter.toInt(request.getParameter(PARAM_NAME_LIMIT));
        int page = Converter.toInt(request.getParameter(PARAM_NAME_PAGE_NUMBER));
        int offset = (page - 1) * limit;

        if (offset < 0 || limit < 0) {
            throw new ValidationException("Invalid limit or offset parameter");
        }

        ServiceResponse<Pair<Artist, List<AlbumDto>>> serviceResponse;
        try {
            serviceResponse = service.findChosenArtistAlbumsForMusicSearchPage(
                    userId,
                    artistId,
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
            ServiceResponse<Pair<Artist, List<AlbumDto>>> serviceResponse,
            JsonSelfWrapper json) {

        Artist artist = serviceResponse.getStoredValue().getFirstValue();

        json.openObject("artist");
        json.appendString("name", artist.getName());
        json.appendString("image", artist.getImageName());
        json.closeObject();

        List<AlbumDto> albums = serviceResponse.getStoredValue().getSecondValue();

        json.openArray("albums");
        for (AlbumDto dto : albums) {
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
