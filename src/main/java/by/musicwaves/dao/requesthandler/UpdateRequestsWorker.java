package by.musicwaves.dao.requesthandler;

import by.musicwaves.dao.exception.DaoException;
import by.musicwaves.dao.util.EntityDependentStatementInitializer;
import by.musicwaves.dao.util.PreparedStatementContainer;
import by.musicwaves.dao.util.PreparedStatementContainerInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Works with requests that updates data already stored in a database
 */
public class UpdateRequestsWorker extends AbstractRequestsWorker {

    private final static Logger LOGGER = LogManager.getLogger(UpdateRequestsWorker.class);

    public UpdateRequestsWorker(SQLRequestHandler requestHandler) {
        super(requestHandler);
    }

    /**
     * Updates data in a database based on data of provided entity parameter
     *
     * @param instance       entity to extract data that shall be placed in database
     * @param sql            sql string to be base of prepared statement
     * @param edsInitializer initializer that maps data to prepared statement taking it from provided entity
     * @param sdsInitializer initializer that maps data to prepared statement. Not dependent on provided entity object
     * @param <T>            type of the entity we work with
     * @return quantity of rows that were affected by executed request
     * @throws DaoException if either something goes wrong with connection OR database treats our sql expression as
     *                      an invalid one OR we failed to get data from returned Result set
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
     * Updates data in a database based on data of provided entity parameter
     *
     * @param sql            sql string to be base of prepared statement
     * @param sdsInitializer initializer that maps data to prepared statement. Not dependent on provided entity object
     * @return quantity of rows that were affected by executed request
     * @throws DaoException if either something goes wrong with connection OR database treats our sql expression as
     *                      an invalid one OR we failed to get data from returned Result set
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
     * Updates instance using provided sql string
     *
     * @param sql sql string to be directly executed
     * @return quantity of rows that were affected by this request
     * @throws DaoException if either something goes wrong  with connection or database treats our sql expression as an invalid one
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

    /**
     * Update several records in a database using provided entities
     *
     * @param instances       entities to get data from
     * @param sql             sql string to be base of prepared statement
     * @param edsInitializer1 special object that extracts data from supported entity object and map it to prepared statement
     * @param edsInitializer2 special object that extracts data from supported entity object and map it to prepared statement
     *                        (in case first one is not enough). Can be null.
     * @param <T>             type of the entity that we work with
     * @return an array of quantities of rows that were affected by that that request, each position in this array
     * is for corresponding entity set in the parameter
     * @throws DaoException if either something goes wrong with connection or database treats our sql expression as an invalid one
     */
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
