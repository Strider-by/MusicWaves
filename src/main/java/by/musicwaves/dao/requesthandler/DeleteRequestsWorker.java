package by.musicwaves.dao.requesthandler;

import by.musicwaves.dao.exception.DaoException;
import by.musicwaves.dao.util.EntityDependentStatementInitializer;
import by.musicwaves.dao.util.PreparedStatementContainer;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

// todo: merge with UpdateRequestsWorker?
// done: checked closing statement and returning of the connection
// done: returning possibly bad connection
public class DeleteRequestsWorker extends AbstractRequestsWorker
{
    private final static Logger LOGGER = LogManager.getLogger(DeleteRequestsWorker.class);

    public DeleteRequestsWorker(SQLRequestHandler requestHandler) {
        super(requestHandler);
    }


    public int processDeleteByIdRequest(Integer id, String sql) throws DaoException {

        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Connection connection = getConnection();
        boolean errorOccurred = false;

        try {
            statementContainer.wrap(connection.prepareStatement(sql));
            statementContainer.setNextInt(id);
            int affectedRows = executeUpdate(statementContainer);
            return affectedRows;
        } catch (SQLException ex) {
            errorOccurred = true;
            throw new DaoException(ex);
        }
        finally {
            closeStatement(statementContainer);
            returnConnection(connection, errorOccurred);
        }
    }

    public <T> int processDeleteRequest(
            T instance,
            String sql,
            EntityDependentStatementInitializer<T> eInitializer) throws DaoException {

        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Connection connection = getConnection();
        boolean errorOccurred = false;

        try {
            statementContainer.wrap(connection.prepareStatement(sql));
            eInitializer.init(instance, statementContainer);
            int affectedRows = executeUpdate(statementContainer);
            return affectedRows;
        } catch (SQLException ex) {
            errorOccurred = true;
            throw new DaoException(ex);
        } finally {
            closeStatement(statementContainer);
            returnConnection(connection, errorOccurred);
        }
    }

    public int processDeleteRequest(String sql) throws DaoException {

        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Connection connection = getConnection();
        boolean errorOccured = false;

        try {
            statementContainer.wrap(connection.prepareStatement(sql));
            int affectedRows = executeUpdate(statementContainer);
            return affectedRows;
        } catch (SQLException ex) {
            errorOccured = true;
            throw new DaoException(ex);
        } finally {
            closeStatement(statementContainer);
            returnConnection(connection, errorOccured);
        }
    }

    // todo: why did I make here 2 edsInitializers again?
    public <T> int[] processMultipleDeleteRequest(
            T[] instances,
            String sql,
            EntityDependentStatementInitializer<T> edsInitializer1,
            EntityDependentStatementInitializer<T> edsInitializer2) throws DaoException {

        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Connection connection = getConnection();
        boolean errorOccured = false;

        try {
            statementContainer.wrap(connection.prepareStatement(sql));
            for (T instance : instances) {
                edsInitializer1.init(instance, statementContainer);
                if (edsInitializer2 != null) {
                    edsInitializer2.init(instance, statementContainer);
                }
                statementContainer.addBatch();
            }

            int[] affectedRows = executeBatch(statementContainer);
            return affectedRows;

        } catch (SQLException ex) {
            errorOccured = true;
            throw new DaoException(ex);
        } finally {
            closeStatement(statementContainer);
            returnConnection(connection, errorOccured);
        }
    }

    private int[] executeBatch(PreparedStatementContainer preparedStatementContainer) throws SQLException {
        Connection connection = preparedStatementContainer.getConnection();
        try {
            connection.setAutoCommit(true);
            LOGGER.debug("Request is: \n" + preparedStatementContainer);
            int[] affectedRows = preparedStatementContainer.executeBatch();
            return affectedRows;
        } catch (SQLException ex) {
            LOGGER.error("Failed to execute batch delete request \n" + preparedStatementContainer, ex);
            throw ex;
        }
    }

    private int executeUpdate(PreparedStatementContainer preparedStatementContainer) throws SQLException {
        Connection connection = preparedStatementContainer.getConnection();
        try {
            connection.setAutoCommit(true);
            LOGGER.debug("Request is: \n" + preparedStatementContainer);
            int affectedRows = preparedStatementContainer.executeUpdate();
            return affectedRows;
        } catch (SQLException ex) {
            LOGGER.error("Failed to execute delete request \n" + preparedStatementContainer, ex);
            throw ex;
        }
    }

}
