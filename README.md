# Patient_registration_in_clinic

Даний проєкт розроблено в межах іспиту з дисципліни "Програмування (Java)". Застосунок призначений для автоматизації обліку пацієнтів, реєстрації візитів та аналізу діагнозів у клініці.


Склад команди
Team Lead: Плутенко Олексій Юрійович — координація, архітектура БД, Code Review.
Developer 1: Кубуша Олексій Сергійович — реалізація моделі даних (DAO/Service), SQL-скрипти.
Developer 2: Денисенко Денис Віталійович — розробка UI (JavaFX/FXML), контролери інтерфейсу.


Технологічний стек:
* Java 21 (OpenJDK)
* JavaFX 17+ (Графічний інтерфейс)
* MySQL (СУБД)
* JDBC (Робота з БД)
* Maven (Керування залежностями)
* JUnit 5 (Тестування логіки)


Архітектура (MVC)
Проєкт реалізовано за архітектурним шаблоном Model-View-Controller:
* Model: Класи-сутності (Patient, Visit) та логіка доступу до даних.
* View: Опис інтерфейсу у форматах .fxml.
* Controller: Обробка подій користувача та зв'язок між UI та логікою.
* Service/DB: Використання PreparedStatement та try-with-resources для безпечної роботи з БД.


Схема бази даних (ER-діаграма)
Фрагмент коду
erDiagram
    CITY ||--o{ PATIENT : "contains"
    PATIENT ||..o{ VISIT : "undergoes"
    DIAGNOSIS ||--o{ VISIT : "is_assigned_to"
    CITY {
        int CityId PK
        varchar City_Name
    }
    PATIENT {
        int PatientId PK
        varchar Full_Name
        varchar Gender
        date Birth_Date
        int CityId FK "Refers to City"
    }
    DIAGNOSIS {
        int DiagnosisId PK
        varchar Diagnosis_Name
    }
    VISIT {
        int VisitId PK
        int PatientId FK "Refers to Patient"
        int DiagnosisId FK "Refers to Diagnosis"
        date Visit_Date
        text Clinical_Notes
    }

.
.
<img src="https://github.com/user-attachments/assets/e807b2a0-e1c8-42b4-9240-e7bc96b623e3" width="400" alt="City Patient Visit">
.
.


Правила роботи в репозиторії
Гілки (Branching Strategy)
* main — стабільна версія проєкту. Прямі коміти заборонені.
* develop — основна гілка для інтеграції розробки.
* feature/особиста назва — персональні гілки розробників для конкретних завдань.


Правила комітів (Commit Messages)
Стандарт Conventional Commits:
* feat: — новий функціонал.
* fix: — виправлення помилки.
* docs: — зміни в документації.
* refactor: — зміна коду без зміни функціоналу.


Правила злиття (Merge Policy)
* Створення Pull Request (PR) зі своєї гілки в develop.
* PR має пройти перевірку (Code Review) від Team Lead.
* Після схвалення проводиться Merge.
