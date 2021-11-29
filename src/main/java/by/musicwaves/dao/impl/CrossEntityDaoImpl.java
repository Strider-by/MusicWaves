package by.musicwaves.dao.impl;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dto.FoundArtistForMusicSearchPageDTO;
import by.musicwaves.dto.MusicSearchPageResultsQuantityContainer;
import by.musicwaves.dao.requesthandler.SQLRequestHandler;
import by.musicwaves.dao.util.CrossEntityDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrossEntityDaoImpl implements CrossEntityDao {

    private static final CrossEntityDao instance = new CrossEntityDaoImpl();
    private final SQLRequestHandler requestHandler = SQLRequestHandler.getInstance();
    private final static Logger LOGGER = LogManager.getLogger(CrossEntityDaoImpl.class);
    private final static String EXCEPTION_MSG_QUERY_RESULT_DOES_NOT_MEET_EXPECTATIONS = "Query result does not meet expectations";

    private CrossEntityDaoImpl() {

    }

    public static CrossEntityDao getInstance() {
        return instance;
    }

    private static final class SQL {

        public final static String GET_SEARCH_RESULTS_QUANTITY_FOR_MUSIC_SEARCH_PAGE
                = "SET @search_string := ?;\n"
                + "SELECT \n"
                + "(SELECT COUNT(artists.id) FROM artists \n"
                + "WHERE artists.name LIKE @search_string AND artists.visible = TRUE) AS artists_count, \n"
                + "(SELECT COUNT(albums.id) FROM albums LEFT JOIN artists ON albums.artist = artists.id \n"
                + "WHERE albums.name LIKE @search_string AND artists.visible = TRUE AND albums.visible = TRUE) AS albums_count, \n"
                + "(SELECT COUNT(tracks.id) FROM tracks LEFT JOIN albums ON tracks.album_id = albums.id LEFT JOIN artists ON albums.artist = artists.id \n"
                + "WHERE tracks.name LIKE @search_string AND artists.visible = TRUE AND albums.visible = TRUE AND tracks.visible = TRUE) AS tracks_count; \n";

        public final static String FIND_ARTISTS_FOR_MUSIC_SEARCH_PAGE
                = "SET @user_id := ?; \n"
                + "SELECT artists.id AS artist_id, artists.name AS artist_name, artists.image AS artist_image, \n"
                + "(SELECT COUNT(id) FROM albums WHERE albums.artist = artist_id AND visible = TRUE) AS albums_count_artist_has, \n"
                + "((SELECT COUNT(id) FROM favourite_artists WHERE artists.id = favourite_artists.artist_id AND user_id = @user_id) > 0) AS favourite\n"
                + " FROM artists WHERE artists.name LIKE @search_string AND visible = TRUE ORDER BY artist_name LIMIT ? OFFSET ?;";
    }


    @Override
    public MusicSearchPageResultsQuantityContainer<List<FoundArtistForMusicSearchPageDTO>> findArtistsForMusicSearchPage(
            String searchString, int userId, int limit, int offset) throws DaoException {

        List<List<Map<String, String>>> results = requestHandler.processCustomSelectRequest(
                SQL.GET_SEARCH_RESULTS_QUANTITY_FOR_MUSIC_SEARCH_PAGE + SQL.FIND_ARTISTS_FOR_MUSIC_SEARCH_PAGE,
                (statement) -> {
                    statement.setNextString("%" + searchString + "%");
                    statement.setNextInt(userId);
                    statement.setNextInt(limit);
                    statement.setNextInt(offset);
                });

        if (results.size() != 2 || results.get(0).size() != 1) {
            throw new DaoException(EXCEPTION_MSG_QUERY_RESULT_DOES_NOT_MEET_EXPECTATIONS);
        }

        // preparing DTO
        MusicSearchPageResultsQuantityContainer<List<FoundArtistForMusicSearchPageDTO>> response
                = new MusicSearchPageResultsQuantityContainer<>();

        // getting search results quantity
        Map<String, String> foundArtistsAlbumsAndAudioTracksDataRow = results.get(0).get(0);
        try {
            response.setArtistsFound(Integer.parseInt(foundArtistsAlbumsAndAudioTracksDataRow.get("artists_count")));
            response.setAlbumsFound(Integer.parseInt(foundArtistsAlbumsAndAudioTracksDataRow.get("albums_count")));
            response.setAudioTracksFound(Integer.parseInt(foundArtistsAlbumsAndAudioTracksDataRow.get("tracks_count")));
        } catch (NullPointerException | NumberFormatException ex) {
            throw new DaoException(EXCEPTION_MSG_QUERY_RESULT_DOES_NOT_MEET_EXPECTATIONS, ex);
        }

        // getting data for found artists
        List<Map<String, String>> artistsDataRows = results.get(1);
        List<FoundArtistForMusicSearchPageDTO> artistsDtoList = new ArrayList<>();
        for (Map<String, String> dataRow : artistsDataRows) {
            FoundArtistForMusicSearchPageDTO dto = new FoundArtistForMusicSearchPageDTO();
            try {
                int artistId = Integer.parseInt(dataRow.get("artist_id"));
                dto.setArtistId(artistId);
                String artistName = dataRow.get("artist_name");
                dto.setArtistName(artistName);
                String artistImageName = dataRow.get("artist_image");
                dto.setArtistImageName(artistImageName);
                boolean artistIsFavourite = "1".equals(dataRow.get("favourite"));
                dto.setFavourite(artistIsFavourite);
                int albumsCountArtistHas = Integer.parseInt(dataRow.get("albums_count_artist_has"));
                dto.setAlbumsCountArtistHas(albumsCountArtistHas);
                artistsDtoList.add(dto);
            } catch (NullPointerException | NumberFormatException ex) {
                throw new DaoException(EXCEPTION_MSG_QUERY_RESULT_DOES_NOT_MEET_EXPECTATIONS, ex);
            }
        }

        response.setStoredValue(artistsDtoList);
        return response;
    }
}
