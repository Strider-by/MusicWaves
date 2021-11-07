package by.musicwaves.dao.requesthandler;

import by.musicwaves.dao.util.PreparedStatementContainer;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class AbstractRequestsWorker {

    protected SQLRequestHandler requestHandler;
    private final static Logger LOGGER = LogManager.getLogger(AbstractRequestsWorker.class);

    public AbstractRequestsWorker(SQLRequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    protected Connection getConnection() {
        return requestHandler.getConnection();
    }

    protected void returnConnection(Connection connection) {
        requestHandler.returnConnection(connection);
    }

    protected void returnConnection(Connection connection, boolean errorOccurred) {
        if (errorOccurred) {
            returnInvalidConnection(connection);
        } else {
            returnConnection(connection);
        }
    }

    protected void returnInvalidConnection(Connection connection) {
        requestHandler.returnInvalidConnection(connection);
    }

    protected Connection exchangeInvalidConnection(Connection connection) {
        return requestHandler.exchangeInvalidConnection(connection);
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
