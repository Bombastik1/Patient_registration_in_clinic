package sumdu.edu.ua;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import sumdu.edu.ua.database.CityDatabase;
import sumdu.edu.ua.database.DiagnosisDatabase;
import sumdu.edu.ua.database.PatientDatabase;
import sumdu.edu.ua.database.VisitDatabase;
import sumdu.edu.ua.model.City;
import sumdu.edu.ua.model.Diagnosis;
import sumdu.edu.ua.model.Patient;
import sumdu.edu.ua.model.VisitDetails;
import sumdu.edu.ua.manager.ClinicManager;

/**
 * Console interface for testing database operations before JavaFX UI created.
 */
public class Main {

    /**
     * Starts console application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Помилка: передайте шлях до db.properties через args");
            return;
        }

        DatabaseManager databaseManager;
        try {
            databaseManager = new DatabaseManager(args[0]);
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка БД: " + e.getMessage());
            return;
        }

        ClinicManager clinicManager = new ClinicManager(
                new PatientDatabase(databaseManager),
                new CityDatabase(databaseManager),
                new DiagnosisDatabase(databaseManager),
                new VisitDatabase(databaseManager)
        );
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt(scanner, "Оберіть пункт меню: ");

            try {
                switch (choice) {
                    case 1 -> printPatients(clinicManager.getAllPatients());
                    case 2 -> searchPatients(scanner, clinicManager);
                    case 3 -> createPatient(scanner, clinicManager);
                    case 4 -> updatePatient(scanner, clinicManager);
                    case 5 -> printVisits(clinicManager.getAllVisits());
                    case 6 -> createVisit(scanner, clinicManager);
                    case 7 -> printCities(clinicManager.getAllCities());
                    case 8 -> createCity(scanner, clinicManager);
                    case 9 -> printDiagnoses(clinicManager.getAllDiagnoses());
                    case 10 -> createDiagnosis(scanner, clinicManager);
                    case 0 -> {
                        running = false;
                        System.out.println("Вихід з програми");
                    }
                    default -> System.out.println("Невірний пункт меню");
                }
            } catch (Exception e) {
                System.out.println("Помилка: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\nМЕНЮ");
        System.out.println("1 Показати всіх пацієнтів");
        System.out.println("2 Пошук пацієнтів");
        System.out.println("3 Додати пацієнта");
        System.out.println("4 Оновити пацієнта");
        System.out.println("5 Показати всі візити");
        System.out.println("6 Додати візит");
        System.out.println("7 Показати міста");
        System.out.println("8 Додати місто");
        System.out.println("9 Показати діагнози");
        System.out.println("10 Додати діагноз");
        System.out.println("0 Вихід");
    }

    private static void searchPatients(Scanner scanner, ClinicManager clinicManager) throws Exception {
        System.out.println("\n1 Пошук за ПІБ");
        System.out.println("2 Пошук за містом");
        System.out.println("3 Пошук за статтю");
        int choice = readInt(scanner, "Оберіть критерій: ");

        switch (choice) {
            case 1 -> printPatients(clinicManager.searchPatientsByName(readNonEmptyString(scanner, "ПІБ: ")));
            case 2 -> printPatients(clinicManager.searchPatientsByCity(readNonEmptyString(scanner, "Місто: ")));
            case 3 -> printPatients(clinicManager.searchPatientsByGender(readNonEmptyString(scanner, "Стать: ")));
            default -> System.out.println("Ви обрали не вірний пункт меню");
        }
    }

    private static void createPatient(Scanner scanner, ClinicManager clinicManager) throws Exception {
        String fullName = readNonEmptyString(scanner, "ПІБ: ");
        String gender = readNonEmptyString(scanner, "Стать: ");
        LocalDate birthDate = readDate(scanner, "Дата народження (yyyy-mm-dd): ");
        int cityId = selectCityId(scanner, clinicManager);

        Patient patient = new Patient(0, fullName, gender, birthDate, cityId, null);
        int generatedId = clinicManager.createPatient(patient);
        System.out.println("Пацієнта додано. ID = " + generatedId);
    }

    private static void updatePatient(Scanner scanner, ClinicManager clinicManager) throws Exception {
        int patientId = readInt(scanner, "PatientId: ");
        String fullName = readNonEmptyString(scanner, "Новий ПІБ: ");
        String gender = readNonEmptyString(scanner, "Нова стать: ");
        LocalDate birthDate = readDate(scanner, "Нова дата народження (yyyy-mm-dd): ");
        int cityId = selectCityId(scanner, clinicManager);

        Patient patient = new Patient(patientId, fullName, gender, birthDate, cityId, null);
        if (clinicManager.updatePatient(patient)) {
            System.out.println("Пацієнта оновлено");
        } else {
            System.out.println("Пацієнта з таким ID не знайдено");
        }
    }

    private static void createVisit(Scanner scanner, ClinicManager clinicManager) throws Exception {
        printPatients(clinicManager.getAllPatients());
        int patientId = readInt(scanner, "PatientId: ");
        int diagnosisId = selectDiagnosisId(scanner, clinicManager);
        LocalDate visitDate = readDate(scanner, "Дата візиту (yyyy-mm-dd): ");
        String notes = readOptionalString(scanner, "Клінічні нотатки: ");

        int generatedId = clinicManager.createVisit(patientId, diagnosisId, visitDate, notes);
        System.out.println("Візит додано ID = " + generatedId);
    }

    private static void createCity(Scanner scanner, ClinicManager clinicManager) throws Exception {
        String cityName = readNonEmptyString(scanner, "Назва міста: ");
        int generatedId = clinicManager.createCity(cityName);
        System.out.println("Місто додано ID = " + generatedId);
    }

    private static void createDiagnosis(Scanner scanner, ClinicManager clinicManager) throws Exception {
        String diagnosisName = readNonEmptyString(scanner, "Назва діагнозу: ");
        int generatedId = clinicManager.createDiagnosis(diagnosisName);
        System.out.println("Діагноз додано ID = " + generatedId);
    }

    private static int selectCityId(Scanner scanner, ClinicManager clinicManager) throws Exception {
        while (true) {
            System.out.println("\nОберіть місто:");
            printCities(clinicManager.getAllCities());
            System.out.println("0 Додати нове місто");
            int cityId = readInt(scanner, "Ваш вибір: ");

            if (cityId == 0) {
                String cityName = readNonEmptyString(scanner, "Введіть назва нового міста: ");
                int generatedId = clinicManager.createCity(cityName);
                System.out.println("Місто додано ID = " + generatedId);
                return generatedId;
            }

            if (cityExists(clinicManager.getAllCities(), cityId)) {
                return cityId;
            }

            System.out.println("Міста з таким ID не знайдено");
        }
    }

    private static int selectDiagnosisId(Scanner scanner, ClinicManager clinicManager) throws Exception {
        while (true) {
            System.out.println("\nОберіть діагноз:");
            printDiagnoses(clinicManager.getAllDiagnoses());
            System.out.println("0. Додати новий діагноз");
            int diagnosisId = readInt(scanner, "Ваш вибір: ");

            if (diagnosisId == 0) {
                String diagnosisName = readNonEmptyString(scanner, "Введіть назва нового діагнозу: ");
                int generatedId = clinicManager.createDiagnosis(diagnosisName);
                System.out.println("Діагноз додано його ID є " + generatedId);
                return generatedId;
            }

            if (diagnosisExists(clinicManager.getAllDiagnoses(), diagnosisId)) {
                return diagnosisId;
            }

            System.out.println("Діагноз з таким ID не знайдено");
        }
    }

    private static boolean cityExists(List<City> cities, int cityId) {
        for (City city : cities) {
            if (city.getCityId() == cityId) {
                return true;
            }
        }
        return false;
    }

    private static boolean diagnosisExists(List<Diagnosis> diagnoses, int diagnosisId) {
        for (Diagnosis diagnosis : diagnoses) {
            if (diagnosis.getDiagnosisId() == diagnosisId) {
                return true;
            }
        }
        return false;
    }

    private static void printPatients(List<Patient> patients) {
        if (patients.isEmpty()) {
            System.out.println("Пацієнтів не знайдено");
            return;
        }

        for (Patient patient : patients) {
            System.out.println(patient);
        }
    }

    private static void printCities(List<City> cities) {
        if (cities.isEmpty()) {
            System.out.println("Міст не знайдено");
            return;
        }

        for (City city : cities) {
            System.out.println(city);
        }
    }

    private static void printDiagnoses(List<Diagnosis> diagnoses) {
        if (diagnoses.isEmpty()) {
            System.out.println("Діагнозів не знайдено");
            return;
        }

        for (Diagnosis diagnosis : diagnoses) {
            System.out.println(diagnosis);
        }
    }

    private static void printVisits(List<VisitDetails> visits) {
        if (visits.isEmpty()) {
            System.out.println("Візитів не знайдено");
            return;
        }

        for (VisitDetails visit : visits) {
            System.out.println(visit);
        }
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Помилка: введіть ціле число");
                scanner.nextLine();
            }
        }
    }

    private static LocalDate readDate(Scanner scanner, String prompt) {
        while (true) {
            try {
                return LocalDate.parse(readNonEmptyString(scanner, prompt));
            } catch (DateTimeParseException e) {
                System.out.println("Помилка: дата повинна бути у форматі yyyy-mm-dd");
            }
        }
    }

    private static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Помилка: рядок не може бути порожнім");
        }
    }

    private static String readOptionalString(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
