package by.musicwaves.dao.impl;

import by.musicwaves.dao.AudioTrackDao;
import by.musicwaves.dao.exception.DaoException;
import by.musicwaves.dao.requesthandler.SQLRequestHandler;
import by.musicwaves.dao.util.PreparedStatementContainer;
import by.musicwaves.entity.AudioTrack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AudioTrackDaoImpl implements AudioTrackDao {

    private static final AudioTrackDao instance = new AudioTrackDaoImpl();
    private final SQLRequestHandler requestHandler = SQLRequestHandler.getInstance();
    private final static Logger LOGGER = LogManager.getLogger(AudioTrackDaoImpl.class);

    private static final class SQL {
        public final static String SELECT_ALL = "SELECT * FROM tracks ";
        public final static String CREATE_TRACK_GENERATING_TRACK_NUMBER
                = "INSERT INTO tracks (album_id, name, visible, number) \n"
                + "SELECT ?, ?, ?, COUNT(id) + 1 FROM tracks WHERE album_id = ?";
        private final static String UPDATE_AUDIO_TRACK_FILE_NAME
                = "SELECT file_name FROM tracks WHERE id = ?;\n"
                + "UPDATE tracks SET file_name = ? WHERE id = ?";
        public final static String UPDATE_INSTANCE
                = "UPDATE tracks SET name = ?, file_name = ?, visible = ? number = ?";
        public final static String UPDATE_NAME_AND_VISIBILITY
                = "UPDATE tracks SET name = ?, visible = ?";
        public final static String DELETE_INSTANCE
                = "DELETE FROM tracks";
        public final static String DELETE_TRACK_AND_GET_FILE_BEING_DELETED_NAME_AND_REARRANGE_ALBUM_TRACKS
                = "SET @track_id := ?; "
                + "SET @Album = (SELECT album_id FROM tracks WHERE tracks.id = @track_id); "
                // getting track file
                + "SELECT file_name FROM tracks "
                + "WHERE id = @track_id; "
                // deleting record
                + "DELETE FROM tracks "
                + "WHERE id = @track_id;"
                // rearranging tracks numbers
                + "SET @row_base = 0; "
                + "UPDATE tracks "
                + "SET number = (@row_base := @row_base + 1) "
                + "WHERE album_id = @Album "
                + "ORDER BY number; ";

        public final static String REARRANGE_ALBUM_TRACKS_NUMBERS
                = "SET @row_base = 0; "
                + "UPDATE tracks "
                + "SET number = (@row_base := @row_base + 1) "
                + "WHERE album_id = ? "
                + "ORDER BY number; ";

        public final static String SHIFT_TRACK_NUMBER_UP
                = "SELECT @current_number := number, @current_album := album_id FROM tracks WHERE id = ?; "
                + "UPDATE tracks "
                + "	SET number = "
                + " (CASE "
                + "     WHEN number = @current_number - 1 THEN @current_number "
                + "     ELSE @current_number - 1 "
                + " END) "
                + "WHERE @current_number > 1 AND number IN (@current_number - 1, @current_number) AND album_id = @current_album;";

        public final static String SHIFT_TRACK_NUMBER_DOWN
                = "SELECT @current_number := number, @current_album := album_id "
                + "FROM tracks "
                + "WHERE id = ?; "
                + "SELECT @tracks_in_album := COUNT(id) "
                + "FROM tracks "
                + "WHERE album_id = @current_album; "
                + "UPDATE tracks "
                + "SET number = IF(number = @current_number, @current_number + 1, @current_number) "
                + "WHERE @current_number < @tracks_in_album "
                + "AND number IN (@current_number, @current_number + 1) "
                + "AND album_id = @current_album;";

        private static final class SelectBy {
            public final static String ID
                    = " WHERE id = ?";
            public final static String ALBUM_ID
                    = " WHERE album_id = ?";
            public final static String NAME
                    = " WHERE name = ?";
            public final static String NUMBER
                    = " WHERE number = ?";
            public final static String VISIBLE
                    = " WHERE visible = ?";
            public final static String FILE_NAME
                    = " WHERE file_name = ?";
        }

        private static final class OrderBy {
            public final static String ID
                    = " ORDER BY id ";
            public final static String ALBUM_ID
                    = " ORDER BY album_id ";
            public final static String NAME
                    = " ORDER BY name ";
            public final static String NUMBER
                    = " ORDER BY number ";
        }
    }

    private AudioTrackDaoImpl() {
        // single instance allowed
    }

    public static AudioTrackDao getInstance() {
        return instance;
    }

    @Override
    public List<AudioTrack> getAll() throws DaoException {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL.SELECT_ALL,
                null,
                AudioTrack::new,
                this::initAudioTrack);
    }

    @Override
    public AudioTrack findById(int id) throws DaoException {
        return requestHandler.processSingleResultSelectRequest(
                SQL.SELECT_ALL + SQL.SelectBy.ID,
                statement -> statement.setNextInt(id),
                AudioTrack::new,
                this::initAudioTrack);
    }

    @Override
    public Integer create(AudioTrack track) throws DaoException {
        return requestHandler.processCreateRequest(
                SQL.CREATE_TRACK_GENERATING_TRACK_NUMBER,
                track,
                this::initCreateStatement);
    }

    @Override
    public void update(AudioTrack track) throws DaoException {
        requestHandler.processUpdateRequest(
                track,
                SQL.UPDATE_INSTANCE + SQL.SelectBy.ID,
                this::initUpdateStatement,
                statement -> statement.setNextInt(track.getId()));
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
    public List<AudioTrack> findAlbumRelatedTracks(int albumId) throws DaoException {
        List<AudioTrack> audioTracks = requestHandler.processMultipleResultsSelectRequest(
                SQL.SELECT_ALL + SQL.SelectBy.ALBUM_ID + SQL.OrderBy.NUMBER,
                statement -> statement.setNextInt(albumId),
                AudioTrack::new,
                this::initAudioTrack);

        return audioTracks;
    }

    @Override
    public boolean deleteById(int id) throws DaoException {
        return 1 == requestHandler.processDeleteByIdRequest(id, SQL.DELETE_INSTANCE + SQL.SelectBy.ID);
    }

    @Override
    public boolean delete(AudioTrack track) throws DaoException {
        return 1 == requestHandler.processDeleteRequest(
                instance,
                SQL.DELETE_INSTANCE + SQL.SelectBy.ID,
                (user, statement) -> statement.setNextInt(track.getId()));
    }

    @Override
    public String deleteAndGetCorrespondingFileName(int trackId) throws DaoException {
        List<List<Map<String, String>>> requestResult = requestHandler.processCustomSelectRequest(
                SQL.DELETE_TRACK_AND_GET_FILE_BEING_DELETED_NAME_AND_REARRANGE_ALBUM_TRACKS,
                (statement) ->
                {
                    statement.setNextInt(trackId);
                });

        if (requestResult == null || requestResult.size() != 1) {
            throw new DaoException("Request result does not meet expectations");
        }

        String fileName = getTracksName(requestResult.get(0));
        return fileName;
    }

    @Override
    public String updateAudioTrackFileName(int trackId, String newFileName) throws DaoException {
        String oldTrackName = requestHandler.processStringResultRequest(
                SQL.UPDATE_AUDIO_TRACK_FILE_NAME,
                statement -> {
                    statement.setNextInt(trackId);
                    statement.setNextString(newFileName);
                    statement.setNextInt(trackId);
                });

        return oldTrackName;
    }

    @Override
    public void rearrangeAlbumTracksNumbers(int albumId) throws DaoException {
        requestHandler.processUpdateRequest(
                SQL.REARRANGE_ALBUM_TRACKS_NUMBERS,
                statement -> statement.setNextInt(albumId));
    }

    @Override
    public void shiftTrackNumberUp(int trackId) throws DaoException {
        requestHandler.processUpdateRequest(
                SQL.SHIFT_TRACK_NUMBER_UP,
                statement -> statement.setNextInt(trackId));
    }

    @Override
    public void shiftTrackNumberDown(int trackId) throws DaoException {
        requestHandler.processUpdateRequest(
                SQL.SHIFT_TRACK_NUMBER_DOWN,
                statement -> statement.setNextInt(trackId));
    }

    
    
    private void initAudioTrack(AudioTrack track, ResultSet resultSet) throws SQLException {
        track.setId(resultSet.getInt("id"));
        track.setAlbumId(resultSet.getInt("album_id"));
        track.setName(resultSet.getString("name"));
        track.setNumber(resultSet.getInt("number"));
        track.setFileName(resultSet.getString("file_name"));
        track.setVisible(resultSet.getBoolean("visible"));
    }

    private void initUpdateStatement(AudioTrack track, PreparedStatementContainer statement) throws SQLException {
        statement.setNextString(track.getName());
        statement.setNextString(track.getFileName());
        statement.setNextBoolean(track.isVisible());
        statement.setNextInt(track.getNumber());
    }

    private void initCreateStatement(AudioTrack track, PreparedStatementContainer statement) throws SQLException {
        statement.setNextInt(track.getAlbumId());
        statement.setNextString(track.getName());
        statement.setNextBoolean(track.isVisible());
        statement.setNextInt(track.getAlbumId()); // yes, it is repeated - we need this to create track number on fly
    }


    private String getTracksName(List<Map<String, String>> data) throws DaoException {
        if (data.size() == 0 || !data.get(0).containsKey("file_name")) {
            throw new DaoException("Request result does not meet expectations");
        }

        return data.get(0).get("file_name");
    }
    
}