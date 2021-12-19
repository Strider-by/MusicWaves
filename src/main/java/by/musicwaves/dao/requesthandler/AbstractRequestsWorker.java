package by.musicwaves.dao.requesthandler;

import by.musicwaves.dao.connection.ConnectionPool;
import by.musicwaves.dao.util.PreparedStatementContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class AbstractRequestsWorker {

    private final static Logger LOGGER = LogManager.getLogger(AbstractRequestsWorker.class);
    private final static ConnectionPool connectionPool = ConnectionPool.INSTANCE;

    protected Connection getConnection() {
        return connectionPool.getConnection();
    }

    protected void returnConnection(Connection connection) {
        connectionPool.returnConnection(connection);
    }

    protected void returnConnection(Connection connection, boolean errorOccurred) {
        if (errorOccurred) {
            connectionPool.returnInvalidConnection(connection);
        } else {
            connectionPool.returnConnection(connection);
        }
    }

    protected void returnInvalidConnection(Connection connection) {
        connectionPool.returnInvalidConnection(connection);
    }

    protected Connection exchangeInvalidConnection(Connection connection) {
        return connectionPool.exchangeInvalidConnection(connection);
    }

    protected void closeStatement(PreparedStatementContainer statementContainer) {
        try {
            if (statementContainer.getInnerStatement() != null) {
                statementContainer.close();
            }
        } catch (SQLException ex) {
            LOGGER.error("Failed to close statement", ex);
        }
    }

}
