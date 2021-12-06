package by.musicwaves.dao;

import by.musicwaves.entity.Playlist;

import java.util.List;

public interface PlaylistDao extends Dao<Playlist> {

    boolean rename(int userId, int playlistId, String playlistName) throws DaoException;

    void delete(int userId, int... playlistsId) throws DaoException;

    List<Playlist> getUserPlaylists(int userId) throws DaoException;
}
