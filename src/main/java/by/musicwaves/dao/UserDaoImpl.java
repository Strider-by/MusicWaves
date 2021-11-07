package by.musicwaves.dao;

import by.musicwaves.dao.requesthandler.SQLRequestHandler;
import by.musicwaves.dao.util.PreparedStatementContainer;
import by.musicwaves.dao.util.PreparedStatementContainerInitializer;
import by.musicwaves.entity.Role;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Language;
import by.musicwaves.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UserDaoImpl implements UserDao {

    private static final UserDao instance = new UserDaoImpl();
    private final SQLRequestHandler requestHandler = SQLRequestHandler.getInstance();
    private final static Logger LOGGER = LogManager.getLogger(UserDaoImpl.class);

    private static final class SQL {
        public final static String SELECT_ALL = "SELECT * FROM users ";
        public final static String CREATE_INSTANCE
                = "SET @user_login := ?;"
                + "SELECT EXISTS(SELECT login FROM users WHERE login = @user_login) AS login_is_occupied;\n"
                + "INSERT IGNORE INTO users (login, password, language, role) "
                + "VALUES (@user_login, ?, ?, ?);\n"
                + "SELECT LAST_INSERT_ID() AS id;";
        public final static String UPDATE_INSTANCE
                = "UPDATE users SET login = ?, password = ?, language = ?, role = ?";
        public final static String UPDATE_USER_LOGIN
                = "SET @user_login := ?;"
                + "SELECT EXISTS(SELECT login FROM users WHERE login = @user_login) AS login_is_occupied;\n"
                + "UPDATE IGNORE users SET login = @user_login";
        public final static String DELETE_INSTANCE
                = "DELETE FROM users";

        private static final class SelectBy {
            public final static String ID
                    = " WHERE id = ?";
            public final static String LOGIN
                    = " WHERE login = ?";
            public final static String LOGIN_AND_PASSWORD
                    = " WHERE login = ? AND password = ?";
        }
    }

    private UserDaoImpl() {
        // single instance allowed
    }

    public static UserDao getInstance() {
        return instance;
    }

    @Override
    public List<User> getAll() throws DaoException {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL.SELECT_ALL,
                null,
                User::new,
                this::initUser);
    }

    @Override
    public User findById(int id) throws DaoException {
        return requestHandler.processSingleResultSelectRequest(
                SQL.SELECT_ALL + SQL.SelectBy.ID,
                statement -> statement.setNextInt(id),
                User::new,
                this::initUser);
    }
    
    @Override
    public User findByLogin(String login) throws DaoException {
        return requestHandler.processSingleResultSelectRequest(
                SQL.SELECT_ALL + SQL.SelectBy.LOGIN,
                (statement) -> statement.setNextString(login),
                User::new,
                this::initUser);
    }
    
    @Override
    public User findByLoginAndPassword(String login, String password) throws DaoException {
        return requestHandler.processSingleResultSelectRequest(
                SQL.SELECT_ALL + SQL.SelectBy.LOGIN_AND_PASSWORD,
                (statement) -> {
                    statement.setNextString(login);
                    statement.setNextString(password);
                },
                User::new,
                this::initUser);
    }

    @Override
    public Integer create(User instance) throws DaoException {
        LOGGER.debug("User instance: \n" + instance);
        List<List<Map<String, String>>> requestResult = requestHandler.processCustomSelectRequest(
                SQL.CREATE_INSTANCE,
                initCreateStatement(instance));

        boolean loginAlreadyInUse = requestResult.get(0).get(0).get("login_is_occupied").equals("1");
        int id = loginAlreadyInUse ? -1 : Integer.parseInt(requestResult.get(1).get(0).get("id"));
        return id;
    }

    @Override
    public void update(User instance) throws DaoException {
        requestHandler.processUpdateRequest(
                instance,
                SQL.UPDATE_INSTANCE + SQL.SelectBy.ID,
                this::initUpdateStatement,
                statement -> statement.setNextInt(instance.getId()));
    }

    public boolean updateUserLogin(String login, int userId) throws DaoException {
        LOGGER.debug("User instance: \n" + instance);
        List<List<Map<String, String>>> requestResult = requestHandler.processCustomSelectRequest(
                SQL.UPDATE_USER_LOGIN + SQL.SelectBy.ID,
                statement -> {
                    statement.setNextString(login);
                    statement.setNextInt(userId);
                });

        boolean loginAlreadyInUse = requestResult.get(0).get(0).get("login_is_occupied").equals("1");
        return !loginAlreadyInUse;
    }

    @Override
    public boolean deleteById(int id) throws DaoException {
        return 1 == requestHandler.processDeleteByIdRequest(id, SQL.DELETE_INSTANCE + SQL.SelectBy.ID);
    }

    @Override
    public boolean delete(User instance) throws DaoException {
        return 1 == requestHandler.processDeleteRequest(
                instance,
                SQL.DELETE_INSTANCE + SQL.SelectBy.ID,
                (user, statement) -> statement.setNextInt(user.getId()));
    }

    
    
    private void initUser(User user, ResultSet resultSet) throws SQLException {
        user.setId(resultSet.getInt("id"));
        user.setLogin(resultSet.getString("login"));
        user.setHashedPassword(resultSet.getString("password"));
        user.setLanguage(Language.getByDatabaseId(resultSet.getInt("language")));
        user.setRole(Role.getByDatabaseId(resultSet.getInt("role")));
        user.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
    }

    private void initUpdateStatement(User user, PreparedStatementContainer statement) throws SQLException {
        statement.setNextString(user.getLogin());
        statement.setNextString(user.getHashedPassword());
        statement.setNextInt(user.getLanguage().getDatabaseId());
        statement.setNextInt(user.getRole().getDatabaseId());
    }

    private PreparedStatementContainerInitializer initCreateStatement(User user) {
        return (statement -> {
            statement.setNextString(user.getLogin());
            statement.setNextString(user.getHashedPassword());
            statement.setNextInt(user.getLanguage().getDatabaseId());
            statement.setNextInt(user.getRole().getDatabaseId());
        });
    }

    
    
}