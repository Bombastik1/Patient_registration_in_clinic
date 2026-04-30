package sumdu.edu.ua.model;

import java.time.LocalDate;

/**
 * Represents patient visit with assigned diagnosis.
 */
public class Visit {

    private final int visitId;
    private final int patientId;
    private final int diagnosisId;
    private final LocalDate visitDate;
    private final String clinicalNotes;

    /**
     * Creates visit data object.
     *
     * @param visitId visit identifier
     * @param patientId related patient identifier
     * @param diagnosisId related diagnosis identifier
     * @param visitDate visit date
     * @param clinicalNotes clinical notes
     */
    public Visit(int visitId, int patientId, int diagnosisId, LocalDate visitDate, String clinicalNotes) {
        this.visitId = visitId;
        this.patientId = patientId;
        this.diagnosisId = diagnosisId;
        this.visitDate = visitDate;
        this.clinicalNotes = clinicalNotes;
    }

    public int getVisitId() {
        return visitId;
    }

    public int getPatientId() {
        return patientId;
    }

    public int getDiagnosisId() {
        return diagnosisId;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public String getClinicalNotes() {
        return clinicalNotes;
    }
}
