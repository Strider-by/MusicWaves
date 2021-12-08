//package by.musicwaves.dao.util;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.List;
//import by.musicwaves.dao.connection.ConnectionPoolV4;
//import by.musicwaves.dao.connection.ProtectedConnection;
//import by.musicwaves.dao.exception.DaoException;
//import java.sql.ResultSetMetaData;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Supplier;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class SQLRequestHandlerOld implements AutoCloseable
//{
//    private final static Logger LOGGER = LoggerFactory.getLogger(SQLRequestHandlerOld.class);
//    private final ConnectionPoolV4 connectionPool = ConnectionPoolV4.INSTANCE;
//    private ProtectedConnection connection = connectionPool.getConnection();
//    private int attemptsToExchangeConnection = 10;
//    private boolean connectionIsAlreadyReturned = false;
//
//    @Override
//    public void close()
//    {
//        if (!connectionIsAlreadyReturned)
//        {
//            returnConnection();
//        }
//    }
//
//    public <T> List<T> processMultipleResultsSelectRequest(
//            String sql,
//            Supplier<T> entityCreator,
//            PreparedStatementContainerInitializer statementInitializer,
//            EntityInitializer<T> entityInitializer) throws DaoException
//    {
//
//        List<T> list = new ArrayList<>();
//        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
//        
//        do
//        {
//            try
//            {
//                statementContainer.wrap(connection.prepareStatement(sql));
//                if (statementInitializer != null)
//                {
//                    statementInitializer.init(statementContainer);
//                }
//                LOGGER.debug("Filled sql: " + statementContainer);
//
//                try (ResultSet resultSet = statementContainer.executeQuery())
//                {
//                    while (resultSet.next())
//                    {
//                        T instance = entityCreator.get();
//                        entityInitializer.init(instance, resultSet);
//                        list.add(instance);
//                    }
//                }
//                
//                return list;
//            }
//            catch (SQLException ex)
//            {
//                // m.b. connection is invalid?
//                // exchange it and continue...
//                LOGGER.warn("Failed to execute request using current connection", ex);
//            }
//            finally
//            {
//                try
//                {
//                    if (statementContainer.getInnerStatement() != null)
//                    {
//                        statementContainer.close();
//                    }
//                }
//                catch (SQLException ex)
//                {
//                    LOGGER.error("Failed to close prepared statement", ex);
//                }
//            }
//        }
//        while (tryToExchangePossiblyInvalidConnection());
//        
//        LOGGER.error("Failed to execute request");
//        throw new DaoException("Failed to execute request");        
//    }
//
//    public <T> T processSingleResultSelectRequest(
//            String sql, Supplier<T> entityCreator,
//            PreparedStatementContainerInitializer statementInitializer,
//            EntityInitializer<T> entityInitializer) throws DaoException
//    {
//        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
//        
//        do
//        {
//            try
//            {
//                statementContainer.wrap(connection.prepareStatement(sql));
//                if (statementInitializer != null)
//                {
//                    statementInitializer.init(statementContainer);
//                }
//                
//                try (ResultSet resultSet = statementContainer.executeQuery())
//                {
//                    if (resultSet.next())
//                    {
//                        T instance = entityCreator.get();
//                        entityInitializer.init(instance, resultSet);
//                        return instance;
//                    }
//                    else
//                    {
//                        resultSet.close();
//                        return null;
//                    }
//                }
//            }
//            catch (SQLException ex)
//            {
//                // connection m.b. invalid
//                // exchange it and continue...
//                LOGGER.warn("Failed to execute request using current connection", ex);
//            }
//            finally
//            {
//                try
//                {
//                    if (statementContainer.getInnerStatement() != null)
//                    {
//                        statementContainer.close();
//                    }
//                }
//                catch (SQLException ex)
//                {
//                    LOGGER.error("Failed to close prepared statement", ex);
//                }
//            }
//        }
//        while (tryToExchangePossiblyInvalidConnection());
//        
//        LOGGER.error("Failed to execute request");
//        throw new DaoException("Failed to execute request");   
//    }
//
//    public <T> int processCreateRequest(
//            T instance,
//            String sql,
//            EntityDependentStatementInitializer<T> initializer) throws DaoException
//    {
//
//        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
//        int databaseId;
//        
//        do
//        {
//            try
//            {
//                statementContainer.wrap(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS));
//                initializer.init(instance, statementContainer);
//
//                int affectedRows = statementContainer.executeUpdate();
//                if (affectedRows == 0)
//                {
//                    throw new SQLException("Creation failed, no rows affected.");
//                }
//
//                try (ResultSet generatedKeys = statementContainer.getGeneratedKeys())
//                {
//                    if (generatedKeys.next())
//                    {
//                        databaseId = generatedKeys.getInt(1);
//                    }
//                    else
//                    {
//                        connection.rollback();
//                        throw new SQLException("Creation failed, no ID obtained.");
//                    }
//                }
//                
//                return databaseId;
//            }
//            catch (SQLException ex)
//            {
//                // connection m.b. invalid
//                // exchange it and continue...
//                LOGGER.warn("Failed to execute request using current connection", ex);
//            }
//            finally
//            {
//                try
//                {
//                    if (statementContainer.getInnerStatement() != null)
//                    {
//                        statementContainer.close();
//                    }
//                }
//                catch (SQLException ex)
//                {
//                    LOGGER.error("Failed to close statement", ex);
//                }
//            }
//        }
//        while (tryToExchangePossiblyInvalidConnection());
//        
//        LOGGER.error("Failed to execute request");
//        throw new DaoException("Failed to execute request"); 
//    }
//
//    public Integer processCreateRequest(
//            String sql,
//            PreparedStatementContainerInitializer initializer) throws DaoException
//    {
//
//        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
//        Integer result = null;
//
//        try
//        {
//            statementContainer.wrap(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS));
//            if (initializer != null)
//            {
//                initializer.init(statementContainer);
//            }
//
//            int affectedRows = statementContainer.executeUpdate();
//            if (affectedRows == 0)
//            {
//                throw new SQLException("Creation failed, no rows affected.");
//            }
//
//            try (ResultSet generatedKeys = statementContainer.getGeneratedKeys())
//            {
//                if (generatedKeys.next())
//                {
//                    result = generatedKeys.getInt(1);
//                }
//                else
//                {
//                    throw new SQLException("Creation failed, no ID obtained.");
//                }
//            }
//            return result;
//        }
//        catch (SQLException ex)
//        {
//            throw new DaoException("Failed to execute request", ex);
//        }
//        finally
//        {
//            try
//            {
//                if (statementContainer.getInnerStatement() != null)
//                {
//                    statementContainer.close();
//                }
//            }
//            catch (SQLException ex)
//            {
//                possiblyBadConnection = true;
//                LOGGER.error("Failed to close statement", ex);
//            }
//        }
//    }
//
//    public <T> T processUpdateRequest(
//            T instance, String sql,
//            EntityDependentStatementInitializer<T> edsInitializer,
//            PreparedStatementContainerInitializer sdsInitializer) throws DaoException
//    {
//        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
//
//        try
//        {
//            statementContainer.wrap(connection.prepareStatement(sql));
//
//            edsInitializer.init(instance, statementContainer);
//            if (sdsInitializer != null)
//            {
//                sdsInitializer.init(statementContainer);
//            }
//
//            LOGGER.debug("update SQL request is: " + statementContainer);
//            int affectedRows = statementContainer.executeUpdate();
//            if (affectedRows == 0)
//            {
//                throw new SQLException("Update failed, no rows affected.");
//            }
//
//            return instance;
//        }
//        catch (SQLException ex)
//        {
//            throw new DaoException("Failed to execute request", ex);
//        }
//        finally
//        {
//            try
//            {
//                if (statementContainer.getInnerStatement() != null)
//                {
//                    statementContainer.close();
//                }
//            }
//            catch (SQLException ex)
//            {
//                possiblyBadConnection = true;
//                LOGGER.error("Failed to close statement", ex);
//            }
//        }
//    }
//
//    public int processCustomUpdateRequest(String sql, PreparedStatementContainerInitializer sdsInitializer) throws DaoException
//    {
//        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
//
//        try
//        {
//            statementContainer.wrap(connection.prepareStatement(sql));
//            if (sdsInitializer != null)
//            {
//                sdsInitializer.init(statementContainer);
//            }
//
//            LOGGER.debug("update SQL request is: " + statementContainer);
//            int affectedRows = statementContainer.executeUpdate();
//            return affectedRows;
//        }
//        catch (SQLException ex)
//        {
//            throw new DaoException("Failed to execute SQL update request", ex);
//        }
//        finally
//        {
//            try
//            {
//                if (statementContainer.getInnerStatement() != null)
//                {
//                    statementContainer.close();
//                }
//            }
//            catch (SQLException ex)
//            {
//                possiblyBadConnection = true;
//                LOGGER.error("Failed to close statement", ex);
//            }
//        }
//    }
//
//    public <T> void processMultipleUpdateRequest(
//            T[] instances,
//            String sql,
//            EntityDependentStatementInitializer<T> edsInitializer1,
//            EntityDependentStatementInitializer<T> edsInitializer2) throws DaoException
//    {
//        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
//        try
//        {
//            connection.setAutoCommit(false);
//            statementContainer.wrap(connection.prepareStatement(sql));
//
//            for (T instance : instances)
//            {
//                edsInitializer1.init(instance, statementContainer);
//                if (edsInitializer2 != null)
//                {
//                    edsInitializer2.init(instance, statementContainer);
//                }
//                statementContainer.addBatch();
//            }
//
//            LOGGER.debug("update SQL request is: " + statementContainer);
//            statementContainer.executeBatch();
//            connection.commit();
//
//        }
//        catch (SQLException ex)
//        {
//            possiblyBadConnection = true;
//            try
//            {
//                connection.rollback();
//            }
//            catch (SQLException rollbackEx)
//            {
//                possiblyBadConnection = true;
//                LOGGER.error("failed to rollback operation", rollbackEx);
//            }
//            throw new DaoException("Failed to execute request", ex);
//        }
//        finally
//        {
//            try
//            {
//                connection.setAutoCommit(true);
//            }
//            catch (SQLException ex)
//            {
//                LOGGER.error("Failed to set autocommit true", ex);
//                possiblyBadConnection = true;
//            }
//
//            try
//            {
//                if (statementContainer.getInnerStatement() != null)
//                {
//                    statementContainer.close();
//                }
//            }
//            catch (SQLException ex)
//            {
//                possiblyBadConnection = true;
//                LOGGER.error("Failed to close statement", ex);
//            }
//        }
//    }
//
//    public void processBatchRequest(String sql, PreparedStatementContainerInitializer... initializers) throws DaoException
//    {
//        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
//        try
//        {
//            connection.setAutoCommit(false);
//            statementContainer.wrap(connection.prepareStatement(sql));
//
//            for (PreparedStatementContainerInitializer initializer : initializers)
//            {
//                initializer.init(statementContainer);
//                statementContainer.addBatch();
//            }
//
//            LOGGER.debug("batch SQL request is: " + statementContainer);
//            statementContainer.executeBatch();
//            connection.commit();
//
//        }
//        catch (SQLException ex)
//        {
//            try
//            {
//                connection.rollback();
//            }
//            catch (SQLException rollbackEx)
//            {
//                possiblyBadConnection = true;
//                LOGGER.error("Failed to rollback operation", rollbackEx);
//            }
//            throw new DaoException("Failed to execute  request", ex);
//        }
//        finally
//        {
//            try
//            {
//                connection.setAutoCommit(true);
//            }
//            catch (SQLException ex)
//            {
//                possiblyBadConnection = true;
//                LOGGER.error("Failed to set autocommit true", ex);
//            }
//
//            try
//            {
//                if (statementContainer.getInnerStatement() != null)
//                {
//                    statementContainer.close();
//                }
//            }
//            catch (SQLException ex)
//            {
//                possiblyBadConnection = true;
//                LOGGER.error("Failed to close statement", ex);
//            }
//        }
//    }
//
//    public boolean processDeleteRequest(Integer id, String sql) throws DaoException
//    {
//        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
//        boolean result;
//
//        try
//        {
//            statementContainer.wrap(connection.prepareStatement(sql));
//            statementContainer.setNextInt(id);
//            result = statementContainer.executeUpdate() > 0;
//
//            return result;
//        }
//        catch (SQLException ex)
//        {
//            throw new DaoException("Failed to execute request", ex);
//        }
//        finally
//        {
//            try
//            {
//                if (statementContainer.getInnerStatement() != null)
//                {
//                    statementContainer.close();
//                }
//            }
//            catch (SQLException ex)
//            {
//                possiblyBadConnection = true;
//                LOGGER.error("Failed to close statement", ex);
//            }
//        }
//    }
//
//    public <T> boolean processDeleteRequest(T instance, String sql,
//            EntityDependentStatementInitializer<T> eInitializer) throws DaoException
//    {
//        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
//        boolean result;
//
//        try
//        {
//            statementContainer.wrap(connection.prepareStatement(sql));
//            eInitializer.init(instance, statementContainer);
//            result = statementContainer.executeUpdate() > 0;
//
//            return result;
//        }
//        catch (SQLException ex)
//        {
//            throw new DaoException("Failed to execute request", ex);
//        }
//        finally
//        {
//            try
//            {
//                if (statementContainer.getInnerStatement() != null)
//                {
//                    statementContainer.close();
//                }
//            }
//            catch (SQLException ex)
//            {
//                possiblyBadConnection = true;
//                LOGGER.error("Failed to close statement", ex);
//            }
//        }
//    }
//
//    public void processCustomRequest(String sql, PreparedStatementContainerInitializer... sdsInitializers) throws DaoException
//    {
//        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
//
//        try
//        {
//            statementContainer.wrap(connection.prepareStatement(sql));
//            for (PreparedStatementContainerInitializer initializer : sdsInitializers)
//            {
//                initializer.init(statementContainer);
//            }
//
//            statementContainer.execute();
//        }
//        catch (SQLException ex)
//        {
//            throw new DaoException("Failed to execute custom SQL request", ex);
//        }
//        finally
//        {
//            try
//            {
//                if (statementContainer.getInnerStatement() != null)
//                {
//                    statementContainer.close();
//                }
//            }
//            catch (SQLException ex)
//            {
//                possiblyBadConnection = true;
//                LOGGER.error("Failed to close statement", ex);
//            }
//        }
//    }
//
//    public boolean processBooleanResultRequest(String sql, PreparedStatementContainerInitializer initializer) throws DaoException
//    {
//        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
//
//        try
//        {
//            statementContainer.wrap(connection.prepareStatement(sql));
//            initializer.init(statementContainer);
//            ResultSet queryResult = statementContainer.executeQuery();
//            queryResult.next();
//            return queryResult.getBoolean(1);
//        }
//        catch (SQLException ex)
//        {
//            throw new DaoException("Failed to execute SQL request", ex);
//        }
//        finally
//        {
//            try
//            {
//                if (statementContainer.getInnerStatement() != null)
//                {
//                    statementContainer.close();
//                }
//            }
//            catch (SQLException ex)
//            {
//                possiblyBadConnection = true;
//                LOGGER.error("Failed to close statement", ex);
//            }
//        }
//    }
//
//    public List<List<Map<String, String>>> processCustomSelectRequest(
//            String sql,
//            PreparedStatementContainerInitializer sInitializer) throws DaoException
//    {
//
//        List<List<Map<String, String>>> globalResultData = new ArrayList<>();
//        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
//        try
//        {
//            statementContainer.wrap(connection.prepareStatement(sql));
//            if (sInitializer != null)
//            {
//                sInitializer.init(statementContainer);
//            }
//            LOGGER.debug("filled sql: " + statementContainer);
//            boolean isResultSet = statementContainer.execute();
//
//            // parsing result sets we got as a result of our request(s) execution
//            // each result set is represented by a separate list (currentResultSetData)
//            // each row from single result set is being held as Map<String, String>,
//            // where key is column label and value is cell content
//            while (true)
//            {
//                if (isResultSet)
//                {
//                    List<Map<String, String>> currentResultSetData = new ArrayList<>();
//                    try (ResultSet resultSet = statementContainer.getResultSet())
//                    {
//
//                        while (resultSet.next())
//                        {
//                            Map<String, String> rowData = new HashMap<>();
//                            ResultSetMetaData metaData = resultSet.getMetaData();
//                            for (int i = 1 ; i <= metaData.getColumnCount() ; i++)
//                            {
//                                String name = metaData.getColumnLabel(i);
//                                String value = resultSet.getString(i);
//                                rowData.put(name, value);
//                            }
//
//                            currentResultSetData.add(rowData);
//                        }
//                    }
//
//                    globalResultData.add(currentResultSetData);
//                }
//                else
//                {
//                    if (statementContainer.getUpdateCount() == -1)
//                    {
//                        break;
//                    }
//                }
//
//                isResultSet = statementContainer.getMoreResults();
//            }
//
//            LOGGER.debug("rows read: " + globalResultData.size());
//            return globalResultData;
//        }
//        catch (SQLException ex)
//        {
//            throw new DaoException("Failed to execute request", ex);
//        }
//        finally
//        {
//            try
//            {
//                if (statementContainer.getInnerStatement() != null)
//                {
//                    statementContainer.close();
//                }
//            }
//            catch (SQLException ex)
//            {
//                possiblyBadConnection = true;
//                LOGGER.error("Failed to close prepared statement", ex);
//            }
//        }
//    }
//    
//    private boolean tryToExchangePossiblyInvalidConnection()
//    {
//        if (--attemptsToExchangeConnection >= 0)
//        {
//            exchangeInvalidConnection();
//            return true;
//        }
//        else
//        {
//            returnInvalidConnection();
//            connectionIsAlreadyReturned = true;
//            return false;
//        }
//    }
//    
//    private void returnConnection()
//    {
//        connectionPool.returnConnection(connection);
//    }
//    
//    private void returnInvalidConnection()
//    {
//        connectionPool.returnInvalidConnection(connection);
//    }
//    
//    private void exchangeInvalidConnection()
//    {
//        connection = connectionPool.exchangeInvalidConnection(connection);
//    }
//}
