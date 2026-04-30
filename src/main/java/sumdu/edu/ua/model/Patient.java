package sumdu.edu.ua.model;

import java.time.LocalDate;

/**
 * Represents patient registered in the clinic.
 */
public class Patient {

    private final int patientId;
    private String fullName;
    private String gender;
    private LocalDate birthDate;
    private int cityId;
    private String cityName;

    /**
     * Creates patient data object.
     *
     * @param patientId patient identifier
     * @param fullName full patient name
     * @param gender patient gender
     * @param birthDate patient birthdate
     * @param cityId related city identifier
     * @param cityName related city name
     */
    public Patient(int patientId, String fullName, String gender, LocalDate birthDate, int cityId, String cityName) {
        this.patientId = patientId;
        setFullName(fullName);
        setGender(gender);
        setBirthDate(birthDate);
        setCityId(cityId);
        this.cityName = cityName;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getFullName() {
        return fullName;
    }

    /**
     * Sets full patient name.
     *
     * @param fullName full patient name
     * @throws IllegalArgumentException if name is empty
     */
    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        this.fullName = fullName.trim();
    }

    public String getGender() {
        return gender;
    }

    /**
     * Sets patient gender.
     *
     * @param gender patient gender
     * @throws IllegalArgumentException if gender is empty
     */
    public void setGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender cannot be empty");
        }
        this.gender = gender.trim();
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * Sets patient birthdate.
     *
     * @param birthDate patient birthdate
     * @throws IllegalArgumentException if birthdate is empty
     */
    public void setBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date cannot be empty");
        }
        this.birthDate = birthDate;
    }

    public int getCityId() {
        return cityId;
    }

    /**
     * Sets related city identifier.
     *
     * @param cityId city identifier
     * @throws IllegalArgumentException if city id is not positive
     */
    public void setCityId(int cityId) {
        if (cityId <= 0) {
            throw new IllegalArgumentException("City id must be more than 0");
        }
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public String toString() {
        String city = cityName == null ? "CityId=" + cityId : cityName;
        return patientId + ". " + fullName + ", " + gender + ", " + birthDate + ", " + city;
    }
}
