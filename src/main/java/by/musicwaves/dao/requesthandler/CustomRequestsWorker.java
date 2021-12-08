package by.musicwaves.dao.requesthandler;

import by.musicwaves.dao.exception.DaoException;
import by.musicwaves.dao.util.PreparedStatementContainer;
import by.musicwaves.dao.util.PreparedStatementContainerInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

// done: checked closing statement and returning of the connection
// done: returning possibly bad connection
public class CustomRequestsWorker extends AbstractRequestsWorker {

    private final static Logger LOGGER = LogManager.getLogger(CustomRequestsWorker.class);

    public CustomRequestsWorker(SQLRequestHandler requestHandler) {
        super(requestHandler);
    }


    // SINGLE RESULT METHODS //

    public String processStringResultRequest(String sql, PreparedStatementContainerInitializer initializer) throws DaoException {
        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Connection connection = getConnection();
        boolean errorOccurred = false;

        try {
            connection.setAutoCommit(true);
            statementContainer.wrap(connection.prepareStatement(sql));
            if(initializer != null) {
                initializer.init(statementContainer);
            }

            ResultSet queryResult = statementContainer.executeQuery();
            // todo: debug and change
            // doesn't work so far
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

    public boolean processBooleanResultRequest(String sql, PreparedStatementContainerInitializer initializer) throws DaoException {

        String booleanString = processStringResultRequest(sql, initializer);

        if(!"1".equals(booleanString) && !"0".equals(booleanString)) {
            ClassCastException classCastException = new ClassCastException("Provided value does not match expected values");
            throw new DaoException(classCastException);
        }

        return "1".equals(booleanString);
    }

    public int processIntegerResultRequest(String sql, PreparedStatementContainerInitializer initializer) throws DaoException {
        String integerString = processStringResultRequest(sql, initializer);
        try {
            return Integer.valueOf(integerString);
        } catch (NumberFormatException numberFormatException) {
            throw new DaoException("Provided value is not a valid Integer number", numberFormatException);
        }
    }


    // OTHER //

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
            LOGGER.error("Failed to execute batch request", ex);
            throw new DaoException(ex);
        } finally {
            closeStatement(statementContainer);
            returnConnection(connection, errorOccurred);
        }
    }

}
