package sumdu.edu.ua;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import sumdu.edu.ua.database.CityDatabase;
import sumdu.edu.ua.database.DiagnosisDatabase;
import sumdu.edu.ua.database.PatientDatabase;
import sumdu.edu.ua.database.VisitDatabase;
import sumdu.edu.ua.manager.ClinicManager;
import sumdu.edu.ua.manager.DatabaseManager;
import sumdu.edu.ua.model.City;
import sumdu.edu.ua.model.Diagnosis;
import sumdu.edu.ua.model.Patient;
import sumdu.edu.ua.model.VisitDetails;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.stage.Stage;
import javafx.scene.image.Image;

/**
 * Controller class for the main JavaFX user interface.
 * Handles UI events and communicates with the underlying business logic through ClinicManager.
 */
public class MainController implements Initializable {

    @FXML private TableView<Patient> patientsTable;
    @FXML private TableColumn<Patient, Integer> colPatientId;
    @FXML private TableColumn<Patient, String> colFullName;
    @FXML private TableColumn<Patient, String> colGender;
    @FXML private TableColumn<Patient, LocalDate> colBirthDate;
    @FXML private TableColumn<Patient, String> colPatientCityName;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchCriterionCombo;

    @FXML private TableView<VisitDetails> visitsTable;
    @FXML private TableColumn<VisitDetails, Integer> colVisitId;
    @FXML private TableColumn<VisitDetails, LocalDate> colVisitDate;
    @FXML private TableColumn<VisitDetails, String> colPatientName;
    @FXML private TableColumn<VisitDetails, String> colPatientDiagnosisName;
    @FXML private TableColumn<VisitDetails, String> colClinicalNotes;

    @FXML private TableView<City> citiesTable;
    @FXML private TableColumn<City, Integer> colCityId;
    @FXML private TableColumn<City, String> colCityName;
    @FXML private TextField cityNameField;

    @FXML private TableView<Diagnosis> diagnosesTable;
    @FXML private TableColumn<Diagnosis, Integer> colDiagnosisId;
    @FXML private TableColumn<Diagnosis, String> colDiagnosisName;
    @FXML private TextField diagnosisNameField;

    private ClinicManager clinicManager;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     *
     * @param location  the location used to resolve relative paths for the root object
     * @param resources the resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDatabase();
        setupTableColumns();
        loadInitialData();

        searchCriterionCombo.setItems(FXCollections.observableArrayList("ПІБ", "Місто", "Стать"));
        searchCriterionCombo.getSelectionModel().selectFirst();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                try {
                    refreshPatients();
                } catch (Exception e) {
                    System.err.println("Помилка при оновленні списку: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Initializes the database connection and the application manager.
     */
    private void setupDatabase() {
        try {
            DatabaseManager dbManager = new DatabaseManager("src/main/resources/db.properties");
            CityDatabase cityDb = new CityDatabase(dbManager);
            DiagnosisDatabase diagnosisDb = new DiagnosisDatabase(dbManager);
            PatientDatabase patientDb = new PatientDatabase(dbManager);
            VisitDatabase visitDb = new VisitDatabase(dbManager);

            clinicManager = new ClinicManager(patientDb, cityDb, diagnosisDb, visitDb);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Помилка БД", "Перевірте підключення до бази: " + e.getMessage());
        }
    }

    /**
     * Binds the columns of all TableViews to their respective model properties.
     */
    private void setupTableColumns() {
        colPatientId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        colPatientCityName.setCellValueFactory(new PropertyValueFactory<>("cityName"));

        colVisitId.setCellValueFactory(new PropertyValueFactory<>("visitId"));
        colVisitDate.setCellValueFactory(new PropertyValueFactory<>("visitDate"));
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colPatientDiagnosisName.setCellValueFactory(new PropertyValueFactory<>("diagnosisName"));
        colClinicalNotes.setCellValueFactory(new PropertyValueFactory<>("clinicalNotes"));

        colCityId.setCellValueFactory(new PropertyValueFactory<>("cityId"));
        colCityName.setCellValueFactory(new PropertyValueFactory<>("cityName"));
        colDiagnosisId.setCellValueFactory(new PropertyValueFactory<>("diagnosisId"));
        colDiagnosisName.setCellValueFactory(new PropertyValueFactory<>("diagnosisName"));
    }

    /**
     * Populates the application tables with initial data from the database.
     */
    private void loadInitialData() {
        if (clinicManager == null) return;
        try {
            refreshPatients();
            refreshVisits();
            refreshCities();
            refreshDiagnoses();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Помилка завантаження", e.getMessage());
        }
    }

    /**
     * Handles the action of adding a new patient.
     *
     * @param event the action event triggered by the user
     */
    @FXML
    void handleAddPatient(ActionEvent event) {
        showPatientDialog(null).ifPresent(patient -> {
            try {
                clinicManager.createPatient(patient);
                refreshPatients();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося додати пацієнта: " + e.getMessage());
            }
        });
    }

    /**
     * Handles the action of updating an existing patient.
     *
     * @param event the action event triggered by the user
     */
    @FXML
    void handleUpdatePatient(ActionEvent event) {
        Patient selected = patientsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Увага", "Будь ласка, оберіть пацієнта для оновлення у таблиці.");
            return;
        }

        showPatientDialog(selected).ifPresent(patient -> {
            try {
                clinicManager.updatePatient(patient);
                refreshPatients();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося оновити пацієнта: " + e.getMessage());
            }
        });
    }

    /**
     * Handles the action of deleting a patient from the system.
     *
     * @param event the action event triggered by the user
     */
    @FXML
    void handleDeletePatient(ActionEvent event) {
        Patient selected = patientsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Увага", "Будь ласка, оберіть пацієнта для видалення у таблиці.");
            return;
        }

        try {
            clinicManager.deletePatient(selected.getPatientId());
            refreshPatients();
            refreshVisits();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Неможливо видалити пацієнта.");
        }
    }

