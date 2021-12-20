package by.musicwaves.dao.impl;

import by.musicwaves.dao.exception.DaoException;
import by.musicwaves.dao.PlaylistDao;
import by.musicwaves.dao.requesthandler.SQLRequestHandler;
import by.musicwaves.dao.util.PreparedStatementContainer;
import by.musicwaves.dao.util.PreparedStatementContainerInitializer;
import by.musicwaves.entity.Playlist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PlaylistDaoImpl implements PlaylistDao {

    private static final PlaylistDaoImpl instance = new PlaylistDaoImpl();
    private final SQLRequestHandler requestHandler = SQLRequestHandler.getInstance();
    private static final Logger LOGGER = LogManager.getLogger(PlaylistDaoImpl.class);

    public static PlaylistDaoImpl getInstance() {
        return instance;
    }

    private static final class SQL {

        public static final String SELECT_ALL
                = "SELECT * FROM playlists";
        public static final String CREATE_INSTANCE
                = "INSERT INTO playlists (name, user_id) VALUES (?, ?)";
        public static final String UPDATE_INSTANCE
                = "UPDATE playlists SET name = ?, user_id = ?";
        public static final String DELETE_INSTANCE
                = "DELETE FROM playlists";
        public static final String DELETE_BY_USER_AND_PLAYLIST_ID
                = "DELETE FROM playlists WHERE user_id = ? AND id = ?";
        public static final String RENAME_PLAYLIST
                = "UPDATE playlists SET name = ? WHERE user_id = ? AND id = ?";

        private static final class SelectBy {
            public static final String ID
                    = " WHERE playlists.id = ?";
            public static final String USER_ID
                    = " WHERE playlists.user_id = ?";
        }

        private static final class OrderBy {
            public static final String PLAYLIST_NAME_ASC
                    = " ORDER BY playlists.name ASC";
        }
    }

    @Override
    public List<Playlist> getAll() throws DaoException {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL.SELECT_ALL,
                null,
                Playlist::new,
                this::initPlaylist);
    }

    @Override
    public Playlist findById(int id) throws DaoException {
        return requestHandler.processSingleResultSelectRequest(
                SQL.SELECT_ALL + SQL.SelectBy.ID,
                (statement) -> statement.setNextInt(id),
                Playlist::new,
                this::initPlaylist);

    }

    @Override
    public Integer create(Playlist instance) throws DaoException {
        return requestHandler.processCreateRequest(
                SQL.CREATE_INSTANCE,
                instance,
                this::initCreationStatement);
    }

    @Override
    public void update(Playlist instance) throws DaoException {
        requestHandler.processUpdateRequest(
                instance,
                SQL.UPDATE_INSTANCE + SQL.SelectBy.ID,
                this::initCreationStatement,
                statement -> statement.setNextInt(instance.getId()));
    }

    @Override
    public boolean rename(int userId, int playlistId, String playlistName) throws DaoException {
        int affectedRows = requestHandler.processUpdateRequest(
                SQL.RENAME_PLAYLIST,
                statement -> {
                    statement.setNextString(playlistName);
                    statement.setNextInt(userId);
                    statement.setNextInt(playlistId);
                });

        return affectedRows == 1;
    }

    @Override
    public boolean deleteById(int id) throws DaoException {
        return 1 == requestHandler.processDeleteByIdRequest(id, SQL.DELETE_INSTANCE + SQL.SelectBy.ID);
    }

    @Override
    public boolean delete(Playlist instance) throws DaoException {
        return deleteById(instance.getId());
    }

    @Override
    public void delete(int userId, int... playlistsId) throws DaoException {
        PreparedStatementContainerInitializer[] initializers = new PreparedStatementContainerInitializer[playlistsId.length];
        for (int i = 0; i < initializers.length; i++) {
            int playlistId = playlistsId[i];
            initializers[i] = statement -> {
                statement.setNextInt(userId);
                statement.setNextInt(playlistId);
            };
        }

        requestHandler.processBatchRequest(SQL.DELETE_BY_USER_AND_PLAYLIST_ID, initializers);
    }

    @Override
    public List<Playlist> getUserPlaylists(int userId) throws DaoException {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL.SELECT_ALL + SQL.SelectBy.USER_ID,
                statement -> statement.setNextInt(userId),
                Playlist::new,
                this::initPlaylist);
    }


    private void initPlaylist(Playlist playlist, ResultSet resultSet) throws SQLException {
        playlist.setId(resultSet.getInt("id"));
        playlist.setName(resultSet.getString("name"));
        playlist.setUserId(resultSet.getInt("user_id"));
    }

    private void initCreationStatement(Playlist playlist, PreparedStatementContainer statement) throws SQLException {
        statement.setNextString(playlist.getName());
        statement.setNextInt(playlist.getUserId());
    }
}
