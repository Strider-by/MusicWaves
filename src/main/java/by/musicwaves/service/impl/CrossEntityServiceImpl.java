package by.musicwaves.service.impl;

import by.musicwaves.dao.exception.DaoException;
import by.musicwaves.dto.*;
import by.musicwaves.dao.factory.CrossEntityDaoFactory;
import by.musicwaves.dao.CrossEntityDao;
import by.musicwaves.entity.Album;
import by.musicwaves.entity.Artist;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.service.CrossEntityService;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.util.Pair;
import by.musicwaves.util.Triplet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Locale;

public class CrossEntityServiceImpl implements CrossEntityService {

    private static final CrossEntityServiceImpl service = new CrossEntityServiceImpl();
    private final CrossEntityDao dao = CrossEntityDaoFactory.getInstance();
    private final static Logger LOGGER = LogManager.getLogger(CrossEntityServiceImpl.class);

    private CrossEntityServiceImpl() {
    }

    public static CrossEntityServiceImpl getInstance() {
        return service;
    }

    @Override
    public final ServiceResponse<
            MusicSearchResultsContainer<
                                    List<ArtistDto>>> findArtistsForMusicSearchPage(
                            String searchString, int userId, int limit, int offset, Locale locale) throws ServiceException {

        try {
            MusicSearchResultsContainer<List<ArtistDto>> daoResponse
                    = dao.findArtistsForMusicSearchPage(searchString, userId, limit, offset);
            return new ServiceResponse<>(daoResponse);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }
    }

    @Override
    public ServiceResponse<MusicSearchResultsContainer<?>> getSearchResultsCountForMusicSearchPage(
            String searchString, Locale locale) throws ServiceException {

        try {
            MusicSearchResultsContainer<?> daoResponse = dao.getSearchResultsCountForMusicSearchPage(searchString);
            return new ServiceResponse<>(daoResponse);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }
    }

    @Override
    public ServiceResponse<
            MusicSearchResultsContainer<
                                List<AlbumDto>>> findAlbumsForMusicSearchPage(
                                    String searchString, int userId, int limit, int offset, Locale locale) throws ServiceException {

        try {
            MusicSearchResultsContainer<List<AlbumDto>> daoResponse
                    = dao.findAlbumsForMusicSearchPage(searchString, userId, limit, offset);
            return new ServiceResponse<>(daoResponse);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }
    }

    @Override
    public ServiceResponse<
            MusicSearchResultsContainer<
                                List<AudioTrackDto>>> findTracksForMusicSearchPage(
                            String searchString, int userId, int limit, int offset, Locale locale) throws ServiceException {

        try {
            MusicSearchResultsContainer<List<AudioTrackDto>> daoResponse
                    = dao.findTracksForMusicSearchPage(searchString, userId, limit, offset);
            return new ServiceResponse<>(daoResponse);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }
    }

    @Override
    public ServiceResponse<Pair<Artist, List<AlbumDto>>> findChosenArtistAlbumsForMusicSearchPage(
            int userId, int artistId, int limit, int offset, Locale locale) throws ServiceException {

        ServiceResponse<Pair<Artist, List<AlbumDto>>> serviceResponse = new ServiceResponse<>();
        try {
            serviceResponse.setStoredValue(dao.findChosenArtistDataForMusicSearchPage(userId, artistId, limit, offset));
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return serviceResponse;
    }

    @Override
    public ServiceResponse<Triplet<Artist, Album, List<AudioTrackDto>>> findChosenAlbumTracksForMusicSearchPage(
            int userId, int albumId, int limit, int offset, Locale locale) throws ServiceException {

        ServiceResponse<Triplet<Artist, Album, List<AudioTrackDto>>> serviceResponse = new ServiceResponse<>();
        try {
            serviceResponse.setStoredValue(dao.findChosenAlbumDataForMusicSearchPage(userId, albumId, limit, offset));
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return serviceResponse;
    }

    @Override
    public ServiceResponse<?> setArtistAsFavourite(int userId, int artistId, Locale locale) throws ServiceException {

        try {
            dao.setArtistAsFavourite(userId, artistId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>();
    }

    @Override
    public ServiceResponse<?> unsetArtistAsFavourite(int userId, int artistId) throws ServiceException {

        try {
            dao.unsetArtistAsFavourite(userId, artistId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>();
    }

    @Override
    public ServiceResponse<?> setAlbumAsFavourite(int userId, int albumId, Locale locale) throws ServiceException {

        try {
            dao.setAlbumAsFavourite(userId, albumId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>();
    }

    @Override
    public ServiceResponse<?> unsetAlbumAsFavourite(int userId, int albumId, Locale locale) throws ServiceException {

        try {
            dao.unsetAlbumAsFavourite(userId, albumId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>();
    }

    @Override
    public ServiceResponse<?> setAudioTrackAsFavourite(int userId, int trackId, Locale locale) throws ServiceException {

        try {
            dao.setAudioTrackAsFavourite(userId, trackId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>();
    }

    @Override
    public ServiceResponse<?> unsetAudioTrackAsFavourite(int userId, int trackId) throws ServiceException {

        try {
            dao.unsetAudioTrackAsFavourite(userId, trackId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>();
    }

    @Override
    public ServiceResponse<List<PlaylistItemDto>> getPlaylistTracks(int userId, int playlistId, Locale locale) throws ServiceException {
        List<PlaylistItemDto> playlistItemDtos;
        try {
            playlistItemDtos = dao.getPlaylistTracks(userId, playlistId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }
        return new ServiceResponse<>(playlistItemDtos);
    }

    @Override
    public ServiceResponse<?> recordPlaylistElements(int userId, int playlistId, int[] tracksIds, Locale locale) throws ServiceException {
        try {
            dao.recordPlaylistItems(userId, playlistId, tracksIds);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>();
    }

    @Override
    public ServiceResponse<AudioTrackDto> getAudioTrackDataById(int trackId, Locale locale) throws ServiceException {
        AudioTrackDto trackData;
        try {
            trackData = dao.getAudioTrackDataById(trackId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>(trackData);
    }

    @Override
    public ServiceResponse<List<AudioTrackDto>> getAudioTracksData(int[] tracksIds, Locale locale) throws ServiceException {
        List<AudioTrackDto> tracksData;
        try {
            tracksData = dao.getAudioTracksData(tracksIds);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>(tracksData);
    }
}
