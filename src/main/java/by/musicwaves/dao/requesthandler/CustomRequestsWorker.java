package by.musicwaves.dao.requesthandler;

import by.musicwaves.dao.exception.DaoException;
import by.musicwaves.dao.util.PreparedStatementContainer;
import by.musicwaves.dao.util.PreparedStatementContainerInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maintains requests that don't fit other Request workers
 */
public class CustomRequestsWorker extends AbstractRequestsWorker {

    private final static Logger LOGGER = LogManager.getLogger(CustomRequestsWorker.class);

    public CustomRequestsWorker(SQLRequestHandler requestHandler) {
        super(requestHandler);
    }


    // SINGLE RESULT METHODS //

    /**
     * Processes request that returns single string value as the result
     *
     * @param sql         - sql string to be base of prepared statement
     * @param initializer - special object that maps data it contains to prepared statement
     * @return string gotten as the result of built request
     * @throws DaoException if something goes wrong either with connection or database take our sql expression as an invalid one
     */
    public String processStringResultRequest(String sql, PreparedStatementContainerInitializer initializer) throws DaoException {
        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Connection connection = getConnection();
        boolean errorOccurred = false;

        try {
            connection.setAutoCommit(true);
            statementContainer.wrap(connection.prepareStatement(sql));
            if (initializer != null) {
                initializer.init(statementContainer);
            }

            ResultSet queryResult = statementContainer.executeQuery();
            // todo: change it the way it shall work even when statement is complex and contains substatements
            //  and the first substatement returns nothing
            boolean resultIsPresent = queryResult.next();
            LOGGER.debug("Result is present " + resultIsPresent);
            return queryResult.getString(1);
        } catch (SQLException ex) {
            errorOccurred = true;
            LOGGER.error("Failed to execute SQL request: \n" + statementContainer, ex);
            throw new DaoException(ex);
        } finally {
            closeStatement(statementContainer);
            returnConnection(connection, errorOccurred);
        }
    }

    /**
     * Processes request that returns single boolean value as the result
     *
     * @param sql         - sql string to be base of prepared statement
     * @param initializer - special object that maps data it contains to prepared statement
     * @return boolean gotten as the result of built request
     * @throws DaoException if something goes wrong either with connection or database take our sql expression as
     *                      an invalid one, OR the value we got from this request can not be converted to boolean
     */
    public boolean processBooleanResultRequest(String sql, PreparedStatementContainerInitializer initializer) throws DaoException {

        String booleanString = processStringResultRequest(sql, initializer);

        if (!"1".equals(booleanString) && !"0".equals(booleanString)) {
            ClassCastException classCastException = new ClassCastException("Provided value does not match expected values");
            throw new DaoException(classCastException);
        }

        return "1".equals(booleanString);
    }

    /**
     * Processes request that returns single int value as the result
     *
     * @param sql         - sql string to be base of prepared statement
     * @param initializer - special object that maps data it contains to prepared statement
     * @return int gotten as the result of built request
     * @throws DaoException if something goes wrong either with connection or database take our sql expression as
     *                      an invalid one, OR the value we got from this request can not be converted to int
     */
    public int processIntegerResultRequest(String sql, PreparedStatementContainerInitializer initializer) throws DaoException {
        String integerString = processStringResultRequest(sql, initializer);
        try {
            return Integer.valueOf(integerString);
        } catch (NumberFormatException numberFormatException) {
            throw new DaoException("Provided value is not a valid Integer number", numberFormatException);
        }
    }


    // OTHER //

    /**
     * Processes request that works with multiple similar requests fit into one general request
     *
     * @param sql          sql string to be base of prepared statement
     * @param initializers special objects that maps data they contain to prepared statement
     * @throws DaoException if something goes wrong either with connection or database take our sql expression as an invalid one
     */
    public void processBatchRequest(String sql, PreparedStatementContainerInitializer... initializers) throws DaoException {
        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Connection connection = getConnection();
        boolean errorOccurred = false;

        try {
            connection.setAutoCommit(true);
            statementContainer.wrap(connection.prepareStatement(sql));

            for (PreparedStatementContainerInitializer initializer : initializers) {
                initializer.init(statementContainer);
                statementContainer.addBatch();
            }

            LOGGER.debug("Batch SQL request is: \n" + statementContainer);
            statementContainer.executeBatch();

        } catch (SQLException ex) {
            errorOccurred = true;
            throw new DaoException(ex);
        } finally {
            closeStatement(statementContainer);
            returnConnection(connection, errorOccurred);
        }
    }

}
