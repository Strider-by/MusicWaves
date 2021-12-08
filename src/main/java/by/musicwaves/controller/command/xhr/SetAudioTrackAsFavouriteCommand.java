package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.util.Converter;
import by.musicwaves.controller.command.exception.CommandException;
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

public class SetAudioTrackAsFavouriteCommand extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(SetAudioTrackAsFavouriteCommand.class);
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
        int userId = user.getId();

        int trackId = Converter.toInt(request.getParameter(PARAM_NAME_TRACK_ID));


        ServiceResponse<?> serviceResponse;
        try {
            serviceResponse = service.setAudioTrackAsFavourite(
                    userId,
                    trackId);
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
