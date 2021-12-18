package by.musicwaves.dao.requesthandler;

import by.musicwaves.dao.exception.DaoException;
import by.musicwaves.dao.util.EntityDependentStatementInitializer;
import by.musicwaves.dao.util.PreparedStatementContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

// todo: merge with UpdateRequestsWorker?

/**
 * Maintains requests that delete records from a database
 */
public class DeleteRequestsWorker extends AbstractRequestsWorker {

    private final static Logger LOGGER = LogManager.getLogger(DeleteRequestsWorker.class);

    public DeleteRequestsWorker(SQLRequestHandler requestHandler) {
        super(requestHandler);
    }

    /**
     * Deletes record from a database with provided id using provided sql string
     *
     * @param id  id to be mapped to prepared statement
     * @param sql sql string to be base of prepared statement
     * @return quantity of rows that were affected by this request
     * @throws DaoException if either something goes wrong  with connection or database treats our sql expression as an invalid one
     */
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
        } finally {
            closeStatement(statementContainer);
            returnConnection(connection, errorOccurred);
        }
    }

    /**
     * Deletes record from a database with an id that is extracted from provided entity
     *
     * @param instance    entity to extract id from
     * @param sql         sql string to be base of prepared statement
     * @param initializer special object that extracts data from supported entity object and map it to prepared statement
     * @param <T>         type of the entity that we work with
     * @return quantity of rows that were affected by this request
     * @throws DaoException if either something goes wrong with connection OR database treats our sql expression as
     *                      an invalid one OR we failed to get data from returned Result set
     */
    public <T> int processDeleteRequest(
            T instance,
            String sql,
            EntityDependentStatementInitializer<T> initializer) throws DaoException {

        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Connection connection = getConnection();
        boolean errorOccurred = false;

        try {
            statementContainer.wrap(connection.prepareStatement(sql));
            initializer.init(instance, statementContainer);
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
     * Deletes instance using provided sql string
     *
     * @param sql sql string to be directly executed
     * @return quantity of rows that were affected by this request
     * @throws DaoException if either something goes wrong  with connection or database treats our sql expression as an invalid one
     */
    public int processDeleteRequest(String sql) throws DaoException {

        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Connection connection = getConnection();
        boolean errorOccurred = false;

        try {
            statementContainer.wrap(connection.prepareStatement(sql));
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

    // todo: why did I make here 2 edsInitializers again?

    /**
     * Delete several records from a database using id-s extracted from provided entities
     *
     * @param instances       entities to get id-s from
     * @param sql             sql string to be base of prepared statement
     * @param edsInitializer1 special object that extracts data from supported entity object and map it to prepared statement
     * @param edsInitializer2 special object that extracts data from supported entity object and map it to prepared statement
     *                        (in case first one is not enough). Can be null.
     * @param <T>             type of the entity that we work with
     * @return an array of quantities of rows that were affected by that that request, each position in this array
     * is for corresponding entity set in the parameter
     * @throws DaoException if either something goes wrong with connection or database treats our sql expression as an invalid one
     */
    public <T> int[] processMultipleDeleteRequest(
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
            throw ex;
        }
    }

}
