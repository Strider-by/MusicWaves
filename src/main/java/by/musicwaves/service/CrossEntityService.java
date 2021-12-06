package by.musicwaves.service;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dto.*;
import by.musicwaves.dao.factory.CrossEntityDaoFactory;
import by.musicwaves.dao.CrossEntityDao;
import by.musicwaves.entity.Album;
import by.musicwaves.entity.Artist;
import by.musicwaves.util.Pair;
import by.musicwaves.util.Triplet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Locale;

public class CrossEntityService {

    private static final CrossEntityService service = new CrossEntityService();
    private static final CrossEntityDao dao = CrossEntityDaoFactory.getInstance();
    private final static Logger LOGGER = LogManager.getLogger(CrossEntityService.class);

    private CrossEntityService() {
    }

    public static CrossEntityService getInstance() {
        return service;
    }

    public final ServiceResponse<
            MusicSearchPageResultsQuantityContainer<
                    List<FoundArtistForMusicSearchPageDTO>>> findArtistsForMusicSearchPage(
                            String searchString, int userId, int limit, int offset) throws ServiceException {

        try {
            MusicSearchPageResultsQuantityContainer<List<FoundArtistForMusicSearchPageDTO>> daoResponse
                    = dao.findArtistsForMusicSearchPage(searchString, userId, limit, offset);
            return new ServiceResponse<>(daoResponse);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }
    }

    public ServiceResponse<MusicSearchPageResultsQuantityContainer<?>> getSearchResultsCountForMusicSearchPage(
            String searchString) throws ServiceException {

        try {
            MusicSearchPageResultsQuantityContainer<?> daoResponse = dao.getSearchResultsCountForMusicSearchPage(searchString);
            return new ServiceResponse<>(daoResponse);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }
    }

    public ServiceResponse<
            MusicSearchPageResultsQuantityContainer<
                    List<FoundAlbumForMusicSearchPageDTO>>> findAlbumsForMusicSearchPage(
                                    String searchString, int userId, int limit, int offset) throws ServiceException {

        try {
            MusicSearchPageResultsQuantityContainer<List<FoundAlbumForMusicSearchPageDTO>> daoResponse
                    = dao.findAlbumsForMusicSearchPage(searchString, userId, limit, offset);
            return new ServiceResponse<>(daoResponse);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }
    }

    public ServiceResponse<
            MusicSearchPageResultsQuantityContainer<
                    List<FoundTrackForMusicSearchPageDTO>>> findTracksForMusicSearchPage(
                            String searchString, int userId, int limit, int offset) throws ServiceException {

        try {
            MusicSearchPageResultsQuantityContainer<List<FoundTrackForMusicSearchPageDTO>> daoResponse
                    = dao.findTracksForMusicSearchPage(searchString, userId, limit, offset);
            return new ServiceResponse<>(daoResponse);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }
    }

    public ServiceResponse<Pair<Artist, List<FoundAlbumForMusicSearchPageDTO>>> findChosenArtistAlbumsForMusicSearchPage(
            int userId, int artistId, int limit, int offset) throws ServiceException {

        ServiceResponse<Pair<Artist, List<FoundAlbumForMusicSearchPageDTO>>> serviceResponse = new ServiceResponse<>();
        try {
            serviceResponse.setStoredValue(dao.findChosenArtistDataForMusicSearchPage(userId, artistId, limit, offset));
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return serviceResponse;
    }

    public ServiceResponse<Triplet<Artist, Album, List<FoundTrackForMusicSearchPageDTO>>> findChosenAlbumTracksForMusicSearchPage(
            int userId, int albumId, int limit, int offset) throws ServiceException {

        ServiceResponse<Triplet<Artist, Album, List<FoundTrackForMusicSearchPageDTO>>> serviceResponse = new ServiceResponse<>();
        try {
            serviceResponse.setStoredValue(dao.findChosenAlbumDataForMusicSearchPage(userId, albumId, limit, offset));
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return serviceResponse;
    }

    public ServiceResponse<?> setArtistAsFavourite(int userId, int artistId) throws ServiceException {

        try {
            dao.setArtistAsFavourite(userId, artistId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>();
    }

    public ServiceResponse<?> unsetArtistAsFavourite(int userId, int artistId) throws ServiceException {

        try {
            dao.unsetArtistAsFavourite(userId, artistId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>();
    }

    public ServiceResponse<?> setAlbumAsFavourite(int userId, int albumId) throws ServiceException {

        try {
            dao.setAlbumAsFavourite(userId, albumId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>();
    }

    public ServiceResponse<?> unsetAlbumAsFavourite(int userId, int albumId) throws ServiceException {

        try {
            dao.unsetAlbumAsFavourite(userId, albumId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>();
    }

    public ServiceResponse<?> setAudioTrackAsFavourite(int userId, int trackId) throws ServiceException {

        try {
            dao.setAudioTrackAsFavourite(userId, trackId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>();
    }

    public ServiceResponse<?> unsetAudioTrackAsFavourite(int userId, int trackId) throws ServiceException {

        try {
            dao.unsetAudioTrackAsFavourite(userId, trackId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>();
    }

    public ServiceResponse<List<PlaylistItemDto>> getPlaylistTracks(int userId, int playlistId, Locale locale) throws ServiceException {
        List<PlaylistItemDto> playlistItemDtos;
        try {
            playlistItemDtos = dao.getPlaylistTracks(userId, playlistId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }
        return new ServiceResponse<>(playlistItemDtos);
    }

    public ServiceResponse<?> recordPlaylistElements(int userId, int playlistId, int[] tracksIds, Locale locale) throws ServiceException {
        try {
            dao.recordPlaylistItems(userId, playlistId, tracksIds);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>();
    }

    public ServiceResponse<FoundTrackForMusicSearchPageDTO> getAudioTrackDataById(int trackId) throws ServiceException {
        FoundTrackForMusicSearchPageDTO trackData;
        try {
            trackData = dao.getAudioTrackDataById(trackId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>(trackData);
    }

    public ServiceResponse<List<FoundTrackForMusicSearchPageDTO>> getAudioTracksData(int[] tracksIds, Locale locale) throws ServiceException {
        List<FoundTrackForMusicSearchPageDTO> tracksData;
        try {
            tracksData = dao.getAudioTracksData(tracksIds);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>(tracksData);
    }
}
