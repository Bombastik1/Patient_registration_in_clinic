package sumdu.edu.ua.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Manages MySQL connection settings and creates JDBC connections for database classes.
 */
public class DatabaseManager {

    private String url;
    private String user;
    private String password;

    /**
     * Creates database manager and loads connection settings from properties file.
     *
     * @param propertiesPath path to db.properties
     */
    public DatabaseManager(String propertiesPath) {
        loadProperties(propertiesPath);
    }

    private void loadProperties(String propertiesPath) {
        if (propertiesPath == null || propertiesPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Path to properties file is empty");
        }

        Properties properties = new Properties();
        try {
            FileInputStream input = new FileInputStream(propertiesPath);
            properties.load(input);
            input.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read properties file: " + e.getMessage());
        }

        this.url = properties.getProperty("db.url");
        this.user = properties.getProperty("db.user");
        this.password = properties.getProperty("db.password");
    }

    /**
     * Opens new MySQL connection.
     *
     * @return active JDBC connection
     * @throws java.sql.SQLException if database connection fails
     */
    public Connection getConnection() throws Exception {
        return DriverManager.getConnection(url, user, password);
    }
}
