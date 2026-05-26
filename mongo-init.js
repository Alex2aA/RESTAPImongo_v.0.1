/*
 * Скрипт инициализации MongoDB для ролевой авторизации.
 *
 * Шаги:
 *   1. Запустить mongod с опцией --auth (или включить авторизацию в конфиге)
 *   2. Подключиться под admin пользователем:
 *      mongosh mongodb://localhost:27017/admin -u admin -p
 *   3. Переключиться на базу company_db:
 *      use company_db
 *   4. Выполнить скрипт:
 *      load("mongo-init.js")
 */

// Переключаемся на рабочую базу
db = db.getSiblingDB("company_db");

// ============================================================
// Кастомные роли
// ============================================================

db.createRole({
  role: "app_reader",
  privileges: [
    { resource: { db: "company_db", collection: "employees" },  actions: ["find"] },
    { resource: { db: "company_db", collection: "departments" }, actions: ["find"] }
  ],
  roles: []
});

db.createRole({
  role: "app_editor",
  privileges: [
    { resource: { db: "company_db", collection: "employees" },  actions: ["find", "insert", "update", "remove"] },
    { resource: { db: "company_db", collection: "departments" }, actions: ["find"] }
  ],
  roles: []
});

db.createRole({
  role: "app_admin",
  privileges: [
    { resource: { db: "company_db", collection: "employees" },   actions: ["find", "insert", "update", "remove"] },
    { resource: { db: "company_db", collection: "departments" },  actions: ["find", "insert", "update", "remove"] },
    { resource: { db: "company_db", collection: "system_users" }, actions: ["find", "insert", "update", "remove"] },
    { resource: { db: "company_db", collection: "" },             actions: ["createCollection", "createIndex", "dropCollection"] }
  ],
  roles: []
});

// ============================================================
// Пользователи MongoDB (по одному на роль)
// ============================================================

db.createUser({
  user: "mongo_reader",
  pwd: "reader_pass",
  roles: [{ role: "app_reader", db: "company_db" }]
});

db.createUser({
  user: "mongo_editor",
  pwd: "editor_pass",
  roles: [{ role: "app_editor", db: "company_db" }]
});

db.createUser({
  user: "mongo_admin",
  pwd: "admin_pass",
  roles: [{ role: "app_admin", db: "company_db" }]
});

// ============================================================
// Индексы (создаются под admin, поскольку auto-index-creation отключён)
// ============================================================

db.system_users.createIndex({ "login": 1 }, { unique: true });

print("=== MongoDB инициализация завершена ===");
print("Роли: app_reader, app_editor, app_admin");
print("Пользователи: mongo_reader, mongo_editor, mongo_admin");
print("Индекс на system_users.login создан");
