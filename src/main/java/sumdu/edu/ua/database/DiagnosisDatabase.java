package sumdu.edu.ua.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import sumdu.edu.ua.DatabaseManager;
import sumdu.edu.ua.model.Diagnosis;

/**
 * Database class for the DIAGNOSIS table.
 */
public class DiagnosisDatabase {

    private final DatabaseManager databaseManager;

    /**
     * Creates database class with database manager dependency.
     *
     * @param databaseManager database manager
     */
    public DiagnosisDatabase(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Returns all diagnoses ordered by name.
     *
     * @return list of diagnoses
     * @throws Exception if database operation fails
     */
    public List<Diagnosis> findAll() throws Exception {
        String sql = "SELECT DiagnosisId, Diagnosis_Name FROM DIAGNOSIS ORDER BY Diagnosis_Name";

        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            List<Diagnosis> diagnoses = new ArrayList<>();

            while (resultSet.next()) {
                diagnoses.add(new Diagnosis(
                        resultSet.getInt("DiagnosisId"),
                        resultSet.getString("Diagnosis_Name")
                ));
            }

            return diagnoses;
        }
    }

    /**
     * Inserts a new diagnosis.
     *
     * @param diagnosisName diagnosis name
     * @return generated diagnosis id
     * @throws Exception if database operation fails
     */
    public int create(String diagnosisName) throws Exception {
        String sql = "INSERT INTO DIAGNOSIS (Diagnosis_Name) VALUES (?)";

        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, diagnosisName);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new IllegalStateException("Diagnosis id generation failed");
            }
        }
    }
}
