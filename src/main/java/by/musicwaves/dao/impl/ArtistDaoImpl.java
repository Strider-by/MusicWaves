package by.musicwaves.dao.impl;

import by.musicwaves.dao.exception.DaoException;
import by.musicwaves.dao.requesthandler.SQLRequestHandler;
import by.musicwaves.dao.ArtistDao;
import by.musicwaves.dao.util.PreparedStatementContainer;
import by.musicwaves.dao.util.SQLTypesWorker;
import by.musicwaves.dao.util.SimilarityType;
import by.musicwaves.entity.Artist;
import by.musicwaves.util.Pair;
import by.musicwaves.util.Triplet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ArtistDaoImpl implements ArtistDao {

    private static final ArtistDao instance = new ArtistDaoImpl();
    private final SQLRequestHandler requestHandler = SQLRequestHandler.getInstance();
    private final static Logger LOGGER = LogManager.getLogger(ArtistDaoImpl.class);

    private static final class SQL {
        public final static String SELECT_ALL = "SELECT * FROM artists ";
        public final static String COUNT_ALL = "SELECT COUNT(*) AS quantity FROM artists ";
        public final static String CREATE_INSTANCE
                = "INSERT INTO artists (name, image, visible) "
                + "VALUES (?, ?, ?);";
        private final static String UPDATE_ARTIST_IMAGE_FILE_NAME
                = "SELECT image FROM artists WHERE id = ?;\n"
                + "UPDATE artists SET image = ? WHERE id = ?";
        private final static String UPDATE_INSTANCE
                = "UPDATE artists SET name = ?, image = ?, visible = ?";
        private final static String UPDATE_NAME_AND_VISIBILITY
                = "UPDATE artists SET name = ?, visible = ?";
        private final static String DELETE_INSTANCE
                = "DELETE FROM artists";
        private static final String DELETE_INSTANCE_AND_GET_RELATED_ARTIST_IMAGE_NAME_AND_ALBUMS_IMAGES_NAMES_AND_AUDIO_TRACKS_FILES_NAMES
                = "SET @artist_id = ?; \n"
                + "SELECT DISTINCT artists.image AS artist_image_name, albums.image AS album_image_name, tracks.file_name AS track_file_name \n"
                + "FROM artists LEFT JOIN albums ON albums.artist = artists.id \n"
                + "LEFT JOIN tracks ON tracks.album_id = albums.id \n"
                + "WHERE artists.id = @artist_id; \n"
                + "DELETE FROM artists WHERE id = @artist_id";


        private static final class SelectBy {
            public final static String ID
                    = " WHERE id = ?";
            public final static String NAME
                    = " WHERE name = ?";
            public final static String VISIBILITY
                    = " WHERE visible = ?";
        }
    }

    private ArtistDaoImpl() {
        // single instance allowed
    }

    public static ArtistDao getInstance() {
        return instance;
    }

    @Override
    public List<Artist> getAll() throws DaoException {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL.SELECT_ALL,
                null,
                Artist::new,
                this::initArtist);
    }

    @Override
    public Artist findById(int id) throws DaoException {
        return requestHandler.processSingleResultSelectRequest(
                SQL.SELECT_ALL + SQL.SelectBy.ID,
                statement -> statement.setNextInt(id),
                Artist::new,
                this::initArtist);
    }

    @Override
    public Integer create(Artist artist) throws DaoException {
        LOGGER.debug("Artist instance: \n" + instance);
        return requestHandler.processCreateRequest(
                SQL.CREATE_INSTANCE,
                artist,
                this::initCreateStatement);
    }

    @Override
    public void update(Artist artist) throws DaoException {
        requestHandler.processUpdateRequest(
                artist,
                SQL.UPDATE_INSTANCE + SQL.SelectBy.ID,
                this::initUpdateStatement,
                statement -> statement.setNextInt(artist.getId()));
    }

    @Override
    public void updateNameAndVisibility(int id, String name, boolean visible) throws DaoException {
        requestHandler.processUpdateRequest(
                SQL.UPDATE_NAME_AND_VISIBILITY + SQL.SelectBy.ID,
                statement -> {
                    statement.setNextString(name);
                    statement.setNextBoolean(visible);
                    statement.setNextInt(id);
                });
    }

    @Override
    public String updateArtistImageFileName(int artistId, String newFileName) throws DaoException {
        String oldFileName = requestHandler.processStringResultRequest(
                SQL.UPDATE_ARTIST_IMAGE_FILE_NAME,
                statement -> {
                    statement.setNextInt(artistId);
                    statement.setNextString(newFileName);
                    statement.setNextInt(artistId);
                });

        return oldFileName;
    }

    @Override
    public Pair<Integer, List<Artist>> findArtists(String name, Boolean visibility, int nameSearchTypeId, int pageNumber, int recordsPerPage) throws DaoException {
        SelectRequestBuilder requestBuilder = new SelectRequestBuilder();
        requestBuilder.setSearchByName(name != null);
        requestBuilder.setSearchByVisibilityState(visibility != null);
        SimilarityType nameSearchSimilarityType = SimilarityType.getById(nameSearchTypeId);
        requestBuilder.setNameSearchType(nameSearchSimilarityType);

        String sql = requestBuilder.build();
        LOGGER.debug("built select SQL is: \n" + sql);

        List<List<Map<String, String>>> result = requestHandler.processCustomSelectRequest(
                sql,
                statement -> {
                    // we do it twice to init both counting adn getting-data parts of our request
                    this.initFindArtistsStatement(statement, name, visibility, nameSearchSimilarityType);
                    this.initFindArtistsStatement(statement, name, visibility, nameSearchSimilarityType);
                    // init limit and offset (required only for getting-data part of our request
                    this.calcAndInitLimitAndOffset(statement, pageNumber, recordsPerPage);
                });


        try {
            // get quantity of users found from the first part og the request
            int quantity = Integer.parseInt(result.get(0).get(0).get("quantity"));

            // get actual users from the second part of the request
            List<Map<String, String>> artistsDataRows = result.get(1);
            List<Artist> artists = new ArrayList<>();
            for(Map<String, String> dataRow : artistsDataRows) {
                artists.add(createArtistFromDataRow(dataRow));
            }

            Pair<Integer, List<Artist>> response = new Pair<>(quantity, artists);
            return response;

        } catch (IndexOutOfBoundsException | IllegalArgumentException | NullPointerException ex) {
            throw new DaoException("Failed to parse response from given SQL request", ex);
        }
    }

    @Override
    public boolean deleteById(int id) throws DaoException {
        return 1 == requestHandler.processDeleteByIdRequest(id, SQL.DELETE_INSTANCE + SQL.SelectBy.ID);
    }

    @Override
    public boolean delete(Artist artist) throws DaoException {
        return 1 == requestHandler.processDeleteRequest(
                instance,
                SQL.DELETE_INSTANCE + SQL.SelectBy.ID,
                (user, statement) -> statement.setNextInt(artist.getId()));
    }

    @Override
    public Triplet<String, List<String>, List<String>> deleteArtistAndGetFilesToDelete(int id) throws DaoException {

        List<List<Map<String, String>>> results = requestHandler.processCustomSelectRequest(
                SQL.DELETE_INSTANCE_AND_GET_RELATED_ARTIST_IMAGE_NAME_AND_ALBUMS_IMAGES_NAMES_AND_AUDIO_TRACKS_FILES_NAMES,
                statement -> statement.setNextInt(id));

        String artistImage = results.get(0).stream()
                .findAny()
                .map(stringStringMap -> stringStringMap.get("artist_image_name"))
                .orElse(null);

        List<String> albumsImages = results.get(0).stream()
                .map(stringStringMap -> stringStringMap.get("album_image_name"))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<String> audioTracksFiles = results.get(0).stream()
                .map(stringStringMap -> stringStringMap.get("track_file_name"))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        return new Triplet<>(artistImage, albumsImages, audioTracksFiles);
    }

    
    
    private void initArtist(Artist artist, ResultSet resultSet) throws SQLException {
        artist.setId(resultSet.getInt("id"));
        artist.setName(resultSet.getString("name"));
        artist.setImageName(resultSet.getString("image"));
        artist.setVisible(resultSet.getBoolean("visible"));
    }

    private Artist createArtistFromDataRow(Map<String, String> dataRow) throws NullPointerException, IllegalArgumentException {
        Artist artist = new Artist();
        artist.setId(Integer.parseInt(dataRow.get("id")));
        artist.setName(dataRow.get("name"));
        artist.setImageName(dataRow.get("image"));
        artist.setVisible(SQLTypesWorker.parseBoolean(dataRow.get("visible")));
        return artist;
    }

    private void initUpdateStatement(Artist artist, PreparedStatementContainer statement) throws SQLException {
        statement.setNextString(artist.getName());
        statement.setNextString(artist.getImageName());
        statement.setNextBoolean(artist.isVisible());
    }

    private void initCreateStatement(Artist artist, PreparedStatementContainer statement) throws SQLException {
        statement.setNextString(artist.getName());
        statement.setNextString(artist.getImageName());
        statement.setNextBoolean(artist.isVisible());
    }

    private void initFindArtistsStatement(
            PreparedStatementContainer statement,
            String name, Boolean active,
            SimilarityType similarityType) throws SQLException {

        if (name != null) {
            // the way we set login parameter depends on haw we are going to search, via '=', or via 'LIKE'
            name = similarityType == SimilarityType.EQUALS ? name : "%" + name + "%";
            statement.setNextString(name);
        }
        if (active != null) statement.setNextBoolean(active);
    }

    private void initLimitAndOffset(PreparedStatementContainer statement, int limit, int offset) throws SQLException {
        statement.setNextInt(limit);
        statement.setNextInt(offset);
    }

    private void calcAndInitLimitAndOffset(PreparedStatementContainer statement, int pageNumber, int recordsPerPage) throws SQLException {
        int limit = recordsPerPage;
        int offset = (pageNumber - 1) * recordsPerPage;
        initLimitAndOffset(statement, limit, offset);
    }

    public class SelectRequestBuilder {

        private static final String AND = " AND ";
        private static final String WHERE = " WHERE ";
        private static final String LIMIT_OFFSET = " LIMIT ? OFFSET ?;";

        StringBuilder dataGettingSql = new StringBuilder();
        StringBuilder resultsCountingSql = new StringBuilder();

        private boolean searchByName;
        private boolean searchByVisibilityState;
        private SimilarityType nameSearchType;

        public String build() {
            dataGettingSql.append(SQL.SELECT_ALL);
            resultsCountingSql.append(SQL.COUNT_ALL);

            boolean whereClauseInvolved = searchByName || searchByVisibilityState;
            if(whereClauseInvolved) {
                boolean andRequired = false;
                dataGettingSql.append(WHERE);
                resultsCountingSql.append(WHERE);

                if (searchByName) {
                    if(andRequired) {
                        dataGettingSql.append(AND);
                        resultsCountingSql.append(AND);
                    }
                    dataGettingSql.append(Field.NAME.getFieldName());
                    dataGettingSql.append(nameSearchType.getSql());
                    resultsCountingSql.append(Field.NAME.getFieldName());
                    resultsCountingSql.append(nameSearchType.getSql());
                    andRequired = true;
                }

                if (searchByVisibilityState) {
                    if(andRequired) {
                        dataGettingSql.append(AND);
                        resultsCountingSql.append(AND);
                    }
                    dataGettingSql.append(Field.VISIBILITY.getFieldName());
                    dataGettingSql.append(nameSearchType.getSql());
                    resultsCountingSql.append(Field.VISIBILITY.getFieldName());
                    resultsCountingSql.append(nameSearchType.getSql());
                }
            }
            // resultsCountingSql building ends here
            resultsCountingSql.append(";\n");
            dataGettingSql.append(LIMIT_OFFSET);

            //LOGGER.debug("Select artists request parts: \n" + resultsCountingSql + "\n" + dataGettingSql);
            return resultsCountingSql.toString() + dataGettingSql.toString();
        }

        public void setSearchByName(boolean searchByName) {
            this.searchByName = searchByName;
        }

        public void setNameSearchType(SimilarityType nameSearchType) {
            this.nameSearchType = nameSearchType;
        }

        public void setSearchByVisibilityState(boolean searchByVisibilityState) {
            this.searchByVisibilityState = searchByVisibilityState;
        }
    }

    public enum Field {

        UNKNOWN_FIELD(-1, null, null),
        ID(1,"id", "id"),
        NAME(2, "name", "name"),
        IMAGE_NAME(3, "image", "image"),
        VISIBILITY(4, "visible", "visible");

        private int id;
        private String fieldName;
        private String propertyKey;

        Field(int id, String fieldName, String propertyKey) {
            this.id = id;
            this.fieldName = fieldName;
            this.propertyKey = propertyKey;
        }

        public String getFieldName() {
            return fieldName;
        }

        public int getId() {
            return id;
        }

        public String getPropertyKey() {
            return propertyKey;
        }

        public static Field getById(int id) {
            return Arrays.stream(values())
                    .filter(field -> field.id == id)
                    .findAny()
                    .orElse(UNKNOWN_FIELD);
        }
    }
    
}