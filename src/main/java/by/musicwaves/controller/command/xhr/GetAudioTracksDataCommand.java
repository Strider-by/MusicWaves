package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.dto.AudioTrackDto;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GetAudioTracksDataCommand extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(GetAudioTracksDataCommand.class);
    private final static CrossEntityService service = ServiceFactory.getInstance().getCrossEntityService();

    private final static String PARAM_NAME_TRACKS_IDS = "tracks_id[]";

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, CommandException {

        // user must be logged in
        User user = getUser(request);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        Locale locale = user.getLanguage().getLocale();

        int[] tracksIds;
        try {
            String[] tracksIdsStrings = request.getParameterValues(PARAM_NAME_TRACKS_IDS);
            tracksIds = Arrays.stream(tracksIdsStrings)
                    .mapToInt(Integer::parseInt)
                    .toArray();
        } catch (NullPointerException | NumberFormatException ex) {
            LOGGER.warn("Provided request parameters are not valid", ex);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (tracksIds.length == 0) {
            LOGGER.warn("We got an empty ids list");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        ServiceResponse<List<AudioTrackDto>> serviceResponse;
        try {
            serviceResponse = service.getAudioTracksData(
                    tracksIds, locale);
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
            ServiceResponse<List<AudioTrackDto>> serviceResponse,
            JsonSelfWrapper json) {

        json.openArray("tracks");
        for (AudioTrackDto dto : serviceResponse.getStoredValue()) {
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
