package by.musicwaves.dao.util;

import java.sql.SQLException;

public interface PreparedStatementContainerInitializer 
{
    void init(PreparedStatementContainer statement) throws SQLException;
}
