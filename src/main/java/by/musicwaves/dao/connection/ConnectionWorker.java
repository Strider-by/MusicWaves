package by.musicwaves.dao.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;

public class ConnectionWorker 
{
    private final ResourceBundle resourceBundle;
    private final String url;
    private final Properties properties;
    
    protected ConnectionWorker() throws SQLException
    {
        resourceBundle = ResourceBundle.getBundle("connection");
        url = resourceBundle.getString("url");
        properties = new Properties();
        
        for (String key : resourceBundle.keySet())
        {
            properties.put(key, resourceBundle.getString(key));
        }
        properties.remove("url");
        
        DriverManager.registerDriver(new org.mariadb.jdbc.Driver());
    }
    
    protected Connection openConnection() throws SQLException
    {
        Connection connection = DriverManager.getConnection(url, properties);
        return connection;
    }

}
