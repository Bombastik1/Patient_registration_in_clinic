package sumdu.edu.ua;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import sumdu.edu.ua.manager.ClinicManager;
import sumdu.edu.ua.model.City;
import sumdu.edu.ua.model.Diagnosis;
import sumdu.edu.ua.model.Patient;
import sumdu.edu.ua.model.VisitDetails;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.testfx.assertions.api.Assertions.assertThat;

// Комплексне інтеграційне тестування інтерактивних функцій інтерфейсу на базі Mock-даних
@ExtendWith(ApplicationExtension.class)
@DisplayName("Максимальне функціональне тестування всіх модулів та вкладок MainController")
class MainControllerTest {

    // Створення ізольованих репозиторіїв в оперативній пам'яті (імітація таблиць MySQL)
    private final List<Patient> fakePatients = new ArrayList<>();
    private final List<City> fakeCities = new ArrayList<>();
    private final List<Diagnosis> fakeDiagnoses = new ArrayList<>();
    private final List<VisitDetails> fakeVisits = new ArrayList<>();

    @Start
    void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        // Наповнення локального репозиторію початковими тестовими даними
        fakeCities.clear();
        fakeCities.add(new City(1, "Суми"));
        fakeCities.add(new City(2, "Київ"));

        fakeDiagnoses.clear();
        fakeDiagnoses.add(new Diagnosis(1, "Грип"));
        fakeDiagnoses.add(new Diagnosis(2, "Алергія"));

        fakePatients.clear();
        fakePatients.add(new Patient(1, "Шевченко Тарас", "Чоловіча", LocalDate.of(1990, 5, 10), 1, "Суми"));
        fakePatients.add(new Patient(2, "Коваленко Анна", "Жіноча", LocalDate.of(1995, 8, 22), 2, "Київ"));

        fakeVisits.clear();
        fakeVisits.add(new VisitDetails(1, "Шевченко Тарас", "Грип", LocalDate.now(), "Приписано амбулаторне лікування"));

        // Отримуємо посилання на вже створений та ініціалізований контролер
        MainController controller = fxmlLoader.getController();

        // Ініціалізація глобального Mock-менеджера з повною реалізацією бізнес-операцій
        ClinicManager mockManager = new ClinicManager(null, null, null, null) {
            @Override
            public List<Patient> getAllPatients() { return new ArrayList<>(fakePatients); }
            @Override
            public List<City> getAllCities() { return new ArrayList<>(fakeCities); }
            @Override
            public List<Diagnosis> getAllDiagnoses() { return new ArrayList<>(fakeDiagnoses); }
            @Override
            public List<VisitDetails> getAllVisits() { return new ArrayList<>(fakeVisits); }

            @Override
            public List<Patient> searchPatientsByName(String name) {
                return fakePatients.stream().filter(p -> p.getFullName().toLowerCase().contains(name.toLowerCase())).toList();
            }
            @Override
            public List<Patient> searchPatientsByCity(String cityName) {
                return fakePatients.stream().filter(p -> p.getCityName().toLowerCase().contains(cityName.toLowerCase())).toList();
            }
            @Override
            public List<Patient> searchPatientsByGender(String gender) {
                return fakePatients.stream().filter(p -> p.getGender().equalsIgnoreCase(gender)).toList();
            }

            @Override
            public int createPatient(Patient patient) {
                int id = fakePatients.size() + 1;
                fakePatients.add(new Patient(id, patient.getFullName(), patient.getGender(), patient.getBirthDate(), patient.getCityId(), "Суми"));
                return id;
            }
            @Override
            public boolean updatePatient(Patient patient) {
                fakePatients.removeIf(p -> p.getPatientId() == patient.getPatientId());
                fakePatients.add(patient);
                return true;
            }
            @Override
            public boolean deletePatient(int patientId) {
                return fakePatients.removeIf(p -> p.getPatientId() == patientId);
            }
            @Override
            public int createCity(String cityName) {
                int id = fakeCities.size() + 1;
                fakeCities.add(new City(id, cityName));
                return id;
            }
            @Override
            public int createDiagnosis(String diagnosisName) {
                int id = fakeDiagnoses.size() + 1;
                fakeDiagnoses.add(new Diagnosis(id, diagnosisName));
                return id;
            }
            @Override
            public int createVisit(int patientId, int diagnosisId, LocalDate visitDate, String clinicalNotes) {
                int id = fakeVisits.size() + 1;
                fakeVisits.add(new VisitDetails(id, "Шевченко Тарас", "Грип", visitDate, clinicalNotes));
                return id;
            }
            @Override
            public boolean deleteVisit(int visitId) {
                return fakeVisits.removeIf(v -> v.getVisitId() == visitId);
            }
        };

