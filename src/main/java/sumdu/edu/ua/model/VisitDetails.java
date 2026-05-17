package sumdu.edu.ua.model;

import java.time.LocalDate;

/**
 * Represents visit information prepared for displaying.
 */
public class VisitDetails {

    private final int visitId;
    private final String patientName;
    private final String diagnosisName;
    private final LocalDate visitDate;
    private final String clinicalNotes;

    /**
     * Creates visit view object.
     *
     * @param visitId visit identifier
     * @param patientName patient full name
     * @param diagnosisName diagnosis name
     * @param visitDate visit date
     * @param clinicalNotes clinical notes
     */
    public VisitDetails(int visitId, String patientName, String diagnosisName, LocalDate visitDate, String clinicalNotes) {
        this.visitId = visitId;
        this.patientName = patientName;
        this.diagnosisName = diagnosisName;
        this.visitDate = visitDate;
        this.clinicalNotes = clinicalNotes;
    }

    public int getVisitId() {
        return visitId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDiagnosisName() {
        return diagnosisName;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public String getClinicalNotes() {
        return clinicalNotes;
    }

    @Override
    public String toString() {
        return visitId + ". " + visitDate + ", " + patientName + ", " + diagnosisName + ", " + clinicalNotes;
    }
}
