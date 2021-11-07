package by.musicwaves.dao.connection;

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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public enum ConnectionPool {
    INSTANCE;

    private final int initialSize = 50;

    private Collection<Connection> allConnections; // is used for closing connections
    private LinkedBlockingQueue<Connection> freeConnections;
    private Collection<Connection> connectionsInUse;
    private ConnectionWorker connectionWorker;
    private AtomicBoolean connectionsAreBeingClosed = new AtomicBoolean(false);
    private ReadWriteLock poolChangeLock = new ReentrantReadWriteLock(true);

    private final Logger logger = LogManager.getLogger(ConnectionPool.class);

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

    private void fillPool() throws SQLException {
        for (int i = 0; i < initialSize; i++) {
            Connection connection = connectionWorker.openConnection();
            freeConnections.add(connection);
            allConnections.add(connection);
        }
    }

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

    public void returnConnection(Connection connection) {
        if (connectionsInUse.remove(connection)) {
            freeConnections.add(connection);
        } else {
            logger.warn("Unknown connection is being tried to return as a used one");
        }
    }

    public void returnInvalidConnection(Connection connection) {
        if (connectionsInUse.remove(connection)) {
            dispose(connection);
            addNewConnection();
        } else {
            logger.warn("Unknown connection is being tried to return as an invalid used one");
        }
    }

    public void returnInvalidConnectionWithCheck(Connection connection) {
        if (connectionsInUse.remove(connection)) {
            runPossiblyBadConnectionCheck(connection);
        } else {
            logger.warn("Unknown connection is being tried to return with check as an invalid used one");
        }
    }

    public Connection exchangeInvalidConnection(Connection connection) {
        logger.info("Exchanging connection that is beeng returned as a bad one...");

        if (connectionsInUse.remove(connection)) {
            dispose(connection);
        } else {
            logger.warn("Unknown connection is being tried to exchange as an invalid");
        }

        return getConnection();
    }

    public Connection exchangeInvalidConnectionWithCheck(Connection connection) {
        logger.info("Exchanging with check connection that is beeng returned as a bad one...");

        if (connectionsInUse.remove(connection)) {
            runPossiblyBadConnectionCheck(connection);
        } else {
            logger.warn("Unknown connection is being tried to exchange with check as an invalid");
        }

        return getConnection();
    }

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
                    logger.info("Failed to close connection during cloaseAllConnections method call", ex);
                }
            }

            allConnections.clear();
        } finally {
            writeLock.unlock();
        }
    }

    private void runPossiblyBadConnectionCheck(Connection connection) {
        ConnectionChecker checker = new ConnectionChecker(connection);
        new Thread(checker).start();
    }

    private void addNewConnection() {
        ConnectionCreator connectionCreator = new ConnectionCreator();
        new Thread(connectionCreator).start();
    }


    // todo separate thread action?
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

    private class ConnectionCreator implements Runnable {

        @Override
        public void run() {
            Connection connection = null;

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
                        logger.error("Error occured during closing newly opened but no longer needed connection", ex);
                    }
                }
            } finally {
                readLock.unlock();
            }
        }

    }


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
