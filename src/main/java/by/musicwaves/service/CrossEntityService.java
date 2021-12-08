package by.musicwaves.service;

import by.musicwaves.dto.*;
import by.musicwaves.entity.Album;
import by.musicwaves.entity.Artist;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.util.Pair;
import by.musicwaves.util.Triplet;

import java.util.List;
import java.util.Locale;

public interface CrossEntityService {
    ServiceResponse<
            MusicSearchResultsContainer<
                    List<ArtistDto>>> findArtistsForMusicSearchPage(
            String searchString, int userId, int limit, int offset) throws ServiceException;

    ServiceResponse<MusicSearchResultsContainer<?>> getSearchResultsCountForMusicSearchPage(
            String searchString) throws ServiceException;

    ServiceResponse<
            MusicSearchResultsContainer<
                                List<AlbumDto>>> findAlbumsForMusicSearchPage(
            String searchString, int userId, int limit, int offset) throws ServiceException;

    ServiceResponse<
            MusicSearchResultsContainer<
                                List<AudioTrackDto>>> findTracksForMusicSearchPage(
            String searchString, int userId, int limit, int offset) throws ServiceException;

    ServiceResponse<Pair<Artist, List<AlbumDto>>> findChosenArtistAlbumsForMusicSearchPage(
            int userId, int artistId, int limit, int offset) throws ServiceException;

    ServiceResponse<Triplet<Artist, Album, List<AudioTrackDto>>> findChosenAlbumTracksForMusicSearchPage(
            int userId, int albumId, int limit, int offset) throws ServiceException;

    ServiceResponse<?> setArtistAsFavourite(int userId, int artistId) throws ServiceException;

    ServiceResponse<?> unsetArtistAsFavourite(int userId, int artistId) throws ServiceException;

    ServiceResponse<?> setAlbumAsFavourite(int userId, int albumId) throws ServiceException;

    ServiceResponse<?> unsetAlbumAsFavourite(int userId, int albumId) throws ServiceException;

    ServiceResponse<?> setAudioTrackAsFavourite(int userId, int trackId) throws ServiceException;

    ServiceResponse<?> unsetAudioTrackAsFavourite(int userId, int trackId) throws ServiceException;

    ServiceResponse<List<PlaylistItemDto>> getPlaylistTracks(int userId, int playlistId, Locale locale) throws ServiceException;

    ServiceResponse<?> recordPlaylistElements(int userId, int playlistId, int[] tracksIds, Locale locale) throws ServiceException;

    ServiceResponse<AudioTrackDto> getAudioTrackDataById(int trackId) throws ServiceException;

    ServiceResponse<List<AudioTrackDto>> getAudioTracksData(int[] tracksIds, Locale locale) throws ServiceException;
}
