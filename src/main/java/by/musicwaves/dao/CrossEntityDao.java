package by.musicwaves.dao;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dto.*;
import by.musicwaves.entity.Album;
import by.musicwaves.entity.Artist;
import by.musicwaves.util.Pair;
import by.musicwaves.util.Triplet;

import java.util.List;

public interface CrossEntityDao {
    MusicSearchPageResultsQuantityContainer<List<FoundArtistForMusicSearchPageDTO>> findArtistsForMusicSearchPage(
            String searchString, int userId, int limit, int offset) throws DaoException;

    MusicSearchPageResultsQuantityContainer<?> getSearchResultsCountForMusicSearchPage(String searchString) throws DaoException;

    MusicSearchPageResultsQuantityContainer<List<FoundAlbumForMusicSearchPageDTO>> findAlbumsForMusicSearchPage(
            String searchString, int userId, int limit, int offset) throws DaoException;

    MusicSearchPageResultsQuantityContainer<List<FoundTrackForMusicSearchPageDTO>> findTracksForMusicSearchPage(
            String searchString, int userId, int limit, int offset) throws DaoException;

    Pair<Artist, List<FoundAlbumForMusicSearchPageDTO>> findChosenArtistDataForMusicSearchPage(
            int userId, int artistId, int limit, int offset) throws DaoException;

    Triplet<Artist, Album, List<FoundTrackForMusicSearchPageDTO>> findChosenAlbumDataForMusicSearchPage(
            int userId, int albumId, int limit, int offset) throws DaoException;

    void setArtistAsFavourite(int userId, int artistId) throws DaoException;

    void unsetArtistAsFavourite(int userId, int artistId) throws DaoException;

    void setAlbumAsFavourite(int userId, int albumId) throws DaoException;

    void unsetAlbumAsFavourite(int userId, int albumId) throws DaoException;

    void setAudioTrackAsFavourite(int userId, int trackId) throws DaoException;

    void unsetAudioTrackAsFavourite(int userId, int trackId) throws DaoException;

    List<PlaylistItemDto> getPlaylistTracks(int userId, int playlistId) throws DaoException;

    void recordPlaylistItems(int userId, int playlistId, int... tracksId) throws DaoException;

    FoundTrackForMusicSearchPageDTO getAudioTrackDataById(int trackId) throws DaoException;

    List<FoundTrackForMusicSearchPageDTO> getAudioTracksData(int[] tracksIds) throws DaoException;
}
