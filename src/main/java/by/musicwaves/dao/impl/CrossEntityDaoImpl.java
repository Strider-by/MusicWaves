package by.musicwaves.dao.impl;

import by.musicwaves.dao.exception.DaoException;
import by.musicwaves.dto.*;
import by.musicwaves.dao.requesthandler.SQLRequestHandler;
import by.musicwaves.dao.CrossEntityDao;
import by.musicwaves.entity.Album;
import by.musicwaves.entity.Artist;
import by.musicwaves.util.Pair;
import by.musicwaves.util.Triplet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
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

        public final static String FIND_ALBUMS_FOR_MUSIC_SEARCH_PAGE
                = "SET @user_id := ?; \n"
                + "SELECT artists.id AS artist_id, artists.name AS artist_name, artists.image AS artist_image, \n"
                + "albums.id AS album_id_t, albums.name AS album_name, albums.image AS album_image, albums.year AS album_year, \n"
                + "(SELECT COUNT(id) FROM tracks WHERE tracks.album_id = album_id_t AND tracks.visible = TRUE) AS tracks_count_album_has, \n"
                + "((SELECT COUNT(id) FROM favourite_albums WHERE albums.id = favourite_albums.album_id AND user_id = @user_id) > 0) AS favourite \n"
                + "FROM albums LEFT JOIN artists ON albums.artist = artists.id "
                + "WHERE albums.name LIKE @search_string AND artists.visible = TRUE AND albums.visible = TRUE \n"
                + "ORDER BY album_name, artist_name, album_year LIMIT ? OFFSET ?;";

        public final static String FIND_AUDIO_TRACKS_FOR_MUSIC_SEARCH_PAGE
                = "SET @user_id := ?; \n"
                + "SELECT artists.id AS artist_id, artists.name AS artist_name, artists.image AS artist_image, \n"
                + "albums.id AS album_id, albums.name AS album_name, albums.image AS album_image, albums.year AS album_year, \n"
                + "tracks.id AS track_id, tracks.name AS track_name, tracks.file_name AS track_file, tracks.number AS track_number, \n"
                + "((SELECT COUNT(id) FROM favourite_tracks WHERE tracks.id = favourite_tracks.track_id AND user_id = @user_id) > 0) AS favourite \n"
                + "FROM tracks LEFT JOIN albums ON tracks.album_id = albums.id LEFT JOIN artists ON albums.artist = artists.id  \n"
                + "WHERE tracks.name LIKE @search_string AND tracks.visible = TRUE AND artists.visible = TRUE AND albums.visible = TRUE \n"
                + "ORDER BY track_name, album_name, artist_name, album_year LIMIT ? OFFSET ?;";

        public final static String GET_CHOSEN_ARTIST_DATA_FOR_MUSIC_SEARCH_PAGE
                = "SET @user_id := ?; SET @artist_id := ?; \n"
                + "SELECT artists.name AS artist_name, artists.image AS artist_image FROM artists WHERE artists.id = @artist_id; \n"
                + "SELECT albums.id AS album_id_t, albums.name AS album_name, albums.image AS album_image, albums.year AS album_year, \n"
                + "(SELECT COUNT(id) FROM tracks WHERE tracks.album_id = album_id_t AND tracks.visible = TRUE) AS tracks_count_album_has, \n"
                + "((SELECT COUNT(id) FROM favourite_albums WHERE albums.id = favourite_albums.album_id AND user_id = @user_id) > 0) AS favourite \n"
                + "FROM albums LEFT JOIN artists ON albums.artist = artists.id WHERE artists.id = @artist_id AND artists.visible = TRUE AND albums.visible = TRUE \n"
                + "ORDER BY album_year, album_name LIMIT ? OFFSET ?;";

        public static final String GET_CHOSEN_ALBUM_DATA_FOR_MUSIC_SEARCH_PAGE
                = "SET @user_id := ?; SET @album_id := ?; \n"
                + "SELECT artists.name AS artist_name, artists.image AS artist_image, albums.name AS album_name, albums.image AS album_image, albums.year AS album_year \n"
                + "FROM artists LEFT JOIN albums ON albums.artist = artists.id WHERE albums.id = @album_id; \n"
                + "SELECT tracks.id AS track_id, tracks.name AS track_name, tracks.file_name AS track_file, \n"
                + "((SELECT COUNT(id) FROM favourite_tracks WHERE tracks.id = favourite_tracks.track_id AND user_id = @user_id) > 0) AS favourite \n"
                + "FROM tracks LEFT JOIN albums ON tracks.album_id = albums.id LEFT JOIN artists ON albums.artist = artists.id \n"
                + "WHERE albums.id = @album_id AND tracks.visible = TRUE AND albums.visible = TRUE AND artists.visible = TRUE \n"
                + "ORDER BY tracks.number LIMIT ? OFFSET ?;";

        public final static String GET_CHOSEN_AUDIO_TRACK_DATA
                = "SELECT artists.id AS artist_id, artists.name AS artist_name, artists.image AS artist_image, \n"
                + "albums.id AS album_id, albums.name AS album_name, albums.image AS album_image, albums.year AS album_year, \n"
                + "tracks.id AS track_id, tracks.name AS track_name, tracks.file_name AS track_file \n"
                + "FROM artists LEFT JOIN albums ON albums.artist = artists.id LEFT JOIN tracks ON tracks.album_id = albums.id \n"
                + "WHERE tracks.id = ? AND tracks.visible = TRUE AND albums.visible = TRUE AND artists.visible = TRUE;" ;

        public final static String SET_ARTIST_AS_FAVOURITE
                = "INSERT IGNORE INTO favourite_artists (user_id, artist_id) VALUES (?, ?);";

        public final static String UNSET_ARTIST_AS_FAVOURITE
                = "DELETE FROM favourite_artists WHERE user_id = ? AND artist_id = ?;";

        public final static String SET_ALBUM_AS_FAVOURITE
                = "INSERT IGNORE INTO favourite_albums (user_id, album_id) VALUES (?, ?);";

        public final static String UNSET_ALBUM_AS_FAVOURITE
                = "DELETE FROM favourite_albums WHERE user_id = ? AND album_id = ?;";

        public final static String SET_TRACK_AS_FAVOURITE
                = "INSERT IGNORE INTO favourite_tracks (user_id, track_id) VALUES (?, ?);";

        public final static String UNSET_TRACK_AS_FAVOURITE
                = "DELETE FROM favourite_tracks WHERE user_id = ? AND track_id = ?;";

        public final static String GET_PLAYLIST_TRACKS
                = "SELECT playlist_elements.id AS item_id, playlist_elements.track_id AS track_id, tracks.name AS track_name, \n"
                + "(SELECT tracks.visible = TRUE AND albums.visible = TRUE AND artists.visible = TRUE) AS is_active_item \n"
                + "FROM playlist_elements \n"
                + "LEFT JOIN playlists ON playlist_elements.playlist_id = playlists.id \n"
                + "LEFT JOIN tracks ON playlist_elements.track_id = tracks.id \n"
                + "LEFT JOIN albums ON tracks.album_id = albums.id \n"
                + "LEFT JOIN artists ON albums.artist = artists.id \n"
                + "WHERE playlists.user_id = ? AND playlists.id = ?";

        public final static String RECORD_PLAYLIST_PT_1
                = "SET @user_id = ?; \n"
                + "SET @playlist_id = ?; \n"
                + "SET @access_granted = (SELECT COUNT(id) FROM playlists WHERE playlists.user_id = @user_id AND playlists.id = @playlist_id) > 0; \n"
                + "SELECT @access_granted AS access_granted; \n"
                + "DELETE FROM playlist_elements WHERE playlist_id = @playlist_id AND @access_granted; \n";

        public final static String RECORD_PLAYLIST_PT_2
                = "INSERT INTO playlist_elements (playlist_id, track_id) SELECT @playlist_id, ? WHERE @access_granted; \n";

        public final static String GET_TRACKS_DATA_PT_1
                = "SELECT artists.id AS artist_id, artists.name AS artist_name, artists.image AS artist_image, \n"
                + "albums.id AS album_id, albums.name AS album_name, albums.image AS album_image, albums.year AS album_year, \n"
                + "tracks.id AS track_id, tracks.name AS track_name, tracks.file_name AS track_file \n"
                + "FROM artists LEFT JOIN albums ON albums.artist = artists.id LEFT JOIN tracks ON tracks.album_id = albums.id \n"
                + "WHERE albums.visible = TRUE AND artists.visible = TRUE AND tracks.visible = TRUE AND tracks.id IN (";

        public final static String GET_TRACKS_DATA_PT_2
                = "); \n";
    }


    @Override
    public MusicSearchResultsContainer<List<ArtistDto>> findArtistsForMusicSearchPage(
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
        MusicSearchResultsContainer<List<ArtistDto>> response
                = new MusicSearchResultsContainer<>();

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
        List<ArtistDto> artistsDtoList = new ArrayList<>();
        for (Map<String, String> dataRow : artistsDataRows) {
            ArtistDto dto = new ArtistDto();
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

    @Override
    public MusicSearchResultsContainer<?> getSearchResultsCountForMusicSearchPage(String searchString) throws DaoException {
        List<List<Map<String, String>>> results = requestHandler.processCustomSelectRequest(
                SQL.GET_SEARCH_RESULTS_QUANTITY_FOR_MUSIC_SEARCH_PAGE,
                (statement) -> statement.setNextString("%" + searchString + "%"));

        if (results.size() != 1 || results.get(0).size() != 1) {
            throw new DaoException(EXCEPTION_MSG_QUERY_RESULT_DOES_NOT_MEET_EXPECTATIONS);
        }

        // preparing DTO
        MusicSearchResultsContainer<?> response = new MusicSearchResultsContainer<>();

        // getting search results quantity
        Map<String, String> foundArtistsAlbumsAndAudioTracksDataRow = results.get(0).get(0);
        try {
            response.setArtistsFound(Integer.parseInt(foundArtistsAlbumsAndAudioTracksDataRow.get("artists_count")));
            response.setAlbumsFound(Integer.parseInt(foundArtistsAlbumsAndAudioTracksDataRow.get("albums_count")));
            response.setAudioTracksFound(Integer.parseInt(foundArtistsAlbumsAndAudioTracksDataRow.get("tracks_count")));
        } catch (NullPointerException | NumberFormatException ex) {
            throw new DaoException(EXCEPTION_MSG_QUERY_RESULT_DOES_NOT_MEET_EXPECTATIONS, ex);
        }

        return response;
    }

    @Override
    public MusicSearchResultsContainer<List<AlbumDto>> findAlbumsForMusicSearchPage(
            String searchString, int userId, int limit, int offset) throws DaoException {

        List<List<Map<String, String>>> results = requestHandler.processCustomSelectRequest(
                SQL.GET_SEARCH_RESULTS_QUANTITY_FOR_MUSIC_SEARCH_PAGE + SQL.FIND_ALBUMS_FOR_MUSIC_SEARCH_PAGE,
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
        MusicSearchResultsContainer<List<AlbumDto>> response
                = new MusicSearchResultsContainer<>();

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
        List<AlbumDto> albumsDtoList = new ArrayList<>();
        for (Map<String, String> dataRow : artistsDataRows) {
            AlbumDto dto = new AlbumDto();
            try {
                int artistId = Integer.parseInt(dataRow.get("artist_id"));
                dto.setArtistId(artistId);
                String artistName = dataRow.get("artist_name");
                dto.setArtistName(artistName);
                String artistImageName = dataRow.get("artist_image");
                dto.setArtistImageName(artistImageName);
                int albumId = Integer.parseInt(dataRow.get("album_id_t"));
                dto.setAlbumId(albumId);
                String albumName = dataRow.get("album_name");
                dto.setAlbumName(albumName);
                String albumImageName = dataRow.get("album_image");
                dto.setAlbumImageName(albumImageName);
                int albumYear = Integer.parseInt(dataRow.get("album_year"));
                dto.setAlbumYear(albumYear);
                int albumTracksCount = Integer.parseInt(dataRow.get("tracks_count_album_has"));
                dto.setTracksCountAlbumHas(albumTracksCount);
                boolean albumIsFavourite = "1".equals(dataRow.get("favourite"));
                dto.setFavourite(albumIsFavourite);
                albumsDtoList.add(dto);
            } catch (NullPointerException | NumberFormatException ex) {
                throw new DaoException(EXCEPTION_MSG_QUERY_RESULT_DOES_NOT_MEET_EXPECTATIONS, ex);
            }
        }

        response.setStoredValue(albumsDtoList);
        return response;
    }

    @Override
    public MusicSearchResultsContainer<List<AudioTrackDto>> findTracksForMusicSearchPage(
            String searchString, int userId, int limit, int offset) throws DaoException {

        List<List<Map<String, String>>> results = requestHandler.processCustomSelectRequest(
                SQL.GET_SEARCH_RESULTS_QUANTITY_FOR_MUSIC_SEARCH_PAGE + SQL.FIND_AUDIO_TRACKS_FOR_MUSIC_SEARCH_PAGE,
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
        MusicSearchResultsContainer<List<AudioTrackDto>> response
                = new MusicSearchResultsContainer<>();

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
        List<Map<String, String>> tracksDataRows = results.get(1);
        List<AudioTrackDto> tracksDtoList = new ArrayList<>();
        for (Map<String, String> dataRow : tracksDataRows) {
            AudioTrackDto dto = new AudioTrackDto();
            try {
                int artistId = Integer.parseInt(dataRow.get("artist_id"));
                dto.setArtistId(artistId);
                String artistName = dataRow.get("artist_name");
                dto.setArtistName(artistName);
                String artistImageName = dataRow.get("artist_image");
                dto.setArtistImageName(artistImageName);
                int albumId = Integer.parseInt(dataRow.get("album_id"));
                dto.setAlbumId(albumId);
                String albumName = dataRow.get("album_name");
                dto.setAlbumName(albumName);
                String albumImageName = dataRow.get("album_image");
                dto.setAlbumImageName(albumImageName);
                int albumYear = Integer.parseInt(dataRow.get("album_year"));
                dto.setAlbumYear(albumYear);
                int trackId = Integer.parseInt(dataRow.get("track_id"));
                dto.setTrackId(trackId);
                String trackName = dataRow.get("track_name");
                dto.setTrackName(trackName);
                String trackFileName = dataRow.get("track_file");
                dto.setTrackFileName(trackFileName);
                int trackNumber = Integer.parseInt(dataRow.get("track_number"));
                dto.setTrackNumber(trackNumber);
                boolean albumIsFavourite = "1".equals(dataRow.get("favourite"));
                dto.setFavourite(albumIsFavourite);
                tracksDtoList.add(dto);
            } catch (NullPointerException | NumberFormatException ex) {
                throw new DaoException(EXCEPTION_MSG_QUERY_RESULT_DOES_NOT_MEET_EXPECTATIONS, ex);
            }
        }

        response.setStoredValue(tracksDtoList);
        return response;
    }

    @Override
    public Pair<Artist, List<AlbumDto>> findChosenArtistDataForMusicSearchPage(
            int userId, int artistId, int limit, int offset) throws DaoException {

        List<List<Map<String, String>>> requestResult = requestHandler.processCustomSelectRequest(
                SQL.GET_CHOSEN_ARTIST_DATA_FOR_MUSIC_SEARCH_PAGE,
                statement -> {
                    statement.setNextInt(userId);
                    statement.setNextInt(artistId);
                    statement.setNextInt(limit);
                    statement.setNextInt(offset);
                }
        );

        if (requestResult.size() != 2 || requestResult.get(0).size() != 1) {
            throw new DaoException(EXCEPTION_MSG_QUERY_RESULT_DOES_NOT_MEET_EXPECTATIONS);
        }

        // getting artist data
        Map<String, String> artistDataRow = requestResult.get(0).get(0);
        Artist artist = new Artist();
        try {
            artist.setName(artistDataRow.get("artist_name"));
            artist.setImageName(artistDataRow.get("artist_image"));
        } catch (NullPointerException | NumberFormatException ex) {
            throw new DaoException(EXCEPTION_MSG_QUERY_RESULT_DOES_NOT_MEET_EXPECTATIONS, ex);
        }

        // getting albums data
        List<Map<String, String>> albumsDataRows = requestResult.get(1);
        List<AlbumDto> albums = new ArrayList<>();

        for (Map<String, String> row : albumsDataRows) {
            AlbumDto dto = new AlbumDto();
            try {
                int albumId = Integer.parseInt(row.get("album_id_t"));
                dto.setAlbumId(albumId);
                String albumName = row.get("album_name");
                dto.setAlbumName(albumName);
                String albumImageName = row.get("album_image");
                dto.setArtistImageName(albumImageName);
                int albumYear = Integer.parseInt(row.get("album_year"));
                dto.setAlbumYear(albumYear);
                int albumTracksCount = Integer.parseInt(row.get("tracks_count_album_has"));
                dto.setTracksCountAlbumHas(albumTracksCount);
                boolean favourite = "1".equals(row.get("favourite"));
                dto.setFavourite(favourite);

                albums.add(dto);

            } catch (NullPointerException | NumberFormatException ex) {
                throw new DaoException(EXCEPTION_MSG_QUERY_RESULT_DOES_NOT_MEET_EXPECTATIONS, ex);
            }
        }

        return new Pair<>(artist, albums);
    }

    @Override
    public Triplet<Artist, Album, List<AudioTrackDto>> findChosenAlbumDataForMusicSearchPage(
            int userId, int albumId, int limit, int offset) throws DaoException {

        List<List<Map<String, String>>> requestResult = requestHandler.processCustomSelectRequest(
                SQL.GET_CHOSEN_ALBUM_DATA_FOR_MUSIC_SEARCH_PAGE,
                statement -> {
                    statement.setNextInt(userId);
                    statement.setNextInt(albumId);
                    statement.setNextInt(limit);
                    statement.setNextInt(offset);
                }
        );

        if (requestResult.size() != 2 || requestResult.get(0).size() != 1) {
            throw new DaoException(EXCEPTION_MSG_QUERY_RESULT_DOES_NOT_MEET_EXPECTATIONS);
        }

        // getting artist and album data
        Map<String, String> artistAndAlbumDataRow = requestResult.get(0).get(0);
        Artist artist = new Artist();
        Album album = new Album();
        try {
            artist.setName(artistAndAlbumDataRow.get("artist_name"));
            artist.setImageName(artistAndAlbumDataRow.get("artist_image"));
            album.setName(artistAndAlbumDataRow.get("album_name"));
            album.setImageName(artistAndAlbumDataRow.get("album_image"));
            album.setYear(Integer.parseInt(artistAndAlbumDataRow.get("album_year")));
        } catch (NullPointerException | NumberFormatException ex) {
            throw new DaoException(EXCEPTION_MSG_QUERY_RESULT_DOES_NOT_MEET_EXPECTATIONS, ex);
        }

        // getting tracks data
        List<AudioTrackDto> tracks = new ArrayList<>();
        List<Map<String, String>> tracksDataRows = requestResult.get(1);
        for (Map<String, String> row : tracksDataRows) {
            AudioTrackDto dto = new AudioTrackDto();
            try {
                int trackId = Integer.parseInt(row.get("track_id"));
                dto.setTrackId(trackId);
                String trackName = row.get("track_name");
                dto.setTrackName(trackName);
                String fileName = row.get("track_file");
                dto.setTrackFileName(fileName);
                boolean favourite = "1".equals(row.get("favourite"));
                dto.setFavourite(favourite);
                tracks.add(dto);
            } catch (NullPointerException | NumberFormatException ex) {
                throw new DaoException(EXCEPTION_MSG_QUERY_RESULT_DOES_NOT_MEET_EXPECTATIONS, ex);
            }
        }

        return new Triplet<>(artist, album, tracks);
    }

    @Override
    public void setArtistAsFavourite(int userId, int artistId) throws DaoException {
        requestHandler.processUpdateRequest(
                SQL.SET_ARTIST_AS_FAVOURITE,
                statement -> {
                    statement.setNextInt(userId);
                    statement.setNextInt(artistId);
                });
    }

    @Override
    public void unsetArtistAsFavourite(int userId, int artistId) throws DaoException {
        requestHandler.processUpdateRequest(
                SQL.UNSET_ARTIST_AS_FAVOURITE,
                statement -> {
                    statement.setNextInt(userId);
                    statement.setNextInt(artistId);
                });
    }

    @Override
    public void setAlbumAsFavourite(int userId, int albumId) throws DaoException {
        requestHandler.processUpdateRequest(
                SQL.SET_ALBUM_AS_FAVOURITE,
                statement -> {
                    statement.setNextInt(userId);
                    statement.setNextInt(albumId);
                });
    }

    @Override
    public void unsetAlbumAsFavourite(int userId, int albumId) throws DaoException {
        requestHandler.processUpdateRequest(
                SQL.UNSET_ALBUM_AS_FAVOURITE,
                statement -> {
                    statement.setNextInt(userId);
                    statement.setNextInt(albumId);
                });
    }

    @Override
    public void setAudioTrackAsFavourite(int userId, int trackId) throws DaoException {
        requestHandler.processUpdateRequest(
                SQL.SET_TRACK_AS_FAVOURITE,
                statement -> {
                    statement.setNextInt(userId);
                    statement.setNextInt(trackId);
                });
    }

    @Override
    public void unsetAudioTrackAsFavourite(int userId, int trackId) throws DaoException {
        requestHandler.processUpdateRequest(
                SQL.UNSET_TRACK_AS_FAVOURITE,
                statement -> {
                    statement.setNextInt(userId);
                    statement.setNextInt(trackId);
                });
    }

    @Override
    public  List<PlaylistItemDto> getPlaylistTracks(int userId, int playlistId) throws DaoException {
        List<List<Map<String, String>>> requestResult = requestHandler.processCustomSelectRequest(
                SQL.GET_PLAYLIST_TRACKS,
                statement ->
                {
                    statement.setNextInt(userId);
                    statement.setNextInt(playlistId);
                });

        if (requestResult.size() != 1) {
            throw new DaoException(EXCEPTION_MSG_QUERY_RESULT_DOES_NOT_MEET_EXPECTATIONS);
        }

        List<Map<String, String>> playlistItemsDataRows = requestResult.get(0);
        List<PlaylistItemDto> playlistItemDtos = new ArrayList<>();
        try {
            for (Map<String, String> row : playlistItemsDataRows) {
                PlaylistItemDto dto = new PlaylistItemDto();
                int itemId = Integer.parseInt(row.get("item_id"));
                dto.setId(itemId);
                int trackId = Integer.parseInt(row.get("track_id"));
                dto.setAudioTrackId(trackId);
                String trackName = row.get("track_name");
                dto.setTrackName(trackName);
                boolean active = "1".equals(row.get("is_active_item"));
                dto.setActive(active);
                playlistItemDtos.add(dto);
            }
        } catch (NullPointerException | NumberFormatException ex) {
            throw new DaoException(EXCEPTION_MSG_QUERY_RESULT_DOES_NOT_MEET_EXPECTATIONS);
        }

        return playlistItemDtos;
    }

    @Override
    public void recordPlaylistItems(int userId, int playlistId, int... tracksId) throws DaoException {
        int elementsToAdd = tracksId.length;
        String sql = buildRecordPlaylistSQL(elementsToAdd);
        requestHandler.processUpdateRequest(
                sql,
                statement -> {
                    statement.setNextInt(userId);
                    statement.setNextInt(playlistId);
                    for(int trackId : tracksId) {
                        statement.setNextInt(trackId);
                    }
                });
    }

    @Override
    public AudioTrackDto getAudioTrackDataById(int trackId) throws DaoException {
        List<AudioTrackDto> audioTrackDtos = requestHandler.processMultipleResultsSelectRequest(
                SQL.GET_CHOSEN_AUDIO_TRACK_DATA,
                statement -> statement.setNextInt(trackId),
                AudioTrackDto::new,
                this::initAudioTrackDto);

        return audioTrackDtos.isEmpty() ? null : audioTrackDtos.get(0);
    }

    @Override
    public List<AudioTrackDto> getAudioTracksData(int[] tracksIds) throws DaoException {
        StringBuilder sql = new StringBuilder();
        sql.append(SQL.GET_TRACKS_DATA_PT_1)
                .append(String.join(", ", "?"))
                .append(SQL.GET_TRACKS_DATA_PT_2);

        return requestHandler.processMultipleResultsSelectRequest(
                sql.toString(),
                statement -> {
                    for (int trackId : tracksIds) {
                        statement.setNextInt(trackId);
                    }
                },
                AudioTrackDto::new,
                this::initAudioTrackDto);
    }

    /*public List<List<Map<String, String>>> getActiveAudioTrackData(int trackId) throws DaoException
    {
        return requestHandler.processCustomSelectRequest(
                SQL_GET_ACTIVE_TRACK_DATA,
                (statement) ->
                {
                    statement.setNextInt(trackId);
                });
    }

    public List<List<Map<String, String>>> getFavouriteAlbumsData(int userId, String searchPattern, int limit, int offset) throws DaoException
    {
        return requestHandler.processCustomSelectRequest(
                SQL_GET_FAVOURITE_ALBUMS_DATA,
                (statement) ->
                {
                    statement.setNextInt(userId);
                    statement.setNextString(searchPattern);
                    statement.setNextInt(limit);
                    statement.setNextInt(offset);
                });
    }

    public List<List<Map<String, String>>> getFavouriteTracksData(int userId, String searchPattern, int limit, int offset) throws DaoException
    {
        return requestHandler.processCustomSelectRequest(
                SQL_GET_FAVOURITE_TRACKS_DATA,
                (statement) ->
                {
                    statement.setNextInt(userId);
                    statement.setNextString(searchPattern);
                    statement.setNextInt(limit);
                    statement.setNextInt(offset);
                });
    }*/

    private String buildRecordPlaylistSQL(int itemsToAdd) {
        StringBuilder sb = new StringBuilder();
        sb.append(SQL.RECORD_PLAYLIST_PT_1);

        for (int i = 0; i < itemsToAdd; i++) {
            sb.append(SQL.RECORD_PLAYLIST_PT_2);
        }

        return sb.toString();
    }

    private void initAudioTrackDto(AudioTrackDto dto, ResultSet resultSet) throws SQLException {

        int artistId = resultSet.getInt("artist_id");
        dto.setArtistId(artistId);
        String artistName = resultSet.getString("artist_name");
        dto.setArtistName(artistName);
        String artistImageName = resultSet.getString("artist_image");
        dto.setArtistImageName(artistImageName);
        int albumId = resultSet.getInt("album_id");
        dto.setAlbumId(albumId);
        String albumName = resultSet.getString("album_name");
        dto.setAlbumName(albumName);
        String albumImageName = resultSet.getString("album_image");
        dto.setAlbumImageName(albumImageName);
        int albumYear = resultSet.getInt("album_year");
        dto.setAlbumYear(albumYear);
        int trackId = resultSet.getInt("track_id");
        dto.setTrackId(trackId);
        String trackName = resultSet.getString("track_name");
        dto.setTrackName(trackName);
        String trackFileName = resultSet.getString("track_file");
        dto.setTrackFileName(trackFileName);
    }

}