        // Заміна реального провайдера даних на Mock-об'єкт через механізм Java Reflection
        try {
            Field managerField = MainController.class.getDeclaredField("clinicManager");
            managerField.setAccessible(true);
            managerField.set(controller, mockManager);

            // Викликаємо приватний метод завантаження даних
            Method loadDataMethod = MainController.class.getDeclaredMethod("loadInitialData");
            loadDataMethod.setAccessible(true);
            loadDataMethod.invoke(controller);

        } catch (Exception e) {
            throw new RuntimeException("Помилка конфігурації архітектури Mock-тестування", e);
        }

        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }

    @Test
    @DisplayName("Тест 1: Валідація наскрізних функцій пошуку та фільтрації пацієнтів")
    void shouldExecutePatientSearchAndFilters(FxRobot robot) {
        TextField searchField = robot.lookup("#searchField").queryAs(TextField.class);
        Button btnSearch = robot.lookup("#btnSearch").queryAs(Button.class);
        TableView<Patient> patientsTable = robot.lookup("#patientsTable").queryAs(TableView.class);

        // Контроль первинного стану таблиці пацієнтів
        assertThat(patientsTable.getItems()).hasSize(2);

        // Перевірка фільтрації таблиці за прізвищем
        robot.clickOn(searchField).write("Шевченко");
        robot.clickOn(btnSearch);
        assertThat(patientsTable.getItems()).hasSize(1);

        // Очищення поля пошуку для перевірки відновлення даних
        robot.clickOn(searchField);
        searchField.clear();
        robot.clickOn(btnSearch);
        assertThat(patientsTable.getItems()).hasSize(2);
    }

    @Test
    @DisplayName("Тест 2: Перевірка інтерфейсу додавання нового пацієнта (Валідація діалогів)")
    void shouldOpenAndVerifyAddPatientDialog(FxRobot robot) {
        Button btnAddPatient = robot.lookup("#btnAddPatient").queryAs(Button.class);
        robot.clickOn(btnAddPatient);

        // Пошук контейнера модального вікна додавання пацієнта
        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        assertThat(dialogPane).isNotNull();
        assertThat(dialogPane.getHeaderText()).contains("Введіть дані пацієнта");

        // Закриття діалогового вікна через натискання кнопки скасування
        robot.clickOn("Cancel");
    }

    @Test
    @DisplayName("Тест 3: Перевірка функцій видалення виділеного пацієнта з таблиці")
    void shouldDeleteSelectedPatientSuccessfully(FxRobot robot) {
        TableView<Patient> patientsTable = robot.lookup("#patientsTable").queryAs(TableView.class);
        Button btnDeletePatient = robot.lookup("#btnDeletePatient").queryAs(Button.class);

        // ВИПРАВЛЕНО: Примусово виділяємо перший рядок саме в таблиці пацієнтів, щоб уникнути конфлікту елементів
        patientsTable.getSelectionModel().select(0);

        // Генерація події видалення
        robot.clickOn(btnDeletePatient);

        // Перевірка того, що пацієнт успішно видалився і таблиця зменшилась
        assertThat(patientsTable.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("Тест 4: Перевірка роботи модуля обліку медичних візитів та їх скасування")
    void shouldManageVisitsTabAndCancelation(FxRobot robot) {
        // Перехід на вкладку "Візити" за допомогою текстового маркера мітки
        robot.clickOn("Візити");

        TableView<VisitDetails> visitsTable = robot.lookup("#visitsTable").queryAs(TableView.class);
        Button btnDeleteVisit = robot.lookup("#btnDeleteVisit").queryAs(Button.class);

        // Контроль наявності запису про візит пацієнта
        assertThat(visitsTable.getItems()).hasSize(1);

        // ВИПРАВЛЕНО: Примусово виділяємо перший рядок саме в таблиці візитів
        visitsTable.getSelectionModel().select(0);
        robot.clickOn(btnDeleteVisit);

        // Перевірка фінального очищення таблиці після скасування події
        assertThat(visitsTable.getItems()).isEmpty();
    }

    @Test
    @DisplayName("Тест 5: Перевірка динамічного розширення довідників міст")
    void shouldModifyDictionariesFields(FxRobot robot) {
        // Перехід на вкладку роботи з системними довідниками
        robot.clickOn("Довідники");

        TextField cityNameField = robot.lookup("#cityNameField").queryAs(TextField.class);
        Button btnAddCity = robot.lookup("#btnAddCity").queryAs(Button.class);
        TableView<City> citiesTable = robot.lookup("#citiesTable").queryAs(TableView.class);

        // Валідація базового наповнення довідника міст
        assertThat(citiesTable.getItems()).hasSize(2);

        // Емуляція створення нового міста користувачем
        robot.clickOn(cityNameField).write("Харків");
        robot.clickOn(btnAddCity);

        // Контроль того, що нове місто миттєво додалося до таблиці відображення довідника
        assertThat(citiesTable.getItems()).hasSize(3);
        assertThat(cityNameField.getText()).isEmpty();
    }
}
