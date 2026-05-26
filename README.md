# RESTAPImongo

REST API на Spring Boot с MongoDB и ролевой авторизацией на стороне базы данных.

## Стек технологий

| Компонент | Технология |
|-----------|-----------|
| Язык | Java 17+ |
| Фреймворк | Spring Boot 2.7.18 |
| База данных | MongoDB |
| Аутентификация | JWT (jjwt 0.11.5) |
| Авторизация | MongoDB RBAC (кастомные роли) |
| Сборка | Maven |
| ORM | Spring Data MongoDB (MongoTemplate) |
| Дополнительно | Lombok, BCrypt |

## Архитектура авторизации

`@PreAuthorize` из контроллеров удалён. Ролевая проверка происходит на стороне MongoDB:

```
Клиент → JwtRequestFilter → Controller → Service → MongoTemplateRouter
                                                       ↓
                                              (выбор MongoTemplate по роли)
                                                       ↓
                          ┌───────────────────────────────────────┐
                          │  reader  →  mongo_reader  (ROLE_READER)│
                          │  editor  →  mongo_editor  (ROLE_EDITOR)│
                          │  admin   →  mongo_admin   (ROLE_ADMIN) │
                          └───────────────────────────────────────┘
                                                       ↓
                                              MongoDB проверяет права
                                                       ↓
                                         Нет прав → HTTP 403 Forbidden
```

### Привилегии MongoDB

| Коллекция | app_reader | app_editor | app_admin |
|-----------|:----------:|:----------:|:---------:|
| `employees` | `find` | `find, insert, update, remove` | все |
| `departments` | `find` | `find` | все |
| `system_users` | — | — | все |

## Эндпоинты

### Аутентификация

| Метод | Путь | Описание |
|-------|------|----------|
| POST | `/api/auth/login` | Вход, получение JWT |

### Сотрудники

| Метод | Путь | Доступ |
|-------|------|--------|
| GET | `/api/employees` | READER, EDITOR, ADMIN |
| GET | `/api/employees/{id}` | READER, EDITOR, ADMIN |
| POST | `/api/employees` | EDITOR, ADMIN |
| PUT | `/api/employees/{id}` | EDITOR, ADMIN |
| DELETE | `/api/employees/{id}` | EDITOR, ADMIN |

### Отделы

| Метод | Путь | Доступ |
|-------|------|--------|
| GET | `/api/departments` | READER, EDITOR, ADMIN |
| GET | `/api/departments/{id}` | READER, EDITOR, ADMIN |
| POST | `/api/departments` | ADMIN |
| DELETE | `/api/departments/{id}` | ADMIN |

### Отчёты

| Метод | Путь | Доступ |
|-------|------|--------|
| GET | `/api/reports/avg-salary-by-dept` | все аутентифицированные |
| GET | `/api/reports/employees-above-salary?minSalary={n}` | все аутентифицированные |
| GET | `/api/reports/count-by-position` | все аутентифицированные |

### Управление пользователями

| Метод | Путь | Доступ |
|-------|------|--------|
| GET | `/api/admin/users` | ADMIN |
| POST | `/api/admin/users` | ADMIN |
| DELETE | `/api/admin/users/{login}` | ADMIN |

## Запуск

### 1. Запуск MongoDB с авторизацией

```bash
# Запустите mongod с флагом --auth
mongod --auth --dbpath /path/to/data
```

### 2. Создание первого администратора MongoDB

```bash
mongosh
use admin
db.createUser({
  user: "site_admin",
  pwd: "admin123",
  roles: ["root"]
})
exit
```

### 3. Инициализация ролей и пользователей

```bash
mongosh mongodb://site_admin:admin123@localhost:27017/admin
use company_db
load("mongo-init.js")
exit
```

### 4. Запуск приложения

```bash
mvn spring-boot:run
```

При первом запуске в коллекции `system_users` будут созданы учётные записи:

| Логин | Пароль | Роль |
|-------|--------|------|
| `reader` | `reader123` | READER |
| `editor` | `editor123` | EDITOR |
| `admin` | `admin123` | ADMIN |

### 5. Тестирование

Импортируйте `RESTAPImongo.postman_collection.json` в Postman или используйте curl:

```bash
# Логин
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","password":"admin123"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['token'])")

# GET сотрудников
curl -s http://localhost:8080/api/employees \
  -H "Authorization: Bearer $TOKEN"
```

## Структура проекта

```
src/main/java/com/
├── Application.java
├── config/
│   ├── GlobalExceptionHandler.java    # Обработка ошибок MongoDB
│   ├── JwtRequestFilter.java          # JWT фильтр
│   ├── JwtUtil.java                   # Генерация/валидация JWT
│   ├── MongoConfig.java               # 3 MongoTemplate (reader/editor/admin)
│   ├── MongoTemplateRouter.java       # Роутинг по роли из JWT
│   └── SecurityConfig.java            # Spring Security конфигурация
├── controller/
│   ├── AdminController.java
│   ├── AuthController.java
│   ├── DepartmentController.java
│   ├── EmployeeController.java
│   └── ReportController.java
├── dto/
│   ├── AuthResponse.java
│   ├── LoginRequest.java
│   └── UserDto.java
├── model/
│   ├── Department.java
│   ├── Employee.java
│   └── SystemUser.java
├── repository/
│   ├── DepartmentRepository.java
│   ├── EmployeeRepository.java
│   └── UserRepository.java
└── service/
    ├── CustomUserDetailsService.java
    ├── DepartmentService.java
    ├── EmployeeService.java
    ├── ReportService.java
    └── UserService.java
```
