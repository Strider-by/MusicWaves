package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.Converter;
import by.musicwaves.entity.Role;
import by.musicwaves.entity.User;
import by.musicwaves.service.AlbumService;
import by.musicwaves.service.ArtistService;
import by.musicwaves.service.ServiceException;
import by.musicwaves.service.ServiceResponse;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

public class DeleteAlbumCommand extends XHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(DeleteAlbumCommand.class);
    private final static AlbumService service = AlbumService.getInstance();

    private final static String PARAM_NAME_ID = "id";

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, CommandException {

        // user must be logged in and it must be an administrator or a curator
        User user = getUser(request);
        if (user == null || (user.getRole() != Role.ADMINISTRATOR && user.getRole() != Role.MUSIC_CURATOR)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        Locale locale = user.getLanguage().getLocale();

        int id = Converter.toInt(request.getParameter(PARAM_NAME_ID));


        ServiceResponse<?> serviceResponse;
        try {
            serviceResponse = service.deleteAlbum(id, locale);
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
