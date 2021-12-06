package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.Converter;
import by.musicwaves.dto.FoundArtistForMusicSearchPageDTO;
import by.musicwaves.dto.MusicSearchPageResultsQuantityContainer;
import by.musicwaves.entity.User;
import by.musicwaves.service.CrossEntityService;
import by.musicwaves.service.ServiceException;
import by.musicwaves.service.ServiceResponse;
import by.musicwaves.util.JsonSelfWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class GetSearchResultsCountForMusicSearchPageCommand extends XHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(GetSearchResultsCountForMusicSearchPageCommand.class);
    private final static CrossEntityService service = CrossEntityService.getInstance();

    private final static String PARAM_NAME_SEARCH_STRING = "search_string";

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, CommandException {

        // user must be logged in
        User user = getUser(request);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String searchString = request.getParameter(PARAM_NAME_SEARCH_STRING);

        ServiceResponse <MusicSearchPageResultsQuantityContainer<?>> serviceResponse;
        try {
            serviceResponse = service.getSearchResultsCountForMusicSearchPage(searchString);
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
            ServiceResponse<MusicSearchPageResultsQuantityContainer<?>> serviceResponse,
            JsonSelfWrapper json) {

        json.openObject("results_quantity");
        json.appendNumber("artists", serviceResponse.getStoredValue().getArtistsFound());
        json.appendNumber("albums", serviceResponse.getStoredValue().getAlbumsFound());
        json.appendNumber("tracks", serviceResponse.getStoredValue().getAudioTracksFound());
        json.closeObject();

    }
}
