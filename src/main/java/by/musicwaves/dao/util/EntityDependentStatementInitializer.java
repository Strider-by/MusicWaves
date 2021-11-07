package by.musicwaves.dao.util;

import java.sql.SQLException;

public interface EntityDependentStatementInitializer<T> 
{
    void init(T instance, PreparedStatementContainer statement) throws SQLException;
}