    /**
     * Handles the search functionality based on the selected criterion.
     *
     * @param event the action event triggered by the user
     */
    @FXML
    void handleSearch(ActionEvent event) {
        String term = searchField.getText();
        String criterion = searchCriterionCombo.getValue();
        try {
            List<Patient> results = clinicManager.getAllPatients();
            if (term != null && !term.isEmpty()) {
                results = switch (criterion) {
                    case "Місто" -> clinicManager.searchPatientsByCity(term);
                    case "Стать" -> clinicManager.searchPatientsByGender(term);
                    default -> clinicManager.searchPatientsByName(term);
                };
            }
            patientsTable.setItems(FXCollections.observableArrayList(results));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Помилка пошуку", e.getMessage());
        }
    }

    /**
     * Handles the action of adding a new visit record.
     *
     * @param event the action event triggered by the user
     */
    @FXML
    void handleAddVisit(ActionEvent event) {
        showVisitDialog().ifPresent(visitInfo -> {
            try {
                int patientId = (Integer) visitInfo[0];
                int diagnosisId = (Integer) visitInfo[1];
                LocalDate date = (LocalDate) visitInfo[2];
                String notes = (String) visitInfo[3];

                clinicManager.createVisit(patientId, diagnosisId, date, notes);
                refreshVisits();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося додати візит: " + e.getMessage());
            }
        });
    }

    /**
     * Handles the action of deleting a visit record.
     *
     * @param event the action event triggered by the user
     */
    @FXML
    void handleDeleteVisit(ActionEvent event) {
        VisitDetails selected = visitsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Увага", "Будь ласка, оберіть візит для скасування у таблиці.");
            return;
        }

