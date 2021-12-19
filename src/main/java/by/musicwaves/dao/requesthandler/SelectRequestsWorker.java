package by.musicwaves.dao.requesthandler;

import by.musicwaves.dao.exception.DaoException;
import by.musicwaves.dao.util.EntityInitializer;
import by.musicwaves.dao.util.PreparedStatementContainer;
import by.musicwaves.dao.util.PreparedStatementContainerInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Works with requests that return some value from a database
 */
public class SelectRequestsWorker extends AbstractRequestsWorker {

    private final static Logger LOGGER = LogManager.getLogger(SelectRequestsWorker.class);

    /**
     * Processes request that returns several results
     *
     * @param sql                  sql string to be base of prepared statement
     * @param statementInitializer object that is used to initialize sql statement
     * @param entityCreator        object that creates entity that needs to be filled with data gotten by the request
     * @param entityInitializer    object that fills entity with data based on returned Result set row
     * @param <T>                  type of the entity we work with
     * @return list of newly created entities
     * @throws DaoException if either something goes wrong with connection OR database treats our sql expression as
     *                      an invalid one OR we failed to get data from returned Result set
     */
    public <T> List<T> processMultipleResultsSelectRequest(
            String sql,
            PreparedStatementContainerInitializer statementInitializer,
            Supplier<T> entityCreator,
            EntityInitializer<T> entityInitializer) throws DaoException {

        List<T> list = new ArrayList<>();
        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Connection connection = getConnection();
        boolean errorOccurred = false;

        try {
            connection.setAutoCommit(true);
            statementContainer.wrap(connection.prepareStatement(sql));
            if (statementInitializer != null) {
                statementInitializer.init(statementContainer);
            }
            LOGGER.debug("Filled sql: " + statementContainer);

            try (ResultSet resultSet = statementContainer.executeQuery()) {
                while (resultSet.next()) {
                    T instance = entityCreator.get();
                    entityInitializer.init(instance, resultSet);
                    list.add(instance);
                }

                return list;
            }
        } catch (SQLException ex) {
            errorOccurred = true;
            throw new DaoException("Failed to execute request \n" + statementContainer, ex);
        } finally {
            closeStatement(statementContainer);
            returnConnection(connection, errorOccurred);
        }

    }


    // todo: switch to Optional<T> to avoid possible null returning?

    /**
     * Precesses request that returns single result and transform returned data to an entity object
     *
     * @param sql                  sql string to be base of prepared statement
     * @param statementInitializer object that is used to initialize sql statement
     * @param entityCreator        object that creates entity that needs to be filled with data gotten by the request
     * @param entityInitializer    object that fills entity with data based on returned Result set row
     * @param <T>                  type of the entity we work with
     * @return newly created entity
     * @throws DaoException if either something goes wrong with connection OR database treats our sql expression as
     *                      an invalid one OR we failed to get data from returned Result set
     */
    public <T> T processSingleResultSelectRequest(
            String sql, PreparedStatementContainerInitializer statementInitializer,
            Supplier<T> entityCreator, EntityInitializer<T> entityInitializer) throws DaoException {

        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Connection connection = getConnection();
        boolean errorOccurred = false;

        try {
            connection.setAutoCommit(true);
            statementContainer.wrap(connection.prepareStatement(sql));
            if (statementInitializer != null) {
                statementInitializer.init(statementContainer);
            }

            try (ResultSet resultSet = statementContainer.executeQuery()) {
                if (resultSet.next()) {
                    T instance = entityCreator.get();
                    entityInitializer.init(instance, resultSet);
                    // success, requested item found
                    return instance;
                } else {
                    // success, but requested item was not found in database
                    return null;
                }
            }
        } catch (SQLException ex) {
            errorOccurred = true;
            throw new DaoException("Failed to execute request \n" + statementContainer, ex);
        } finally {
            closeStatement(statementContainer);
            returnConnection(connection, errorOccurred);
        }
    }


    /**
     * Processes request that returns several result sets. To be able to return such result, all result sets
     * are transformed to {@link List}<{@link Map}> of strings where each List represents separate Result set
     * and each element of List (Map of strings) represents data row of that Result set. Keys of this Map are names
     * of columns in Result set so Access to particular value we seek can be gotten by: <pre>
     * list.get(n)  --  get result set by it's number;
     *     .get(m)  --  get data row by it's number starting from 0
     *     .get(o)  --  get particular data cell by the name of it's column. </pre>
     * If subrequest does not return result set, it isn't represented in resulting list.
     *
     * @param sql sql string to be base of prepared statement
     * @return list of lists of maps of strings that can be used to get required data
     * @throws DaoException if either something goes wrong with connection OR database treats our sql expression as
     *                      an invalid one OR we failed to get data from returned Result set
     */
    public List<List<Map<String, String>>> processCustomSelectRequest(
            String sql,
            PreparedStatementContainerInitializer initializer) throws DaoException {

        List<List<Map<String, String>>> globalResultData = new ArrayList<>();
        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Connection connection = getConnection();
        boolean errorOccurred = false;

        try {
            connection.setAutoCommit(true);
            statementContainer.wrap(connection.prepareStatement(sql));
            if (initializer != null) {
                initializer.init(statementContainer);
            }
            LOGGER.debug("Filled sql: " + statementContainer);
            boolean hasNextResultSet = statementContainer.execute();

            // parsing result sets we got as a result of our request(s) execution
            // each result set is represented by a separate list (currentResultSetData)
            // each row from single result set is being held as Map<String, String>,
            // where key is column label and value is cell content
            while (true) {
                if (hasNextResultSet) {
                    List<Map<String, String>> currentResultSetData = new ArrayList<>();
                    try (ResultSet resultSet = statementContainer.getResultSet()) {

                        while (resultSet.next()) {
                            Map<String, String> rowData = new HashMap<>();
                            ResultSetMetaData metaData = resultSet.getMetaData();
                            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                                String name = metaData.getColumnLabel(i);
                                String value = resultSet.getString(i);
                                rowData.put(name, value);
                            }

                            currentResultSetData.add(rowData);
                        }
                    }

                    globalResultData.add(currentResultSetData);
                } else {
                    /*
                    From javadoc:
                    There are no more results when the following is true:
                    // stmt is a Statement object
                    ((stmt.getMoreResults() == false) && (stmt.getUpdateCount() == -1))
                     */
                    if (statementContainer.getUpdateCount() == -1) {
                        break;
                    }
                }

                hasNextResultSet = statementContainer.getMoreResults();
            }

            LOGGER.debug("Custom select request, rows read: " + globalResultData.size());
            return globalResultData;
        } catch (SQLException ex) {
            errorOccurred = true;
            LOGGER.error("Failed to execute request \n" + statementContainer, ex);
            throw new DaoException(ex);
        } finally {
            closeStatement(statementContainer);
            returnConnection(connection, errorOccurred);
        }
    }


}