package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.Converter;
import by.musicwaves.dto.FoundAlbumForMusicSearchPageDTO;
import by.musicwaves.dto.FoundTrackForMusicSearchPageDTO;
import by.musicwaves.entity.Album;
import by.musicwaves.entity.Artist;
import by.musicwaves.entity.User;
import by.musicwaves.service.CrossEntityService;
import by.musicwaves.service.ServiceException;
import by.musicwaves.service.ServiceResponse;
import by.musicwaves.util.JsonSelfWrapper;
import by.musicwaves.util.Pair;
import by.musicwaves.util.Triplet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class GetChosenAlbumDataForMusicSearchPageCommand extends XHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(GetChosenAlbumDataForMusicSearchPageCommand.class);
    private final static CrossEntityService service = CrossEntityService.getInstance();

    private final static String PARAM_NAME_ALBUM_ID = "album_id";
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

        int albumId = Converter.toInt(request.getParameter(PARAM_NAME_ALBUM_ID));
        int limit = Converter.toInt(request.getParameter(PARAM_NAME_LIMIT));
        int page = Converter.toInt(request.getParameter(PARAM_NAME_PAGE_NUMBER));
        int offset = (page - 1) * limit;

        if (offset < 0 || limit < 0) {
            throw new CommandException("Invalid limit or offset parameter");
        }


        ServiceResponse<Triplet<Artist, Album, List<FoundTrackForMusicSearchPageDTO>>> serviceResponse;
        try {
            serviceResponse = service.findChosenAlbumTracksForMusicSearchPage(
                    userId,
                    albumId,
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
            ServiceResponse<Triplet<Artist, Album, List<FoundTrackForMusicSearchPageDTO>>> serviceResponse,
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

        List<FoundTrackForMusicSearchPageDTO> tracks = serviceResponse.getStoredValue().getThirdValue();

        json.openArray("tracks");
        for (FoundTrackForMusicSearchPageDTO dto : tracks) {
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
