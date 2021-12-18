package by.musicwaves.dao.requesthandler;

import by.musicwaves.dao.exception.DaoException;
import by.musicwaves.dao.util.EntityDependentStatementInitializer;
import by.musicwaves.dao.util.PreparedStatementContainer;
import by.musicwaves.dao.util.PreparedStatementContainerInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Maintains requests that record newly created instanced to a database
 */
public class CreateRequestsWorker extends AbstractRequestsWorker {

    private final static Logger LOGGER = LogManager.getLogger(CreateRequestsWorker.class);

    public CreateRequestsWorker(SQLRequestHandler requestHandler) {
        super(requestHandler);
    }


    /**
     * Records instance data fields to database getting data from entity instance parameter using provided initializer.
     * Gotten parameters are being mapped to prepared statement based on provided sql string parameter.
     *
     * @param sql         sql expression to be base for creating prepared statement
     * @param instance    entity to get data for recording to database
     * @param initializer special object that extracts data from supported entity object and map it to prepared statement
     * @param <T>         type of entity that we work with
     * @return generated by database new record id
     * @throws DaoException if something goes wrong either with connection or database take our sql expression as an invalid one
     */
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

    /**
     * Records data to database using provided initializer. Parameters are being mapped to prepared statement that is
     * based on provided sql string parameter.
     *
     * @param sql         sql expression to be base for creating prepared statement
     * @param initializer special object that maps data it contains to prepared statement
     * @return generated by database new record id
     * @throws DaoException if something goes wrong either with connection or database take our sql expression as an invalid one
     */
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

    /**
     * Records data to database using provided prepared statement.
     *
     * @return generated by database new record id
     * @throws DaoException if something goes wrong either with connection or database take our sql expression as an invalid one
     */
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
            throw ex;
        }
    }

}
