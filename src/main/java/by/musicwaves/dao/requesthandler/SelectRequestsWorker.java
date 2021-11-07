package by.musicwaves.dao.requesthandler;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.util.EntityInitializer;
import by.musicwaves.dao.util.PreparedStatementContainer;
import by.musicwaves.dao.util.PreparedStatementContainerInitializer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

// done: checked closing statement and returning of the connection
// done: returning possibly bad connection
public class SelectRequestsWorker extends AbstractRequestsWorker {

    private final static Logger LOGGER = LogManager.getLogger(SelectRequestsWorker.class);

    public SelectRequestsWorker(SQLRequestHandler requestHandler) {
        super(requestHandler);
    }

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
            LOGGER.error("Failed to execute request \n" + statementContainer, ex);
            throw new DaoException(ex);
        } finally {
            closeStatement(statementContainer);
            returnConnection(connection, errorOccurred);
        }

    }


    // todo: switch to Optional<T> to avoid possible null returning?
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
            LOGGER.error("Failed to execute request \n" + statementContainer, ex);
            throw new DaoException(ex);
        } finally {
            closeStatement(statementContainer);
            returnConnection(connection, errorOccurred);
        }
    }


    // todo: test it
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