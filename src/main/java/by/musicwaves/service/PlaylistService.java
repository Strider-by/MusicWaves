package by.musicwaves.service;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.PlaylistDao;
import by.musicwaves.dao.factory.PlaylistDaoFactory;
import by.musicwaves.dto.PlaylistItemDto;
import by.musicwaves.entity.Playlist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Locale;

public class PlaylistService {

    private static final PlaylistService service = new PlaylistService();
    private static final PlaylistDao dao = PlaylistDaoFactory.getInstance();
    private final static Logger LOGGER = LogManager.getLogger(PlaylistService.class);

    private PlaylistService() {
    }

    public static PlaylistService getInstance() {
        return service;
    }


    public ServiceResponse<Playlist> createPlaylist(int userId, String playlistName, Locale locale) throws ServiceException {

        // todo: restriction for playlist name?
        Playlist playlist = new Playlist();
        playlist.setUserId(userId);
        playlist.setName(playlistName);
        try {
            int id = dao.create(playlist);
            playlist.setId(id);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>(playlist);
    }


    public ServiceResponse<?> deleteMultiplePlaylists(int userId, int[] playlistsIds, Locale locale) throws ServiceException {
        try {
            dao.delete(userId, playlistsIds);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>();
    }

    public ServiceResponse<List<Playlist>> getUserPlaylists(int userId, Locale locale) throws ServiceException {
        List<Playlist> daoResponse;
        try {
            daoResponse = dao.getUserPlaylists(userId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>(daoResponse);
    }
}
