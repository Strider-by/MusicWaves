package by.musicwaves.service;

import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.Playlist;
import by.musicwaves.service.exception.ServiceException;

import java.util.List;
import java.util.Locale;

public interface PlaylistService {
    ServiceResponse<Playlist> createPlaylist(int userId, String playlistName, Locale locale) throws ServiceException;

    ServiceResponse<?> deleteMultiplePlaylists(int userId, int[] playlistsIds, Locale locale) throws ServiceException;

    ServiceResponse<List<Playlist>> getUserPlaylists(int userId, Locale locale) throws ServiceException;
}
