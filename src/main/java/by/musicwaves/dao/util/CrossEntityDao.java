package by.musicwaves.dao.util;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dto.FoundArtistForMusicSearchPageDTO;
import by.musicwaves.dto.MusicSearchPageResultsQuantityContainer;

import java.util.List;

public interface CrossEntityDao {
    MusicSearchPageResultsQuantityContainer<List<FoundArtistForMusicSearchPageDTO>> findArtistsForMusicSearchPage(
            String searchString, int userId, int limit, int offset) throws DaoException;
}
