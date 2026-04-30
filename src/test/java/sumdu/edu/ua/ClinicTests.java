package sumdu.edu.ua;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import sumdu.edu.ua.model.City;
import sumdu.edu.ua.model.Diagnosis;
import sumdu.edu.ua.model.Patient;
import sumdu.edu.ua.model.Visit;
import sumdu.edu.ua.model.VisitDetails;

class ClinicTests {

    @Test
    void shouldCreateCity() {
        City city = new City(1, "Sumy");

        assertEquals(1, city.getCityId());
        assertEquals("Sumy", city.getCityName());
        assertEquals("1. Sumy", city.toString());
    }

    @Test
    void shouldCreateDiagnosis() {
        Diagnosis diagnosis = new Diagnosis(1, "Flu");

        assertEquals(1, diagnosis.getDiagnosisId());
        assertEquals("Flu", diagnosis.getDiagnosisName());
        assertEquals("1. Flu", diagnosis.toString());
    }

    @Test
    void shouldCreatePatient() {
        LocalDate birthDate = LocalDate.of(2000, 1, 15);
        Patient patient = new Patient(1, " Vin Diesel ", "Male", birthDate, 2, "Kyiv");

        assertEquals(1, patient.getPatientId());
        assertEquals("Vin Diesel", patient.getFullName());
        assertEquals("Male", patient.getGender());
        assertEquals(birthDate, patient.getBirthDate());
        assertEquals(2, patient.getCityId());
        assertEquals("Kyiv", patient.getCityName());
        assertEquals("1. Vin Diesel, Male, 2000-01-15, Kyiv", patient.toString());
    }

    @Test
    void shouldRejectInvalidPatientData() {
        assertThrows(IllegalArgumentException.class, () -> new Patient(1, " ", "Male", LocalDate.of(2000, 1, 15), 1, "Sumy"));
        assertThrows(IllegalArgumentException.class, () -> new Patient(1, "Vin Diesel", " ", LocalDate.of(2000, 1, 15), 1, "Sumy"));
        assertThrows(IllegalArgumentException.class, () -> new Patient(1, "Vin Diesel", "Male", null, 1, "Sumy"));
        assertThrows(IllegalArgumentException.class, () -> new Patient(1, "Vin Diesel", "Male", LocalDate.of(2000, 1, 15), 0, "Sumy"));
    }

    @Test
    void shouldCreateVisit() {
        LocalDate visitDate = LocalDate.of(2026, 4, 30);
        Visit visit = new Visit(1, 2, 3, visitDate, "Regular check");

        assertEquals(1, visit.getVisitId());
        assertEquals(2, visit.getPatientId());
        assertEquals(3, visit.getDiagnosisId());
        assertEquals(visitDate, visit.getVisitDate());
        assertEquals("Regular check", visit.getClinicalNotes());
    }

    @Test
    void shouldCreateVisitDetails() {
        LocalDate visitDate = LocalDate.of(2026, 4, 30);
        VisitDetails visit = new VisitDetails(1, "Vin Diesel", "Flu", visitDate, "Regular check");

        assertEquals(1, visit.getVisitId());
        assertEquals("Vin Diesel", visit.getPatientName());
        assertEquals("Flu", visit.getDiagnosisName());
        assertEquals(visitDate, visit.getVisitDate());
        assertEquals("Regular check", visit.getClinicalNotes());
        assertEquals("1. 2026-04-30, Vin Diesel, Flu, Regular check", visit.toString());
    }
}
