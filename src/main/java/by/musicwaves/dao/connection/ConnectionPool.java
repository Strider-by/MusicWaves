package by.musicwaves.dao.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public enum ConnectionPool {

    INSTANCE;

    private final int initialSize = 50;
    private final Logger logger = LogManager.getLogger(ConnectionPool.class);
    private final Collection<Connection> allConnections; // is used for closing connections
    private final LinkedBlockingQueue<Connection> freeConnections;
    private final Collection<Connection> connectionsInUse;
    private final ConnectionWorker connectionWorker;
    private final AtomicBoolean connectionsAreBeingClosed = new AtomicBoolean(false);
    private final ReadWriteLock poolChangeLock = new ReentrantReadWriteLock(true);

    ConnectionPool() {

        freeConnections = new LinkedBlockingQueue<>();
        connectionsInUse = Collections.synchronizedCollection(new HashSet<>());
        allConnections = Collections.synchronizedCollection(new HashSet<>());

        try {
            connectionWorker = new ConnectionWorker();
            fillPool();

            logger.info("Connection pool initialization has finished");
        } catch (SQLException ex) {
            logger.error("Fatal error during connection pool creation", ex);
            throw new RuntimeException("Failed to fill connection pool", ex);
        }
    }

    /**
     * Fills pool with connections up to maximal pool size
     *
     * @throws SQLException if {@link ConnectionWorker} fails to create connection
     */
    private void fillPool() throws SQLException {
        for (int i = 0; i < initialSize; i++) {
            Connection connection = connectionWorker.openConnection();
            freeConnections.add(connection);
            allConnections.add(connection);
        }
    }

    /**
     * Gives connection to work with if any is available.
     * If not - calling this method Thread shall wait until free connection appear.
     */
    public Connection getConnection() {
        try {
            Connection connection = freeConnections.take();
            connectionsInUse.add(connection);

            return connection;
        } catch (InterruptedException ex) {
            logger.error("Fatal error during attempt to get connection from pool", ex);
            throw new RuntimeException("Failed to get connection from pool", ex);
        }
    }

    /**
     * Accepts connection after it has been used and is not longer needed to place it back to pool as a free one.
     * Only connections got from this pool shall be accepted.
     */
    public void returnConnection(Connection connection) {
        if (connectionsInUse.remove(connection)) {
            freeConnections.add(connection);
        } else {
            logger.warn("Unknown connection is being tried to return as a used one");
        }
    }

    /**
     * Accepts connection after it has been used and found to be invalid (operation with it has failed).
     * Only connections got from this pool shall be accepted.
     * Connection shall be removed from the pool and new connection shall be added.
     */
    public void returnInvalidConnection(Connection connection) {
        if (connectionsInUse.remove(connection)) {
            dispose(connection);
            addNewConnection();
        } else {
            logger.warn("Unknown connection is being tried to return as an invalid used one");
        }
    }

    /**
     * Accepts connection after it has been used and found to be invalid (operation with it has failed).
     * Only connections got from this pool shall be accepted.
     * Connection shall be checked and if it is all right - returned to pool. Otherwise - it shall be replaced with
     * a new one.
     */
    public void returnInvalidConnectionWithCheck(Connection connection) {
        if (connectionsInUse.remove(connection)) {
            runPossiblyBadConnectionCheck(connection);
        } else {
            logger.warn("Unknown connection is being tried to return with check as an invalid used one");
        }
    }

    /**
     * Accepts connection after it has been used and found to be invalid (operation with it has failed) and return a new
     * one in exchange.
     * Only connections got from this pool shall be accepted.
     * Connection shall be removed from the pool and new connection shall be added.
     *
     * @return java.sql.Connection from this pool if any is available. If not - calling this method Thread
     * shall wait until free connection appear.
     */
    public Connection exchangeInvalidConnection(Connection connection) {
        logger.info("Exchanging connection that is beeng returned as a bad one...");

        if (connectionsInUse.remove(connection)) {
            dispose(connection);
        } else {
            logger.warn("Unknown connection is being tried to exchange as an invalid");
        }

        return getConnection();
    }

    /**
     * Accepts connection after it has been used and found to be invalid (operation with it has failed) and return a new
     * one in exchange.
     * Only connections got from this pool shall be accepted.
     * Connection shall be checked and if it is all right - returned to pool. Otherwise - it shall be replaced with
     * a new one.
     *
     * @return java.sql.Connection from this pool if any is available. If not - calling this method Thread
     * shall wait until free connection appear.
     */
    public Connection exchangeInvalidConnectionWithCheck(Connection connection) {
        logger.info("Exchanging with check connection that is beeng returned as a bad one...");

        if (connectionsInUse.remove(connection)) {
            runPossiblyBadConnectionCheck(connection);
        } else {
            logger.warn("Unknown connection is being tried to exchange with check as an invalid");
        }

        return getConnection();
    }

    /**
     * Closes all connections in this pool and empty it.
     */
    public void closeAllConnections() {
        logger.info("Closing all connections");
        // since we will iterate through our allConnections collection
        // we'd better be sure that no one can add or delete element
        // during this action and thus broke our iteration
        Lock writeLock = poolChangeLock.writeLock();

        try {
            writeLock.lock();
            connectionsAreBeingClosed.set(true);
            freeConnections.clear();
            connectionsInUse.clear();

            for (Connection connection : allConnections) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    logger.info("Failed to close connection during #closeAllConnections method call", ex);
                }
            }

            allConnections.clear();
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Runs check of the connection that was found to be invalid. Connection is being checked in a separate Thread.
     *
     * @param connection - connection to be checked
     */
    private void runPossiblyBadConnectionCheck(Connection connection) {
        ConnectionChecker checker = new ConnectionChecker(connection);
        new Thread(checker).start();
    }

    /**
     * Adds new connection to the pool. Connection creation is being executed in a separate Thread.
     */
    private void addNewConnection() {
        ConnectionCreator connectionCreator = new ConnectionCreator();
        new Thread(connectionCreator).start();
    }


    // todo separate thread action?

    /**
     * Removes connection from this pool.
     *
     * @param connection - connection to be removed
     * @return if connection has been removed or not. Connection IS NOT removed and therefore false to be returned if
     * the connection provided as the parameter does not belong to this pool.
     */
    private boolean dispose(Connection connection) {
        Lock readLock = poolChangeLock.readLock();

        try {
            readLock.lock();

            try {
                connection.close();
            } catch (SQLException ex) {
                logger.info("Failed to close connection that is being disposed", ex);
            }

            return allConnections.remove(connection);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * This class is intended to create new connection on request and than place it in the connection pool.
     * Can be used to replace an invalid connection with a new one.
     */
    private class ConnectionCreator implements Runnable {

        @Override
        public void run() {
            Connection connection;

            try {
                //todo is it thread safe? M.b. I need to use Lock here?
                connection = connectionWorker.openConnection();
            } catch (SQLException ex) {
                logger.error("Error during creating new connection", ex);
                throw new RuntimeException("Failed to create new connection", ex);
            }

            Lock readLock = poolChangeLock.readLock();
            try {
                readLock.lock();
                if (!connectionsAreBeingClosed.get()) {
                    allConnections.add(connection);
                    freeConnections.add(connection);
                } else {
                    logger.info("Closing newly opened connection that is not needed any longer...");
                    try {
                        connection.close();
                    } catch (SQLException ex) {
                        logger.error("Error occurred during closing newly opened but no longer needed connection", ex);
                    }
                }
            } finally {
                readLock.unlock();
            }
        }

    }


    /**
     * This class is intended to check if connection is valid or not. If it is - it will be placed back to the pool.
     * If not - creation of a new connection shall be launched.
     */
    private class ConnectionChecker implements Runnable {
        private final Connection connection;
        // todo change this value?
        int secondsToWait = 0;

        public ConnectionChecker(Connection connection) {
            this.connection = connection;
        }


        @Override
        public void run() {
            boolean replacementRequired;

            try {
                replacementRequired = !connection.isValid(secondsToWait);
            } catch (SQLException ex) {
                replacementRequired = true;
            }

            Lock readLock = poolChangeLock.readLock();
            try {
                readLock.lock();
                if (!connectionsAreBeingClosed.get()) {
                    if (replacementRequired) {
                        addNewConnection();
                    } else {
                        freeConnections.add(connection);
                    }

                    allConnections.remove(connection);
                }
            } finally {
                readLock.unlock();
            }
        }
    }

}
