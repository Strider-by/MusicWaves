package by.musicwaves.service;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dto.FoundArtistForMusicSearchPageDTO;
import by.musicwaves.dto.MusicSearchPageResultsQuantityContainer;
import by.musicwaves.dao.factory.CrossEntityDaoFactory;
import by.musicwaves.dao.util.CrossEntityDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class CrossEntityService {

    private static final CrossEntityService service = new CrossEntityService();
    private static final CrossEntityDao dao = CrossEntityDaoFactory.getInstance();
    private final static Logger LOGGER = LogManager.getLogger(CrossEntityService.class);

    private CrossEntityService() {
    }

    public static CrossEntityService getInstance() {
        return service;
    }

    public final ServiceResponse<MusicSearchPageResultsQuantityContainer<List<FoundArtistForMusicSearchPageDTO>>> findArtistsForMusicSearchPage(
            String searchString, int userId, int limit, int offset) throws ServiceException {

        try {
            MusicSearchPageResultsQuantityContainer<List<FoundArtistForMusicSearchPageDTO>> doaResponse
                    = dao.findArtistsForMusicSearchPage(searchString, userId, limit, offset);
            return new ServiceResponse<>(doaResponse);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }
    }

}
