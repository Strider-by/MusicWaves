package by.musicwaves.dao.impl;

import by.musicwaves.dao.AlbumDao;
import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.SimilarityType;
import by.musicwaves.dao.SortOrder;
import by.musicwaves.dao.requesthandler.SQLRequestHandler;
import by.musicwaves.dao.util.PreparedStatementContainer;
import by.musicwaves.entity.Album;
import by.musicwaves.entity.Artist;
import by.musicwaves.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class AlbumDaoImpl implements AlbumDao {

    private static final AlbumDao instance = new AlbumDaoImpl();
    private final SQLRequestHandler requestHandler = SQLRequestHandler.getInstance();
    private final static Logger LOGGER = LogManager.getLogger(AlbumDaoImpl.class);

    private static final class SQL {
        public final static String SELECT_ALL = "SELECT * FROM albums ";
        public final static String COUNT_ALL = "SELECT COUNT(*) AS quantity FROM albums ";
        public final static String CREATE_INSTANCE
                = "INSERT INTO albums (artist, name, year, image, visible) "
                + "VALUES (?, ?, ?, ?, ?);";
        private final static String UPDATE_ALBUM_IMAGE_FILE_NAME
                = "SELECT image FROM albums WHERE id = ?;\n"
                + "UPDATE albums SET image = ? WHERE id = ?";
        public final static String UPDATE_INSTANCE
                = "UPDATE albums SET artist = ?, name = ?, year = ?, image = ?, visible = ?";
        public final static String UPDATE_NAME_YEAR_AND_VISIBILITY
                = "UPDATE albums SET name = ?, year = ?, visible = ?";
        public final static String DELETE_INSTANCE
                = "DELETE FROM albums";
        public final static String DELETE_INSTANCE_AND_GET_RELATED_ALBUM_IMAGE_NAME_AND_TRACKS_FILES_NAMES
                = "SET @album_id = ?; \n"
                // get album image name
                + "SELECT image FROM albums \n"
                + "WHERE id = @album_id AND image IS NOT NULL; \n"
                // get track files names
                + "SELECT file_name FROM tracks \n"
                + "WHERE album_id = @album_id AND file_name IS NOT NULL; \n"
                // delete album (it should cause cascade deletion of corresponding tracks)
                + "DELETE FROM albums WHERE id = @album_id;";

        private static final class SelectBy {
            public final static String ID
                    = " WHERE id = ?";
            public final static String NAME
                    = " WHERE name = ?";
            public final static String ARTIST
                    = " WHERE artist = ?";
            public final static String YEAR
                    = " WHERE year = ?";
            public final static String VISIBLE
                    = " WHERE visible = ?";
        }
    }

    private AlbumDaoImpl() {
        // single instance allowed
    }

    public static AlbumDao getInstance() {
        return instance;
    }

    @Override
    public List<Album> getAll() throws DaoException {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL.SELECT_ALL,
                null,
                Album::new,
                this::initAlbum);
    }

    @Override
    public Album findById(int id) throws DaoException {
        return requestHandler.processSingleResultSelectRequest(
                SQL.SELECT_ALL + SQL.SelectBy.ID,
                statement -> statement.setNextInt(id),
                Album::new,
                this::initAlbum);
    }

    @Override
    public Integer create(Album album) throws DaoException {
        return requestHandler.processCreateRequest(
                SQL.CREATE_INSTANCE,
                album,
                this::initCreateStatement);
    }

    @Override
    public void update(Album album) throws DaoException {
        requestHandler.processUpdateRequest(
                album,
                SQL.UPDATE_INSTANCE + SQL.SelectBy.ID,
                this::initUpdateStatement,
                statement -> statement.setNextInt(album.getId()));
    }

    @Override
    public void updateNameYearAndVisibility(int id, String name, Integer year, boolean visible) throws DaoException {
        requestHandler.processUpdateRequest(
                SQL.UPDATE_NAME_YEAR_AND_VISIBILITY + SQL.SelectBy.ID,
                statement -> {
                    statement.setNextString(name);
                    statement.setNextInt(year);
                    statement.setNextBoolean(visible);
                    statement.setNextInt(id);
                });
    }

    @Override
    public String updateAlbumImageFileName(int albumId, String newFileName) throws DaoException {
        String oldFileName = requestHandler.processStringResultRequest(
                SQL.UPDATE_ALBUM_IMAGE_FILE_NAME,
                statement -> {
                    statement.setNextInt(albumId);
                    statement.setNextString(newFileName);
                    statement.setNextInt(albumId);
                });

        return oldFileName;
    }

    @Override
    public Pair<Integer, List<Album>> findAlbums(int artistId, String name, Integer year, Boolean visibility, int pageNumber, int recordsPerPage) throws DaoException {
        SelectRequestBuilder requestBuilder = new SelectRequestBuilder();
        requestBuilder.setSearchByName(name != null);
        requestBuilder.setSearchByVisibilityState(visibility != null);
        requestBuilder.setSearchByYear(year != null);

        String sql = requestBuilder.build();
        LOGGER.debug("built select SQL is: \n" + sql);

        List<List<Map<String, String>>> result = requestHandler.processCustomSelectRequest(
                sql,
                statement -> {
                    // we do it twice to init both counting adn getting-data parts of our request
                    this.initFindAlbumsStatement(statement, artistId, name, year, visibility);
                    this.initFindAlbumsStatement(statement, artistId, name, year, visibility);
                    // init limit and offset (required only for getting-data part of our request
                    this.calcAndInitLimitAndOffset(statement, pageNumber, recordsPerPage);
                });


        try {
            // get quantity of users found from the first part og the request
            int quantity = Integer.parseInt(result.get(0).get(0).get("quantity"));

            // get actual users from the second part of the request
            List<Map<String, String>> albumsDataRows = result.get(1);
            List<Album> albums = new ArrayList<>();
            for(Map<String, String> dataRow : albumsDataRows) {
                albums.add(createAlbumFromDataRow(dataRow));
            }

            Pair<Integer, List<Album>> response = new Pair<>(quantity, albums);
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
    public boolean delete(Album album) throws DaoException {
        return 1 == requestHandler.processDeleteRequest(
                instance,
                SQL.DELETE_INSTANCE + SQL.SelectBy.ID,
                (user, statement) -> statement.setNextInt(album.getId()));
    }


    @Override
    public Pair<String, List<String>> deleteAlbumAndGetRelatedAlbumImageNameAndTracksFilesNames(int id) throws DaoException {
        List<List<Map<String, String>>> results = requestHandler.processCustomSelectRequest(
                SQL.DELETE_INSTANCE_AND_GET_RELATED_ALBUM_IMAGE_NAME_AND_TRACKS_FILES_NAMES,
                statement -> statement.setNextInt(id));

        // first set should contain only 1 value - name of the image file, OR there is no value and we get empty result
        String image = results.get(0).stream()
                .map(stringStringMap -> stringStringMap.get("image"))
                .findAny()
                .orElse(null);

        // second set is for tracks files names
        List<String> fileNames = results.get(1).stream()
                .map(stringStringMap -> stringStringMap.get("file_name"))
                .collect(Collectors.toList());

        return new Pair<>(image, fileNames);
    }

    
    
    private void initAlbum(Album album, ResultSet resultSet) throws SQLException {
        album.setId(resultSet.getInt("id"));
        album.setArtistId(resultSet.getInt("artist"));
        album.setName(resultSet.getString("name"));
        // year field can be empty
        String yearString = resultSet.getString("year");
        album.setYear(yearString == null ? null : Integer.parseInt(yearString));
        album.setImageName(resultSet.getString("image"));
        album.setVisible(resultSet.getBoolean("visible"));
    }

    private Album createAlbumFromDataRow(Map<String, String> dataRow) throws NullPointerException, IllegalArgumentException {
        Album album = new Album();
        album.setId(Integer.parseInt(dataRow.get("id")));
        album.setArtistId(Integer.parseInt(dataRow.get("artist")));
        album.setName(dataRow.get("name"));
        String yearString = dataRow.get("year");
        album.setYear(yearString == null ? null : Integer.parseInt(yearString));
        album.setImageName(dataRow.get("image"));
        album.setVisible(SQLTypesWorker.parseBoolean(dataRow.get("visible")));
        return album;
    }

    private void initUpdateStatement(Album album, PreparedStatementContainer statement) throws SQLException {
        statement.setNextInt(album.getArtistId());
        statement.setNextString(album.getName());
        statement.setNextInt(album.getYear());
        statement.setNextString(album.getImageName());
        statement.setNextBoolean(album.isVisible());
    }

    private void initCreateStatement(Album album, PreparedStatementContainer statement) throws SQLException {
        statement.setNextInt(album.getArtistId());
        statement.setNextString(album.getName());
        statement.setNextInt(album.getYear());
        statement.setNextString(album.getImageName());
        statement.setNextBoolean(album.isVisible());
    }

    private void initFindAlbumsStatement(
            PreparedStatementContainer statement,
            int artistId, String name, Integer year, Boolean active) throws SQLException {

        statement.setNextInt(artistId);
        if (name != null) statement.setNextString("%" + name + "%");
        if (year != null) statement.setNextInt(year);
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
        private boolean searchByYear;
        private boolean searchByVisibilityState;

        public String build() {
            dataGettingSql.append(SQL.SELECT_ALL);
            resultsCountingSql.append(SQL.COUNT_ALL);

            dataGettingSql.append(WHERE);
            resultsCountingSql.append(WHERE);

            dataGettingSql.append(Field.ARTIST_ID.getFieldName());
            dataGettingSql.append(SimilarityType.EQUALS.getSql());
            resultsCountingSql.append(Field.ARTIST_ID.getFieldName());
            resultsCountingSql.append(SimilarityType.EQUALS.getSql());

            if (searchByName) {
                dataGettingSql.append(AND);
                resultsCountingSql.append(AND);
                dataGettingSql.append(Field.NAME.getFieldName());
                dataGettingSql.append(SimilarityType.CONTAINS.getSql());
                resultsCountingSql.append(Field.NAME.getFieldName());
                resultsCountingSql.append(SimilarityType.CONTAINS.getSql());
            }

            if (searchByYear) {
                dataGettingSql.append(AND);
                resultsCountingSql.append(AND);
                dataGettingSql.append(Field.YEAR.getFieldName());
                resultsCountingSql.append(Field.YEAR.getFieldName());
                dataGettingSql.append(SimilarityType.EQUALS.getSql());
                resultsCountingSql.append(SimilarityType.EQUALS.getSql());
            }

            if (searchByVisibilityState) {
                dataGettingSql.append(AND);
                resultsCountingSql.append(AND);
                dataGettingSql.append(Field.VISIBILITY.getFieldName());
                dataGettingSql.append(SimilarityType.EQUALS.getSql());
                resultsCountingSql.append(Field.VISIBILITY.getFieldName());
                resultsCountingSql.append(SimilarityType.EQUALS.getSql());
            }

            // resultsCountingSql building ends here
            resultsCountingSql.append(";\n");
            dataGettingSql.append(" ORDER BY ")
                    .append(Field.YEAR.getFieldName())
                    .append(" ");
            dataGettingSql.append(LIMIT_OFFSET);

            //LOGGER.debug("Select artists request parts: \n" + resultsCountingSql + "\n" + dataGettingSql);
            return resultsCountingSql.toString() + dataGettingSql.toString();
        }

        public void setSearchByName(boolean searchByName) {
            this.searchByName = searchByName;
        }

        public void setSearchByVisibilityState(boolean searchByVisibilityState) {
            this.searchByVisibilityState = searchByVisibilityState;
        }

        public void setSearchByYear(boolean searchByYear) {
            this.searchByYear = searchByYear;
        }
    }

    public enum Field {

        UNKNOWN_FIELD(-1, null, null),
        ID(1,"id", "id"),
        ARTIST_ID(2,"artist", "artist"),
        NAME(3, "name", "name"),
        YEAR(4, "year", "year"),
        IMAGE_NAME(5, "image", "image"),
        VISIBILITY(6, "visible", "visible");

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