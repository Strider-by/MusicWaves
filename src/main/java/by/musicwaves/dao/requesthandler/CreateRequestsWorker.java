package by.musicwaves.dao.requesthandler;

import by.musicwaves.dao.exception.DaoException;
import by.musicwaves.dao.util.EntityDependentStatementInitializer;
import by.musicwaves.dao.util.PreparedStatementContainer;
import by.musicwaves.dao.util.PreparedStatementContainerInitializer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

// done: checked closing statement and returning of the connection
// done: returning possibly bad connection
public class CreateRequestsWorker extends AbstractRequestsWorker {

    private final static Logger LOGGER = LogManager.getLogger(CreateRequestsWorker.class);

    public CreateRequestsWorker(SQLRequestHandler requestHandler) {
        super(requestHandler);
    }


    public <T> int processCreateRequest(
            String sql,
            T instance,
            EntityDependentStatementInitializer<T> initializer) throws DaoException {

        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Connection connection = getConnection();
        boolean errorOccurred = false;

        try {
            statementContainer.wrap(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS));
            initializer.init(instance, statementContainer);
            return processCreateRequest(statementContainer);
        } catch (SQLException ex) {
            errorOccurred = true;
            throw new DaoException(ex);
        } finally {
            closeStatement(statementContainer);
            returnConnection(connection, errorOccurred);
        }
    }

    public int processCreateRequest(
            String sql,
            PreparedStatementContainerInitializer initializer) throws DaoException {

        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Connection connection = getConnection();
        boolean errorOccurred = false;

        try {
            statementContainer.wrap(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS));
            initializer.init(statementContainer);
            return processCreateRequest(statementContainer);
        } catch (SQLException ex) {
            errorOccurred = true;
            throw new DaoException(ex);
        } finally {
            closeStatement(statementContainer);
            returnConnection(connection, errorOccurred);
        }
    }

    private int processCreateRequest(PreparedStatementContainer preparedStatementContainer) throws SQLException {

        Connection connection = preparedStatementContainer.getConnection();

        try {
            connection.setAutoCommit(true);
            int affectedRows = preparedStatementContainer.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creation failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatementContainer.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // success
                    return generatedKeys.getInt(1);
                } else {
                    // failure
                    throw new SQLException("Creation failed, no ID obtained.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Failed to execute request \n" + preparedStatementContainer, ex);
            throw ex;
        }
    }

}
