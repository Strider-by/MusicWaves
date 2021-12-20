package by.musicwaves.dao.impl;

import by.musicwaves.dao.*;
import by.musicwaves.dao.exception.DaoException;
import by.musicwaves.dao.requesthandler.SQLRequestHandler;
import by.musicwaves.dao.util.*;
import by.musicwaves.entity.Role;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Language;
import by.musicwaves.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UserDaoImpl implements UserDao {

    private static final UserDao instance = new UserDaoImpl();
    private final SQLRequestHandler requestHandler = SQLRequestHandler.getInstance();
    private static final Logger LOGGER = LogManager.getLogger(UserDaoImpl.class);

    private static final class SQL {
        public static final String SELECT_ALL = "SELECT * FROM users ";
        public static final String COUNT_ALL = "SELECT COUNT(*) AS quantity FROM users ";
        public static final String CREATE_INSTANCE
                = "SET @user_login := ?;"
                + "SELECT EXISTS(SELECT login FROM users WHERE login = @user_login) AS login_is_occupied;\n"
                + "INSERT IGNORE INTO users (login, password, language, role) "
                + "VALUES (@user_login, ?, ?, ?);\n"
                + "SELECT LAST_INSERT_ID() AS id;";
        public static final String UPDATE_INSTANCE
                = "UPDATE users SET login = ?, password = ?, language = ?, role = ?";
        public static final String UPDATE_USER_LOGIN
                = "SET @user_login := ?;"
                + "SELECT EXISTS(SELECT login FROM users WHERE login = @user_login) AS login_is_occupied;\n"
                + "UPDATE IGNORE users SET login = @user_login";
        public static final String UPDATE_USER_ROLE
                = "UPDATE users SET role = ?";
        public static final String DELETE_INSTANCE
                = "DELETE FROM users";
        public static final String CHECK_IF_LOGIN_IS_AVAILIBLE
                = "SELECT (SELECT COUNT(id) FROM users WHERE login = ?) = 0 AS login_is_available;";

        private static final class SelectBy {
            public static final String ID
                    = " WHERE id = ?";
            public static final String LOGIN
                    = " WHERE login = ?";
            public static final String LOGIN_AND_PASSWORD
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
        List<List<Map<String, String>>> requestResult = requestHandler.processCustomSelectRequest(
                SQL.UPDATE_USER_LOGIN + SQL.SelectBy.ID,
                statement -> {
                    statement.setNextString(login);
                    statement.setNextInt(userId);
                });

        boolean loginAlreadyInUse = requestResult.get(0).get(0).get("login_is_occupied").equals("1");
        return !loginAlreadyInUse;
    }

    public void updateUserRole(int userId, int roleId) throws DaoException {
        requestHandler.processUpdateRequest(
                SQL.UPDATE_USER_ROLE + SQL.SelectBy.ID,
                statement -> {
                    statement.setNextInt(roleId);
                    statement.setNextInt(userId);
                });
    }

    @Override
    public Pair<Integer, List<User>> findUsers(Integer id, String login, int loginSearchTypeId, Integer roleId, LocalDate registerDate, int registerDateCompareTypeId, int fieldIdToBeSortedBy, int sortOrderId, int pageNumber, int recordsPerPage) throws DaoException {
        SelectRequestBuilder requestBuilder = new SelectRequestBuilder();
        requestBuilder.setSearchById(id != null);
        requestBuilder.setSearchByLogin(login != null);
        requestBuilder.setSearchByRole(roleId != null);
        requestBuilder.setSearchByRegisterDate(registerDate != null);
        SimilarityType loginSearchSimilarityType = SimilarityType.getById(loginSearchTypeId);
        requestBuilder.setLoginSearchType(loginSearchSimilarityType);
        DateCompareType dateCompareType = DateCompareType.getById(registerDateCompareTypeId);
        requestBuilder.setRegisterDateCompareType(dateCompareType);

        Field fieldToBeSortedBy = Field.getById(fieldIdToBeSortedBy);
        if (fieldToBeSortedBy == Field.UNKNOWN_FIELD) {
            LOGGER.error("Failed to find proper database field using provided id. Sort by id will be used instead.");
            fieldToBeSortedBy = Field.ID;
        }
        requestBuilder.setSortBy(fieldToBeSortedBy);

        SortOrder sortOrder = SortOrder.getById(sortOrderId);
        requestBuilder.setSortOrder(sortOrder);

        String sql = requestBuilder.build();
        LOGGER.debug("built select SQL is: \n" + sql);

        List<List<Map<String, String>>> result = requestHandler.processCustomSelectRequest(
                sql,
                statement -> {
                    // we do it twice to init both counting adn getting-data parts of our request
                    this.initFindUsersStatement(statement, id, login, roleId, registerDate, loginSearchSimilarityType);
                    this.initFindUsersStatement(statement, id, login, roleId, registerDate, loginSearchSimilarityType);
                    // init limit and offset (required only for getting-data part of our request
                    this.calcAndInitLimitAndOffset(statement, pageNumber, recordsPerPage);
                });


        try {
            // get quantity of users found from the first part og the request
            int quantity = Integer.parseInt(result.get(0).get(0).get("quantity"));

            // get actual users from the second part of the request
            List<Map<String, String>> usersDataRows = result.get(1);
            List<User> users = new ArrayList<>();
            for(Map<String, String> dataRow : usersDataRows) {
                users.add(createUserFromDataRow(dataRow));
            }

            Pair<Integer, List<User>> response = new Pair<>(quantity, users);
            return response;

        } catch (IndexOutOfBoundsException | IllegalArgumentException | NullPointerException ex) {
            throw new DaoException("Failed to parse response from given SQL request", ex);
        }
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

    @Override
    public boolean checkIfLoginIsAvailable(String login) throws DaoException {
        return requestHandler.processBooleanResultRequest(
                SQL.CHECK_IF_LOGIN_IS_AVAILIBLE,
                statement -> statement.setNextString(login));
    }

    
    
    private void initUser(User user, ResultSet resultSet) throws SQLException {
        user.setId(resultSet.getInt("id"));
        user.setLogin(resultSet.getString("login"));
        user.setHashedPassword(resultSet.getString("password"));
        user.setLanguage(Language.getByDatabaseId(resultSet.getInt("language")));
        user.setRole(Role.getByDatabaseId(resultSet.getInt("role")));
        user.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
    }

    private User createUserFromDataRow(Map<String, String> dataRow) throws NullPointerException, IllegalArgumentException {
        User user = new User();
        user.setId(Integer.parseInt(dataRow.get("id")));
        user.setLogin(dataRow.get("login"));
        user.setHashedPassword(dataRow.get("password"));
        user.setLanguage(Language.getByDatabaseId(Integer.parseInt(dataRow.get("language"))));
        user.setRole(Role.getByDatabaseId(Integer.parseInt(dataRow.get("role"))));
        user.setCreated(Timestamp.valueOf(dataRow.get("created")).toLocalDateTime());
        return user;
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

    private void initFindUsersStatement(
            PreparedStatementContainer statement,
            Integer id, String login, Integer roleId, LocalDate registerDate,
            SimilarityType similarityType) throws SQLException {

        if (id != null) statement.setNextInt(id);
        if (login != null) {
            // the way we set login parameter depends on haw we are going to search, via '=', or via 'LIKE'
            login = similarityType == SimilarityType.EQUALS ? login : "%" + login + "%";
            statement.setNextString(login);
        }
        if (roleId != null) statement.setNextInt(roleId);
        if (registerDate != null) statement.setNextDate(Date.valueOf(registerDate));
    }

    private void initLimitAndOffset(PreparedStatementContainer statement, int limit, int offset) throws SQLException {
        statement.setNextInt(limit);
        statement.setNextInt(offset);
    }

    private void calcAndInitLimitAndOffset(PreparedStatementContainer statement, int pageNumber, int recordsPerPage) throws SQLException {
        int limit = recordsPerPage;
        int offset = (pageNumber - 1) * recordsPerPage;
        initLimitAndOffset(statement, limit, offset);
    }

    public class SelectRequestBuilder {

        StringBuilder dataGettingSql = new StringBuilder();
        StringBuilder resultsCountingSql = new StringBuilder();

        private boolean searchById;
        private boolean searchByLogin;
        private SimilarityType loginSearchType;
        private boolean searchByRole;
        private boolean searchByRegisterDate;
        private DateCompareType registerDateCompareType;

        private Field sortBy;
        private SortOrder sortOrder;

        public String build() {
            dataGettingSql.append(SQL.SELECT_ALL);
            resultsCountingSql.append(SQL.COUNT_ALL);

            boolean whereClauseInvolved = searchById || searchByLogin || searchByRole || searchByRegisterDate;
            if(whereClauseInvolved) {
                boolean andRequired = false;
                dataGettingSql.append(" WHERE ");
                resultsCountingSql.append(" WHERE ");
                if (searchById) {
                    dataGettingSql.append(Field.ID);
                    dataGettingSql.append(SimilarityType.EQUALS.getSql());
                    resultsCountingSql.append(Field.ID);
                    resultsCountingSql.append(SimilarityType.EQUALS.getSql());
                    andRequired = true;
                }
                if (searchByLogin) {
                    if(andRequired) {
                        dataGettingSql.append(" AND ");
                        resultsCountingSql.append(" AND ");
                    }
                    dataGettingSql.append(Field.LOGIN.getFieldName());
                    dataGettingSql.append(loginSearchType.getSql());
                    resultsCountingSql.append(Field.LOGIN.getFieldName());
                    resultsCountingSql.append(loginSearchType.getSql());
                    andRequired = true;
                }
                if (searchByRole) {
                    if (andRequired) {
                        dataGettingSql.append(" AND ");
                        resultsCountingSql.append(" AND ");
                    }
                    dataGettingSql.append(Field.ROLE.getFieldName());
                    dataGettingSql.append(SimilarityType.EQUALS.getSql());
                    resultsCountingSql.append(Field.ROLE.getFieldName());
                    resultsCountingSql.append(SimilarityType.EQUALS.getSql());
                    andRequired = true;
                }
                if (searchByRegisterDate) {
                    if (andRequired) {
                        dataGettingSql.append(" AND ");
                        resultsCountingSql.append(" AND ");
                    }
                    dataGettingSql.append("DATE(");
                    dataGettingSql.append(Field.REGISTER_DATE.getFieldName());
                    dataGettingSql.append(")");
                    dataGettingSql.append(registerDateCompareType.getSql());
                    resultsCountingSql.append("DATE(");
                    resultsCountingSql.append(Field.REGISTER_DATE.getFieldName());
                    resultsCountingSql.append(")");
                    resultsCountingSql.append(registerDateCompareType.getSql());
                }
            }
            // resultsCountingSql building ends here
            resultsCountingSql.append(";\n");

            dataGettingSql.append(" ORDER BY ");
            dataGettingSql.append(sortBy.getFieldName());

            dataGettingSql.append(sortOrder.getSqlEquivalent());
            dataGettingSql.append(" LIMIT ? OFFSET ?;");

            LOGGER.debug("Select users request parts: \n" + resultsCountingSql + "\n" + dataGettingSql);
            return resultsCountingSql.toString() + dataGettingSql.toString();
        }

        public void setSearchById(boolean searchById) {
            this.searchById = searchById;
        }

        public void setSearchByLogin(boolean searchByLogin) {
            this.searchByLogin = searchByLogin;
        }

        public void setLoginSearchType(SimilarityType loginSearchType) {
            this.loginSearchType = loginSearchType;
        }

        public void setSearchByRole(boolean searchByRole) {
            this.searchByRole = searchByRole;
        }

        public void setSearchByRegisterDate(boolean searchByRegisterDate) {
            this.searchByRegisterDate = searchByRegisterDate;
        }

        public void setRegisterDateCompareType(DateCompareType registerDateCompareType) {
            this.registerDateCompareType = registerDateCompareType;
        }

        public void setSortBy(Field sortBy) {
            this.sortBy = sortBy;
        }

        public void setSortOrder(SortOrder sortOrder) {
            this.sortOrder = sortOrder;
        }
    }

    public enum Field {

        UNKNOWN_FIELD(-1, null, null),
        ID(1,"id", "id"),
        LOGIN(2, "login", "login"),
        PASSWORD(3, "password", "password"),
        LANGUAGE(4, "language", "language"),
        ROLE(5, "role", "role"),
        REGISTER_DATE(6,"created", "register_date");

        private int id;
        private String fieldName;
        private String propertyKey;

        Field(int id, String fieldName, String propertyKey) {
            this.id = id;
            this.fieldName = fieldName;
            this.propertyKey = propertyKey;
        }

        public String getFieldName() {
            return fieldName;
        }

        public int getId() {
            return id;
        }

        public String getPropertyKey() {
            return propertyKey;
        }

        public static Field getById(int id) {
            return Arrays.stream(values())
                    .filter(field -> field.id == id)
                    .findAny()
                    .orElse(UNKNOWN_FIELD);
        }
    }
    
}