        try {
            clinicManager.deleteVisit(selected.getVisitId());
            refreshVisits();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося видалити візит.");
        }
    }

    /**
     * Handles the action of adding a new city to the database.
     *
     * @param event the action event triggered by the user
     */
    @FXML
    void handleAddCity(ActionEvent event) {
        String name = cityNameField.getText();
        if (name == null || name.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Увага", "Поле 'Впишіть нове місто' не може бути порожнім!");
            return;
        }

        try {
            clinicManager.createCity(name);
            cityNameField.clear();
            refreshCities();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося додати місто. Можливо, воно вже існує.");
        }
    }

    /**
     * Handles the action of adding a new diagnosis to the database.
     *
     * @param event the action event triggered by the user
     */
    @FXML
    void handleAddDiagnosis(ActionEvent event) {
        String name = diagnosisNameField.getText();
        if (name == null || name.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Увага", "Поле 'Впишіть новий діагноз' не може бути порожнім!");
            return;
        }

        try {
            clinicManager.createDiagnosis(name);
            diagnosisNameField.clear();
            refreshDiagnoses();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося додати діагноз. Можливо, він вже існує.");
        }
    }

    /**
     * Displays a dialog for adding or editing a patient.
     *
     * @param existingPatient the patient to edit, or null if creating a new patient
     * @return an Optional containing the valid Patient object, or empty if canceled
     */
    private Optional<Patient> showPatientDialog(Patient existingPatient) {
        Dialog<Patient> dialog = new Dialog<>();

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());

        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/logo.png"))));

        dialog.setTitle(existingPatient == null ? "Додати пацієнта" : "Оновити пацієнта");
        dialog.setHeaderText("Введіть дані пацієнта");

        ButtonType saveButtonType = new ButtonType("Зберегти", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 50, 20, 20));

        TextField nameField = new TextField();
        nameField.setPromptText("ПІБ");
        ComboBox<String> genderCombo = new ComboBox<>(FXCollections.observableArrayList("Чоловіча", "Жіноча"));
        DatePicker birthDatePicker = new DatePicker();
        ComboBox<City> cityCombo = new ComboBox<>();

        try {
            cityCombo.setItems(FXCollections.observableArrayList(clinicManager.getAllCities()));
        } catch (Exception ignored) {}

        if (existingPatient != null) {
            nameField.setText(existingPatient.getFullName());
            genderCombo.setValue(existingPatient.getGender());
            birthDatePicker.setValue(existingPatient.getBirthDate());
            cityCombo.getItems().stream().filter(c -> c.getCityId() == existingPatient.getCityId()).findFirst().ifPresent(cityCombo::setValue);
        }

        grid.add(new Label("ПІБ:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Стать:"), 0, 1);
        grid.add(genderCombo, 1, 1);
        grid.add(new Label("Дата народження:"), 0, 2);
        grid.add(birthDatePicker, 1, 2);
        grid.add(new Label("Місто:"), 0, 3);
        grid.add(cityCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Button btOk = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            if (nameField.getText().trim().isEmpty() || genderCombo.getValue() == null || birthDatePicker.getValue() == null || cityCombo.getValue() == null) {
                event.consume();
                showAlert(Alert.AlertType.WARNING, "Помилка заповнення", "Будь ласка, заповніть всі обов'язкові поля пацієнта!");
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                int id = existingPatient == null ? 0 : existingPatient.getPatientId();
                return new Patient(id, nameField.getText(), genderCombo.getValue(), birthDatePicker.getValue(), cityCombo.getValue().getCityId(), cityCombo.getValue().getCityName());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Displays a dialog for recording a new visit.
     *
     * @return an Optional containing an array of visit data, or empty if canceled
     */
    private Optional<Object[]> showVisitDialog() {
        Dialog<Object[]> dialog = new Dialog<>();

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());

        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/logo.png"))));

        dialog.setTitle("Записати на візит");
        dialog.setHeaderText("Оберіть пацієнта та діагноз");

        ButtonType saveButtonType = new ButtonType("Записати", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        ComboBox<Patient> patientCombo = new ComboBox<>();
        ComboBox<Diagnosis> diagnosisCombo = new ComboBox<>();
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField notesField = new TextField();

        try {
            patientCombo.setItems(FXCollections.observableArrayList(clinicManager.getAllPatients()));
            diagnosisCombo.setItems(FXCollections.observableArrayList(clinicManager.getAllDiagnoses()));
        } catch (Exception ignored) {}

        grid.add(new Label("Пацієнт:"), 0, 0);
        grid.add(patientCombo, 1, 0);
        grid.add(new Label("Діагноз:"), 0, 1);
        grid.add(diagnosisCombo, 1, 1);
        grid.add(new Label("Дата:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Нотатки:"), 0, 3);
        grid.add(notesField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Button btOk = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            if (patientCombo.getValue() == null || diagnosisCombo.getValue() == null || datePicker.getValue() == null) {
                event.consume();
                showAlert(Alert.AlertType.WARNING, "Помилка заповнення", "Будь ласка, обов'язково оберіть пацієнта, діагноз та дату!");
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Object[]{patientCombo.getValue().getPatientId(), diagnosisCombo.getValue().getDiagnosisId(), datePicker.getValue(), notesField.getText()};
            }
            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Retrieves the latest list of patients and updates the TableView.
     *
     * @throws Exception if a database access error occurs
     */
    private void refreshPatients() throws Exception {
        patientsTable.setItems(FXCollections.observableArrayList(clinicManager.getAllPatients()));
    }

    /**
     * Retrieves the latest list of visits and updates the TableView.
     *
     * @throws Exception if a database access error occurs
     */
    private void refreshVisits() throws Exception {
        visitsTable.setItems(FXCollections.observableArrayList(clinicManager.getAllVisits()));
    }

    /**
     * Retrieves the latest list of cities and updates the TableView.
     *
     * @throws Exception if a database access error occurs
     */
    private void refreshCities() throws Exception {
        citiesTable.setItems(FXCollections.observableArrayList(clinicManager.getAllCities()));
    }

    /**
     * Retrieves the latest list of diagnoses and updates the TableView.
     *
     * @throws Exception if a database access error occurs
     */
    private void refreshDiagnoses() throws Exception {
        diagnosesTable.setItems(FXCollections.observableArrayList(clinicManager.getAllDiagnoses()));
    }

    /**
     * Displays an alert window with the specified information.
     *
     * @param type    the type of alert (e.g., ERROR, WARNING)
     * @param title   the title text for the dialog
     * @param message the main content message
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}