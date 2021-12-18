package by.musicwaves.dao.requesthandler;

import by.musicwaves.dao.util.PreparedStatementContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class AbstractRequestsWorker {

    private final static Logger LOGGER = LogManager.getLogger(AbstractRequestsWorker.class);
    protected SQLRequestHandler requestHandler;

    public AbstractRequestsWorker(SQLRequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    protected Connection getConnection() {
        return SQLRequestHandler.getConnection();
    }

    protected void returnConnection(Connection connection) {
        SQLRequestHandler.returnConnection(connection);
    }

    protected void returnConnection(Connection connection, boolean errorOccurred) {
        if (errorOccurred) {
            returnInvalidConnection(connection);
        } else {
            returnConnection(connection);
        }
    }

    protected void returnInvalidConnection(Connection connection) {
        SQLRequestHandler.returnInvalidConnection(connection);
    }

    protected Connection exchangeInvalidConnection(Connection connection) {
        return SQLRequestHandler.exchangeInvalidConnection(connection);
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
