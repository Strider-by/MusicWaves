package by.musicwaves.dao.requesthandler;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.util.EntityDependentStatementInitializer;
import by.musicwaves.dao.util.PreparedStatementContainer;
import by.musicwaves.dao.util.PreparedStatementContainerInitializer;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

// done: checked closing statement and returning of the connection
// done: returning possibly bad connection
public class UpdateRequestsWorker extends AbstractRequestsWorker {

    private final static Logger LOGGER = LogManager.getLogger(UpdateRequestsWorker.class);

    public UpdateRequestsWorker(SQLRequestHandler requestHandler) {
        super(requestHandler);
    }


    /**
     * @return quantity of rows that were affected by executed request
     */
    public <T> int processUpdateRequest(
            T instance, String sql,
            EntityDependentStatementInitializer<T> edsInitializer,
            PreparedStatementContainerInitializer sdsInitializer) throws DaoException {

        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Connection connection = getConnection();
        boolean errorOccurred = false;
        try {
            statementContainer.wrap(connection.prepareStatement(sql));

            edsInitializer.init(instance, statementContainer);
            if (sdsInitializer != null) {
                sdsInitializer.init(statementContainer);
            }

            LOGGER.debug("update SQL request is: " + statementContainer);
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


    /**
     * @return quantity of rows that were affected by executed request
     */
    public int processUpdateRequest(
            String sql, 
            PreparedStatementContainerInitializer sdsInitializer) throws DaoException {

        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Connection connection = getConnection();
        boolean errorOccurred = false;

        try {
            statementContainer.wrap(connection.prepareStatement(sql));
            if (sdsInitializer != null) {
                sdsInitializer.init(statementContainer);
            }

            LOGGER.debug("update SQL request is: " + statementContainer);
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


    /**
     * @return quantity of rows that were affected by executed request
     */
    public int processUpdateRequest(String sql) throws DaoException {

        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Connection connection = getConnection();
        boolean errorOccurred = false;

        try {
            statementContainer.wrap(connection.prepareStatement(sql));

            LOGGER.debug("update SQL request is: " + statementContainer);
            int affectedRows = executeUpdate(statementContainer);
            return affectedRows;
        } catch (SQLException ex) {
            errorOccurred = true;
            throw new DaoException("Failed to execute update request", ex);
        } finally {
            closeStatement(statementContainer);
            returnConnection(connection, errorOccurred);
        }
    }


    // todo: why did I make here 2 edsInitializers again?
    public <T> int[] processMultipleUpdateRequest(
            T[] instances,
            String sql,
            EntityDependentStatementInitializer<T> edsInitializer1,
            EntityDependentStatementInitializer<T> edsInitializer2) throws DaoException {

        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Connection connection = getConnection();
        boolean errorOccurred = false;

        try {
            statementContainer.wrap(connection.prepareStatement(sql));
            for (T instance : instances) {
                edsInitializer1.init(instance, statementContainer);
                if (edsInitializer2 != null) {
                    edsInitializer2.init(instance, statementContainer);
                }
                statementContainer.addBatch();
            }

            LOGGER.debug("update SQL request is: " + statementContainer);
            int[] affectedRows = executeBatch(statementContainer);
            return affectedRows;

        } catch (SQLException ex) {
            errorOccurred = true;
            throw new DaoException(ex);
        } finally {
            closeStatement(statementContainer);
            returnConnection(connection, errorOccurred);
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
            LOGGER.error("Failed to execute batch update request \n" + preparedStatementContainer, ex);
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
            LOGGER.error("Failed to execute update request \n" + preparedStatementContainer, ex);
            throw ex;
        }
    }

}
