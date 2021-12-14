package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.controller.command.exception.ValidationException;
import by.musicwaves.controller.command.util.Converter;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.dto.AudioTrackDto;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.Album;
import by.musicwaves.entity.Artist;
import by.musicwaves.entity.User;
import by.musicwaves.service.CrossEntityService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.factory.ServiceFactory;
import by.musicwaves.util.JsonSelfWrapper;
import by.musicwaves.util.Triplet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

public class GetChosenAlbumDataForMusicSearchPageCommand extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(GetChosenAlbumDataForMusicSearchPageCommand.class);
    private final static CrossEntityService service = ServiceFactory.getInstance().getCrossEntityService();
    private final static String PARAM_NAME_ALBUM_ID = "album_id";
    private final static String PARAM_NAME_PAGE_NUMBER = "page";
    private final static String PARAM_NAME_LIMIT = "limit";

    public GetChosenAlbumDataForMusicSearchPageCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
        Locale locale = user.getLanguage().getLocale();
        int userId = user.getId();
        int albumId = Converter.toInt(request.getParameter(PARAM_NAME_ALBUM_ID));
        int limit = Converter.toInt(request.getParameter(PARAM_NAME_LIMIT));
        int page = Converter.toInt(request.getParameter(PARAM_NAME_PAGE_NUMBER));
        int offset = (page - 1) * limit;

        if (offset < 0 || limit < 0) {
            throw new ValidationException("Invalid limit or offset parameter");
        }

        ServiceResponse<Triplet<Artist, Album, List<AudioTrackDto>>> serviceResponse;
        try {
            serviceResponse = service.findChosenAlbumTracksForMusicSearchPage(
                    userId,
                    albumId,
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
            ServiceResponse<Triplet<Artist, Album, List<AudioTrackDto>>> serviceResponse,
            JsonSelfWrapper json) {

        Artist artist = serviceResponse.getStoredValue().getFirstValue();
        Album album = serviceResponse.getStoredValue().getSecondValue();

        json.openObject("artist");
        json.appendString("name", artist.getName());
        json.appendString("image", artist.getImageName());
        json.closeObject();

        json.openObject("album");
        json.appendString("name", album.getName());
        json.appendString("image", album.getImageName());
        json.appendNumber("year", album.getYear());
        json.closeObject();

        List<AudioTrackDto> tracks = serviceResponse.getStoredValue().getThirdValue();

        json.openArray("tracks");
        for (AudioTrackDto dto : tracks) {
            json.openObject();
            json.appendNumber("id", dto.getTrackId());
            json.appendString("name", dto.getTrackName());
            json.appendString("file", dto.getTrackFileName());
            json.appendBoolean("favourite", dto.isFavourite());
            json.closeObject();
        }
        json.closeArray();

    }
}
