package by.musicwaves.dao.requesthandler;

import java.sql.Connection;
import java.util.List;
import by.musicwaves.dao.connection.ConnectionPool;
import by.musicwaves.dao.exception.DaoException;
import by.musicwaves.dao.util.EntityDependentStatementInitializer;
import by.musicwaves.dao.util.EntityInitializer;
import by.musicwaves.dao.util.PreparedStatementContainerInitializer;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class SQLRequestHandler {

    private final static Logger LOGGER = LogManager.getLogger(SQLRequestHandler.class); // todo: do I need a Logger here?
    private final static ConnectionPool connectionPool = ConnectionPool.INSTANCE;
    private final static SQLRequestHandler sqlRequestHandlerInstance = new SQLRequestHandler();

    private final CreateRequestsWorker createRequestsWorker = new CreateRequestsWorker(this);
    private final SelectRequestsWorker selectRequestsWorker = new SelectRequestsWorker(this);
    private final UpdateRequestsWorker updateRequestsWorker = new UpdateRequestsWorker(this);
    private final DeleteRequestsWorker deleteRequestsWorker = new DeleteRequestsWorker(this);
    private final CustomRequestsWorker customRequestsWorker = new CustomRequestsWorker(this);

    private SQLRequestHandler() {
    }

    public static SQLRequestHandler getInstance() {
        return sqlRequestHandlerInstance;
    }

    static Connection getConnection() {
        return connectionPool.getConnection();
    }

    static void returnConnection(Connection connection) {
        connectionPool.returnConnection(connection);
    }

    static void returnInvalidConnection(Connection connection) {
        connectionPool.returnInvalidConnection(connection);
    }

    static Connection exchangeInvalidConnection(Connection connection) {
        return connectionPool.exchangeInvalidConnection(connection);
    }

    // Methods that will delegate calls to corresponding class methods //
    
    // CREATE //
    
    public <T> int processCreateRequest(
            String sql,
            T instance,
            EntityDependentStatementInitializer<T> initializer) throws DaoException {

        return createRequestsWorker.processCreateRequest(sql, instance, initializer);
    }

    public Integer processCreateRequest(
            String sql,
            PreparedStatementContainerInitializer initializer) throws DaoException {

        return createRequestsWorker.processCreateRequest(sql, initializer);
    }
    
    // SELECT //
    
    public <T> List<T> processMultipleResultsSelectRequest(
            String sql,
            PreparedStatementContainerInitializer statementInitializer,
            Supplier<T> entityCreator,
            EntityInitializer<T> entityInitializer) throws DaoException {
        
        return selectRequestsWorker.processMultipleResultsSelectRequest(
                sql, statementInitializer, entityCreator, entityInitializer);
    }
    
    public <T> T processSingleResultSelectRequest(
            String sql, PreparedStatementContainerInitializer statementInitializer,
            Supplier<T> entityCreator, EntityInitializer<T> entityInitializer) throws DaoException {
        
        return selectRequestsWorker.processSingleResultSelectRequest(
                sql, statementInitializer, entityCreator, entityInitializer);
    }
    
    public List<List<Map<String, String>>> processCustomSelectRequest(
            String sql,
            PreparedStatementContainerInitializer initializer) throws DaoException {
        
        return selectRequestsWorker.processCustomSelectRequest(sql, initializer);
    }
    
    
    // UPDATE //
    
    public <T> int processUpdateRequest(
            T instance, String sql,
            EntityDependentStatementInitializer<T> edsInitializer,
            PreparedStatementContainerInitializer sdsInitializer) throws DaoException {
        
        return updateRequestsWorker.processUpdateRequest(instance, sql, edsInitializer, sdsInitializer);
    }
    
    
    public int processUpdateRequest(
            String sql, 
            PreparedStatementContainerInitializer sdsInitializer) throws DaoException {
        
        return updateRequestsWorker.processUpdateRequest(sql, sdsInitializer);
    }
    
    public <T> int[] processMultipleUpdateRequest(
            T[] instances,
            String sql,
            EntityDependentStatementInitializer<T> edsInitializer1,
            EntityDependentStatementInitializer<T> edsInitializer2) throws DaoException {
        
        return updateRequestsWorker.processMultipleUpdateRequest(instances, sql, edsInitializer1, edsInitializer2);
    }

    public int processUpdateRequest(String sql) throws DaoException {
        return updateRequestsWorker.processUpdateRequest(sql);
    }


    // DELETE //

    public int processDeleteByIdRequest(int id, String sql) throws DaoException {
        return deleteRequestsWorker.processDeleteByIdRequest(id, sql);
    }

    public int processDeleteRequest(String sql) throws DaoException {
        return deleteRequestsWorker.processDeleteRequest(sql);
    }

    public <T> int processDeleteRequest(
            T instance,
            String sql,
            EntityDependentStatementInitializer<T> eInitializer) throws DaoException {

        return deleteRequestsWorker.processDeleteRequest(instance, sql, eInitializer);
    }

    public <T> int[] processMultipleDeleteRequest(
            T[] instances,
            String sql,
            EntityDependentStatementInitializer<T> edsInitializer1,
            EntityDependentStatementInitializer<T> edsInitializer2) throws DaoException {

        return deleteRequestsWorker.processMultipleDeleteRequest(instances, sql, edsInitializer1, edsInitializer2);
    }
    

    // CUSTOM //

    public String processStringResultRequest(String sql, PreparedStatementContainerInitializer initializer) throws DaoException {
        return customRequestsWorker.processStringResultRequest(sql, initializer);
    }

    public boolean processBooleanResultRequest(String sql, PreparedStatementContainerInitializer initializer) throws DaoException {
        return customRequestsWorker.processBooleanResultRequest(sql, initializer);
    }

    public int processIntegerResultRequest(String sql, PreparedStatementContainerInitializer initializer) throws DaoException {
        return customRequestsWorker.processIntegerResultRequest(sql, initializer);
    }


    public void processBatchRequest(String sql, PreparedStatementContainerInitializer... initializers) throws DaoException {
        customRequestsWorker.processBatchRequest(sql, initializers);
    }


}
