//package by.musicwaves.dao;
//
//import by.musicwaves.dao.requesthandler.SQLRequestHandler;
//import by.musicwaves.dao.util.PreparedStatementContainer;
//import by.musicwaves.entity.Role;
//import by.musicwaves.entity.User;
//import by.musicwaves.entity.ancillary.Language;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.function.Supplier;
//
//
//public class UserDao1 implements Dao<User>
//{
//    public final static String SQL_SELECT_ALL = "SELECT * FROM users ";
//    public final static String SQL_CREATE_INSTANCE
//            = "INSERT INTO users ("
//            + "email, password, language, conf_code, role, register_date) "
//            + "VALUES (?, ?, ?, ?, ?, ?)";
//    public final static String SQL_POSTFIX_SELECT_BY_ID
//            = " WHERE user_id = ?";
//    public final static String SQL_POSTFIX_SELECT_BY_EMAIL
//            = " WHERE email = ?";
//    
//    private final SQLRequestHandler requestHandler = new SQLRequestHandler();
//
//    @Override
//    public List<User> getAll() throws DaoException {
//        return requestHandler.processMultipleResultsSelectRequest(
//                SQL_SELECT_ALL,
//                User::new,
//                null,
//                this::initUser);
//    }
//
//    @Override
//    public User findById(int id) throws DaoException {
//        return requestHandler.processSingleResultSelectRequest(
//                SQL_SELECT_ALL + SQL_POSTFIX_SELECT_BY_ID, 
//                User::new, 
//                statement -> statement.setNextInt(id), 
//                this::initUser);
//    }
//    
//    public User findByEmail(String email) throws DaoException {
//        return requestHandler.processSingleResultSelectRequest(
//                SQL_SELECT_ALL + SQL_POSTFIX_SELECT_BY_EMAIL,
//                User::new,
//                (statement) -> statement.setNextString(email),
//                this::initUser);
//    }
//
//    @Override
//    public Integer create(User instance) throws DaoException {
//        return requestHandler.processCreateRequest(
//                SQL_CREATE_INSTANCE, 
//                instance, 
//                this::initCreationStatement);
//    }
//
//    @Override
//    public User update(User instance) throws DaoException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public boolean deleteById(int id) throws DaoException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public boolean delete(User instance) throws DaoException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public void close() {
//        requestHandler.close();
//    }
//    
//    
//    private void initUser(User user, ResultSet resultSet) throws SQLException {
//        user.setId(resultSet.getInt("user_id"));
//        user.setEmail(resultSet.getString("email"));
//        user.setHashedPassword(resultSet.getString("password"));
//        user.setLanguage(Language.getByDatabaseId(resultSet.getInt("language")));
//        user.setConfCode(resultSet.getString("conf_code"));
//        user.setRole(Role.getByDatabaseId(resultSet.getInt("role")));
//        user.setRegisterDate(LocalDate.parse(resultSet.getString("register_date")));
//    }
//
//    private void initCreationStatement(User user, PreparedStatementContainer statement) throws SQLException {
//        statement.setNextString(user.getEmail());
//        statement.setNextString(user.getHashedPassword());
//        statement.setNextInt(user.getLanguage().getDatabaseId());
//        statement.setNextString(user.getConfCode());
//        statement.setNextInt(user.getRole().getDatabaseId());
//        statement.setNextString(user.getRegisterDate().toString());
//    }
//    
//    private class UserCreator implements Supplier<User> {
//
//        @Override
//        public User get() {
//            return new User();
//        }
//        
//    }
//    
//    
//}
