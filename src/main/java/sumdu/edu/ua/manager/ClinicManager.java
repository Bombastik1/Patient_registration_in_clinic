package sumdu.edu.ua.manager;

import java.time.LocalDate;
import java.util.List;

import sumdu.edu.ua.database.CityDatabase;
import sumdu.edu.ua.database.DiagnosisDatabase;
import sumdu.edu.ua.database.PatientDatabase;
import sumdu.edu.ua.database.VisitDatabase;
import sumdu.edu.ua.model.City;
import sumdu.edu.ua.model.Diagnosis;
import sumdu.edu.ua.model.Patient;
import sumdu.edu.ua.model.VisitDetails;

/**
 * Manager class that provides clinic operations.
 */
public class ClinicManager {

    private final PatientDatabase patientDatabase;
    private final CityDatabase cityDatabase;
    private final DiagnosisDatabase diagnosisDatabase;
    private final VisitDatabase visitDatabase;

    /**
     * Creates clinic manager with database dependencies.
     *
     * @param patientDatabase patient database object
     * @param cityDatabase city database object
     * @param diagnosisDatabase diagnosis database object
     * @param visitDatabase visit database object
     */
    public ClinicManager(PatientDatabase patientDatabase, CityDatabase cityDatabase, DiagnosisDatabase diagnosisDatabase, VisitDatabase visitDatabase) {
        this.patientDatabase = patientDatabase;
        this.cityDatabase = cityDatabase;
        this.diagnosisDatabase = diagnosisDatabase;
        this.visitDatabase = visitDatabase;
    }

    /**
     * Returns all patients.
     *
     * @return list of patients
     * @throws Exception if database operation fails
     */
    public List<Patient> getAllPatients() throws Exception {
        return patientDatabase.findAll();
    }

    /**
     * Searches patients by name.
     *
     * @param name name
     * @return matched patients
     * @throws Exception if database operation fails
     */
    public List<Patient> searchPatientsByName(String name) throws Exception {
        return patientDatabase.searchByName(name);
    }

    /**
     * Searches patients by city name.
     *
     * @param cityName city name
     * @return matched patients
     * @throws Exception if database operation fails
     */
    public List<Patient> searchPatientsByCity(String cityName) throws Exception {
        return patientDatabase.searchByCity(cityName);
    }

    /**
     * Searches patients by gender.
     *
     * @param gender gender
     * @return matched patients
     * @throws Exception if database operation fails
     */
    public List<Patient> searchPatientsByGender(String gender) throws Exception {
        return patientDatabase.searchByGender(gender);
    }

    /**
     * Creates patient data.
     *
     * @param patient patient data
     * @return generated patient id
     * @throws Exception if database operation fails
     */
    public int createPatient(Patient patient) throws Exception {
        return patientDatabase.create(patient);
    }

    /**
     * Updates patient data.
     *
     * @param patient patient data with existing id
     * @return true if patient was updated
     * @throws Exception if database operation fails
     */
    public boolean updatePatient(Patient patient) throws Exception {
        return patientDatabase.update(patient);
    }

    /**
     * Deletes patient by id.
     *
     * @param patientId patient identifier
     * @return true if patient was deleted
     * @throws Exception if database operation fails
     */
    public boolean deletePatient(int patientId) throws Exception {
        return patientDatabase.deleteById(patientId);
    }

    /**
     * Returns all cities.
     *
     * @return city list
     * @throws Exception if database operation fails
     */
    public List<City> getAllCities() throws Exception {
        return cityDatabase.findAll();
    }

    /**
     * Creates city record.
     *
     * @param cityName city name
     * @return generated city id
     * @throws Exception if database operation fails
     */
    public int createCity(String cityName) throws Exception {
        return cityDatabase.create(cityName);
    }

    /**
     * Returns all diagnoses.
     *
     * @return diagnosis list
     * @throws Exception if database operation fails
     */
    public List<Diagnosis> getAllDiagnoses() throws Exception {
        return diagnosisDatabase.findAll();
    }

    /**
     * Creates diagnosis data.
     *
     * @param diagnosisName diagnosis name
     * @return generated diagnosis id
     * @throws Exception if database operation fails
     */
    public int createDiagnosis(String diagnosisName) throws Exception {
        return diagnosisDatabase.create(diagnosisName);
    }

    /**
     * Returns all visits with patient and diagnosis.
     *
     * @return visit details list
     * @throws Exception if database operation fails
     */
    public List<VisitDetails> getAllVisits() throws Exception {
        return visitDatabase.findAllDetails();
    }

    /**
     * Creates visit data.
     *
     * @param patientId related patient identifier
     * @param diagnosisId related diagnosis identifier
     * @param visitDate visit date
     * @param clinicalNotes clinical notes
     * @return generated visit id
     * @throws Exception if database operation fails
     */
    public int createVisit(int patientId, int diagnosisId, LocalDate visitDate, String clinicalNotes) throws Exception {
        return visitDatabase.create(patientId, diagnosisId, visitDate, clinicalNotes);
    }

    /**
     * Deletes visit by id.
     *
     * @param visitId visit identifier
     * @return true if visit was deleted
     * @throws Exception if database operation fails
     */
    public boolean deleteVisit(int visitId) throws Exception {
        return visitDatabase.deleteById(visitId);
    }

}
