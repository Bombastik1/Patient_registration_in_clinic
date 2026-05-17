package sumdu.edu.ua;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;

import static org.testfx.assertions.api.Assertions.assertThat;

// Інтеграційне тестування користувацького інтерфейсу на базі TestFX та JUnit 5
@ExtendWith(ApplicationExtension.class)
@DisplayName("Комплексне тестування графічного інтерфейсу MainController")
class MainControllerTest {

    // Ініціалізація графічного потоку JavaFX та завантаження первинної сцени форми
    @Start
    void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }

    // Перехоплення та автоматичне закриття модальних вікон відсутності підключення до СКБД перед кожним тест-кейсом
    @BeforeEach
    void handleStartupAlert(FxRobot robot) {
        try {
            // Емуляція кліку по тригеру закриття системного діалогу для розблокування головного потоку форми
            robot.clickOn("OK");
        } catch (Exception e) {
            // Обробка виключення у випадку запуску тестів у середовищі з активним сервером MySQL
            System.out.println("Стартове вікно помилки відсутнє або вже закрите.");
        }
    }

    // Наскрізна перевірка базового користувацького сценарію введення та відправки пошукового запиту
    @Test
    @DisplayName("Тест 1: Повний цикл успішного пошуку пацієнта за текстом")
    void shouldExecuteFullSearchWorkflow(FxRobot robot) {
        // Локалізація цільових вузлів інтерфейсу за унікальними ідентифікаторами fx:id
        TextField searchField = robot.lookup("#searchField").queryAs(TextField.class);
        Button btnSearch = robot.lookup("#btnSearch").queryAs(Button.class);

        // Фокусування та емуляція посимвольного введення текстових даних з клавіатури
        robot.clickOn(searchField);
        String searchQuery = "Шевченко";
        robot.write(searchQuery);

        // Верифікація відповідності фактичного вмісту текстового компонента введеному значенню
        assertThat(searchField.getText()).isEqualTo(searchQuery);

        // Емуляція натискання на керуючу кнопку активації фільтрації даних
        robot.clickOn(btnSearch);

        // Перевірка збереження стану та цілісності даних у полі введення після відправки запиту
        assertThat(searchField.getText()).isEqualTo(searchQuery);
    }

    // Тестування стійкості обробників подій контролера до некоректних або порожніх вхідних параметрів
    @Test
    @DisplayName("Тест 2: Стійкість системи при спробі пустого пошуку (Граничні умови)")
    void shouldHandleEmptySearchQuerySafely(FxRobot robot) {
        TextField searchField = robot.lookup("#searchField").queryAs(TextField.class);
        Button btnSearch = robot.lookup("#btnSearch").queryAs(Button.class);
        TableView<?> patientsTable = robot.lookup("#patientsTable").queryAs(TableView.class);

        // Перевірка початкового пустого стану елемента текстового введення
        assertThat(searchField.getText()).isEmpty();

        // Генерація події кліку миші по кнопці пошуку без попереднього заповнення критеріїв
        robot.clickOn(btnSearch);

        // Верифікація відсутності аварійного завершення програми та збереження видимості контейнера даних
        assertThat(patientsTable.isVisible()).isTrue();
    }

    // Перевірка коректності зв'язування FXML розмітки з екземпляром контролера на рівні властивостей компонентів
    @Test
    @DisplayName("Тест 3: Валідація критичних компонентів та архітектури FXML форми")
    void shouldVerifyAllUIComponentsAndTextLabels(FxRobot robot) {
        // Контроль стану поля введення пошукового запиту щодо доступності для редагування
        TextField searchField = robot.lookup("#searchField").queryAs(TextField.class);
        assertThat(searchField).isNotNull();
        assertThat(searchField).isEnabled();
        assertThat(searchField.isEditable()).isTrue();

        // Перевірка відповідності текстової мітки на кнопці вимогам технічного завдання та локалізації
        Button btnSearch = robot.lookup("#btnSearch").queryAs(Button.class);
        assertThat(btnSearch).isNotNull();
        assertThat(btnSearch.getText()).isEqualTo("Пошук");

        // Валідація наявності об'єкта головної таблиці відображення сутностей пацієнтів
        TableView<?> patientsTable = robot.lookup("#patientsTable").queryAs(TableView.class);
        assertThat(patientsTable).isNotNull();
        assertThat(patientsTable.isVisible()).isTrue();

        // Перевірка реляційної структури TableView та успішної декларації колонок у файлі розмітки
        assertThat(patientsTable.getColumns()).isNotEmpty();
    }
}
