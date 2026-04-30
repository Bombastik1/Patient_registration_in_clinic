package sumdu.edu.ua.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import sumdu.edu.ua.manager.DatabaseManager;
import sumdu.edu.ua.model.Patient;

/**
 * Database class for creating, reading, updating and searching in PATIENT table.
 */
public class PatientDatabase {

    private final DatabaseManager databaseManager;

    /**
     * Creates database class with database manager dependency.
     *
     * @param databaseManager database manager
     */
    public PatientDatabase(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Returns all patients with city names.
     *
     * @return list of patients
     * @throws Exception if database operation fails
     */
    public List<Patient> findAll() throws Exception {
        String sql = """
            SELECT p.PatientId, p.Full_Name, p.Gender, p.Birth_Date, p.CityId, c.City_Name
            FROM PATIENT p
            JOIN CITY c ON p.CityId = c.CityId
            ORDER BY p.PatientId
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return mapPatients(resultSet);
        }
    }

    /**
     * Searches patients by full name fragment.
     *
     * @param name full name fragment
     * @return matched patients
     * @throws Exception if database operation fails
     */
    public List<Patient> searchByName(String name) throws Exception {
        String sql = """
            SELECT p.PatientId, p.Full_Name, p.Gender, p.Birth_Date, p.CityId, c.City_Name
            FROM PATIENT p
            JOIN CITY c ON p.CityId = c.CityId
            WHERE p.Full_Name LIKE ? 
            ORDER BY p.PatientId
            """;

        return searchBySingleTextParameter(sql, name);
    }

    /**
     * Searches patients by city name fragment.
     *
     * @param cityName city name fragment
     * @return matched patients
     * @throws Exception if database operation fails
     */
    public List<Patient> searchByCity(String cityName) throws Exception {
        String sql = """
            SELECT p.PatientId, p.Full_Name, p.Gender, p.Birth_Date, p.CityId, c.City_Name
            FROM PATIENT p
            JOIN CITY c ON p.CityId = c.CityId
            WHERE c.City_Name LIKE ? 
            ORDER BY p.PatientId
            """;

        return searchBySingleTextParameter(sql, cityName);
    }

    /**
     * Searches patients by gender.
     *
     * @param gender patient gender
     * @return matched patients
     * @throws Exception if database operation fails
     */
    public List<Patient> searchByGender(String gender) throws Exception {
        String sql = """
            SELECT p.PatientId, p.Full_Name, p.Gender, p.Birth_Date, p.CityId, c.City_Name
            FROM PATIENT p
            JOIN CITY c ON p.CityId = c.CityId
            WHERE p.Gender = ? 
            ORDER BY p.PatientId
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, gender);

            try (ResultSet resultSet = statement.executeQuery()) {
                return mapPatients(resultSet);
            }
        }
    }

    /**
     * Inserts a new patient.
     *
     * @param patient patient data without generated id
     * @return generated patient id
     * @throws Exception if database operation fails
     */
    public int create(Patient patient) throws Exception {
        String sql = """
                INSERT INTO PATIENT (Full_Name, Gender, Birth_Date, CityId)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillPatientStatement(statement, patient);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new SQLException("Patient id generation failed");
            }
        }
    }

    /**
     * Updates patient by identifier.
     *
     * @param patient patient data with existing id
     * @return true if patient was updated
     * @throws Exception if database operation fails
     */
    public boolean update(Patient patient) throws Exception {
        String sql = """
                UPDATE PATIENT
                SET Full_Name = ?, Gender = ?, Birth_Date = ?, CityId = ?
                WHERE PatientId = ?
                """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillPatientStatement(statement, patient);
            statement.setInt(5, patient.getPatientId());
            return statement.executeUpdate() > 0;
        }
    }

    private List<Patient> searchBySingleTextParameter(String sql, String value) throws Exception {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + value + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                return mapPatients(resultSet);
            }
        }
    }

    private void fillPatientStatement(PreparedStatement statement, Patient patient) throws SQLException {
        statement.setString(1, patient.getFullName());
        statement.setString(2, patient.getGender());
        statement.setDate(3, Date.valueOf(patient.getBirthDate()));
        statement.setInt(4, patient.getCityId());
    }

    private List<Patient> mapPatients(ResultSet resultSet) throws SQLException {
        List<Patient> patients = new ArrayList<>();

        while (resultSet.next()) {
            Patient patient = new Patient(
                    resultSet.getInt("PatientId"),
                    resultSet.getString("Full_Name"),
                    resultSet.getString("Gender"),
                    resultSet.getDate("Birth_Date").toLocalDate(),
                    resultSet.getInt("CityId"),
                    resultSet.getString("City_Name")
            );

            patients.add(patient);
        }

        return patients;
    }
}
