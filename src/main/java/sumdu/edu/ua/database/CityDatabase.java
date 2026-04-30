package sumdu.edu.ua.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import sumdu.edu.ua.manager.DatabaseManager;
import sumdu.edu.ua.model.City;

/**
 * Database class for the CITY table.
 */
public class CityDatabase {

    private final DatabaseManager databaseManager;

    /**
     * Creates database class with database manager dependency.
     *
     * @param databaseManager database manager
     */
    public CityDatabase(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Returns all cities.
     *
     * @return list of cities
     * @throws Exception if database operation fails
     */
    public List<City> findAll() throws Exception {
        String sql = "SELECT CityId, City_Name FROM CITY ORDER BY City_Name";

        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            List<City> cities = new ArrayList<>();

            while (resultSet.next()) {
                cities.add(new City(
                        resultSet.getInt("CityId"),
                        resultSet.getString("City_Name")
                ));
            }

            return cities;
        }
    }

    /**
     * Inserts new city.
     *
     * @param cityName city name
     * @return generated city id
     * @throws Exception if database operation fails
     */
    public int create(String cityName) throws Exception {
        String sql = "INSERT INTO CITY (City_Name) VALUES (?)";

        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, cityName);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new IllegalStateException("City id generation failed");
            }
        }
    }
}
