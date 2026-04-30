package sumdu.edu.ua.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import sumdu.edu.ua.DatabaseManager;
import sumdu.edu.ua.model.VisitDetails;

/**
 * Database class for visit data.
 */
public class VisitDatabase {

    private final DatabaseManager databaseManager;

    /**
     * Creates database class with database manager dependency.
     *
     * @param databaseManager database manager
     */
    public VisitDatabase(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Returns all visits with patient and diagnosis names.
     *
     * @return list of visit details
     * @throws Exception if database operation fails
     */
    public List<VisitDetails> findAllDetails() throws Exception {
        String sql = """
                SELECT v.VisitId, p.Full_Name, d.Diagnosis_Name, v.Visit_Date, v.Clinical_Notes
                FROM VISIT v
                JOIN PATIENT p ON v.PatientId = p.PatientId
                JOIN DIAGNOSIS d ON v.DiagnosisId = d.DiagnosisId
                ORDER BY v.Visit_Date DESC, v.VisitId DESC
                """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return mapVisitDetails(resultSet);
        }
    }

    /**
     * Inserts new visit.
     *
     * @param patientId related patient identifier
     * @param diagnosisId related diagnosis identifier
     * @param visitDate visit date
     * @param clinicalNotes clinical notes
     * @return generated visit id
     * @throws Exception if database operation fails
     */
    public int create(int patientId, int diagnosisId, LocalDate visitDate, String clinicalNotes) throws Exception {
        String sql = """
                INSERT INTO VISIT (PatientId, DiagnosisId, Visit_Date, Clinical_Notes)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, patientId);
            statement.setInt(2, diagnosisId);
            statement.setDate(3, Date.valueOf(visitDate));
            statement.setString(4, clinicalNotes);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new SQLException("Visit id generation failed");
            }
        }
    }

    private List<VisitDetails> mapVisitDetails(ResultSet resultSet) throws SQLException {
        List<VisitDetails> visits = new ArrayList<>();

        while (resultSet.next()) {
            VisitDetails visit = new VisitDetails(
                    resultSet.getInt("VisitId"),
                    resultSet.getString("Full_Name"),
                    resultSet.getString("Diagnosis_Name"),
                    resultSet.getDate("Visit_Date").toLocalDate(),
                    resultSet.getString("Clinical_Notes")
            );

            visits.add(visit);
        }

        return visits;
    }
}
