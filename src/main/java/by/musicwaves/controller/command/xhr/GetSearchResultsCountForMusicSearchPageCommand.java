package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.exception.ValidationException;
import by.musicwaves.controller.util.Validator;
import by.musicwaves.controller.util.AccessLevelEnum;
import by.musicwaves.dto.MusicSearchResultsContainer;
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
import java.util.Locale;

public class GetSearchResultsCountForMusicSearchPageCommand extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(GetSearchResultsCountForMusicSearchPageCommand.class);
    private final static CrossEntityService service = ServiceFactory.getInstance().getCrossEntityService();
    private final static String PARAM_NAME_SEARCH_STRING = "search_string";

    public GetSearchResultsCountForMusicSearchPageCommand(AccessLevelEnum accessLevelEnum) {
        super(accessLevelEnum);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException {

        User user = getUser(request);
        Locale locale = user.getLanguage().getLocale();
        String searchString = Validator.assertNonNull(request.getParameter(PARAM_NAME_SEARCH_STRING));

        ServiceResponse<MusicSearchResultsContainer<?>> serviceResponse;
        try {
            serviceResponse = service.getSearchResultsCountForMusicSearchPage(searchString, locale);
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
            ServiceResponse<MusicSearchResultsContainer<?>> serviceResponse,
            JsonSelfWrapper json) {

        json.openObject("results_quantity");
        json.appendNumber("artists", serviceResponse.getStoredValue().getArtistsFound());
        json.appendNumber("albums", serviceResponse.getStoredValue().getAlbumsFound());
        json.appendNumber("tracks", serviceResponse.getStoredValue().getAudioTracksFound());
        json.closeObject();

    }
}
