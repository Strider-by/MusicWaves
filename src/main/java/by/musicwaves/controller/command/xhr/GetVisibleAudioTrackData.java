package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.util.Converter;
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

public class GetVisibleAudioTrackData extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(GetVisibleAudioTrackData.class);
    private final static CrossEntityService service = ServiceFactory.getInstance().getCrossEntityService();

    private final static String PARAM_NAME_TRACK_ID = "track_id";

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, CommandException {

        // user must be logged in
        User user = getUser(request);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        int trackId = Converter.toInt(request.getParameter(PARAM_NAME_TRACK_ID));


        ServiceResponse<AudioTrackDto> serviceResponse;
        try {
            serviceResponse = service.getAudioTrackDataById(trackId);
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

    private void appendServiceProvidedData(ServiceResponse<AudioTrackDto> serviceResponse, JsonSelfWrapper json) {

        AudioTrackDto dto = serviceResponse.getStoredValue();
        if (dto == null) {
            json.appendString(JSON_DATA_OBJECT_NAME, null);
            return;
        }

        json.openObject(JSON_DATA_OBJECT_NAME);

        json.openObject("artist");
        json.appendNumber("id", dto.getArtistId());
        json.appendString("name", dto.getArtistName());
        json.appendString("image", dto.getArtistImageName());
        json.closeObject();

        json.openObject("album");
        json.appendNumber("id", dto.getAlbumId());
        json.appendString("name", dto.getAlbumName());
        json.appendString("image", dto.getAlbumImageName());
        json.appendNumber("year", dto.getAlbumYear());
        json.closeObject();

        json.openObject("track");
        json.appendNumber("id", dto.getTrackId());
        json.appendString("name", dto.getTrackName());
        json.appendString("file", dto.getTrackFileName());
        json.closeObject();

        json.closeObject();

    }
}
