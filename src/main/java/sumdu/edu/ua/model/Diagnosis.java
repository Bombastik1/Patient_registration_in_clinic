package sumdu.edu.ua.model;

/**
 * Represents medical diagnosis that can be assigned during visit.
 */
public class Diagnosis {

    private final int diagnosisId;
    private final String diagnosisName;

    /**
     * Creates diagnosis data object.
     *
     * @param diagnosisId diagnosis identifier
     * @param diagnosisName diagnosis name
     */
    public Diagnosis(int diagnosisId, String diagnosisName) {
        this.diagnosisId = diagnosisId;
        this.diagnosisName = diagnosisName;
    }

    public int getDiagnosisId() {
        return diagnosisId;
    }

    public String getDiagnosisName() {
        return diagnosisName;
    }

    @Override
    public String toString() {
        return diagnosisId + ". " + diagnosisName;
    }
}
