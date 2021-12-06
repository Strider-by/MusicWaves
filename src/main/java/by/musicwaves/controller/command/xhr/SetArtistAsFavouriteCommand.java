package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.Converter;
import by.musicwaves.dto.FoundTrackForMusicSearchPageDTO;
import by.musicwaves.entity.Album;
import by.musicwaves.entity.Artist;
import by.musicwaves.entity.User;
import by.musicwaves.service.CrossEntityService;
import by.musicwaves.service.ServiceException;
import by.musicwaves.service.ServiceResponse;
import by.musicwaves.util.JsonSelfWrapper;
import by.musicwaves.util.Triplet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class SetArtistAsFavouriteCommand extends XHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(SetArtistAsFavouriteCommand.class);
    private final static CrossEntityService service = CrossEntityService.getInstance();

    private final static String PARAM_NAME_ARTIST_ID = "artist_id";

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


        ServiceResponse<?> serviceResponse;
        try {
            serviceResponse = service.setArtistAsFavourite(
                    userId,
                    artistId);
        } catch (ServiceException ex) {
            throw new CommandException(ex);
        }

        JsonSelfWrapper json = new JsonSelfWrapper();
        json.openJson();


        appendServiceExecutionResult(serviceResponse, json);
        appendServiceMessages(serviceResponse, json);

        json.closeJson();
        response.getWriter().write(json.toString());
    }
}
