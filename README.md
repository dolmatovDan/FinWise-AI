# FinWise-AI

Микросервис для управления финансами, построенный на Go, PostgreSQL и Docker.

## Технологии

- **Go 1.24** - Язык программирования
- **Gin** - HTTP веб-фреймворк
- **PostgreSQL 16** - База данных
- **pgx/v5** - Драйвер PostgreSQL
- **Docker & Docker Compose** - Контейнеризация
- **migrate** - Миграции базы данных

## Требования

- Docker
- Docker Compose

## Быстрый старт

### 1. Клонируйте репозиторий

```bash
git clone https://github.com/dolmatovDan/FinWise-AI.git
cd FinWise-AI
```

### 2. Настройте окружение (опционально)

Скопируйте файл с примером переменных окружения и измените при необходимости:

```bash
cp .env.example .env
```

Конфигурация по умолчанию:
- **Auth сервис**: `localhost:8082`
- **Transactions сервис**: `localhost:8080`
- **ML-API сервис**: `localhost:8081`
- **База данных**: PostgreSQL на порту `5432`
- **Уровень логирования**: `info`

### 3. Сгенерируйте JWT ключи (первый запуск)

Для работы аутентификации необходимы RSA ключи:

```bash
# Генерация ключей для auth сервиса
cd backend/auth/keys && ./generate_keys.sh && cd ../../..

# Копирование публичного ключа в transactions сервис
cp backend/auth/keys/public.pem backend/transactions/keys/public.pem
```

**Важно:** Ключи `private.pem` не должны попадать в git (они в .gitignore).

### 4. Запустите приложение

```bash
docker-compose up --build
```

Это действие:
1. Запустит контейнер PostgreSQL
2. Автоматически выполнит миграции базы данных
3. Запустит три микросервиса:
   - Auth Service (порт 8082)
   - Transactions Service (порт 8080)
   - ML-API Service (порт 8081)

Сервисы будут доступны по адресам:
- Auth: `http://localhost:8082`
- Transactions: `http://localhost:8080`
- ML-API: `http://localhost:8081`

### 5. Проверьте работу сервисов

```bash
# Auth Service
curl http://localhost:8082/health

# Transactions Service
curl http://localhost:8080/health

# ML-API Service
curl http://localhost:8081/health
```

Ожидаемый ответ от каждого:
```json
{
  "status": "ok"
}
```

## API эндпоинты

### Проверка здоровья
```bash
GET /health
```

### Аутентификация

Auth сервис работает на порту **8082** и предоставляет API для управления пользователями и JWT токенами.

#### Регистрация нового пользователя
```bash
POST http://localhost:8082/api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securepassword123",
  "full_name": "Иван Иванов"
}
```

**Ответ (201 Created):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "full_name": "Иван Иванов",
  "created_at": "2025-11-29T10:00:00Z",
  "updated_at": "2025-11-29T10:00:00Z"
}
```

**Требования:**
- `email` - обязательный, валидный email, макс 255 символов
- `password` - обязательный, минимум 8 символов, максимум 72 символа
- `full_name` - опциональный, макс 255 символов

#### Вход (Login)
```bash
POST http://localhost:8082/api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securepassword123"
}
```

**Ответ (200 OK):**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "550e8400-e29b-41d4-a716-446655440000",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "full_name": "Иван Иванов",
    "created_at": "2025-11-29T10:00:00Z",
    "updated_at": "2025-11-29T10:00:00Z"
  }
}
```

**Токены:**
- `access_token` - JWT токен, срок жизни **15 минут**, используется для авторизации API запросов
- `refresh_token` - UUID токен, срок жизни **30 дней**, используется для обновления access token

#### Обновление токена (Refresh)
```bash
POST http://localhost:8082/api/v1/auth/refresh
Content-Type: application/json

{
  "refresh_token": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Ответ (200 OK):**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "660e8400-e29b-41d4-a716-446655440001"
}
```

**Примечание:** При обновлении генерируется новый refresh token, старый становится недействительным.

#### Выход (Logout)
```bash
POST http://localhost:8082/api/v1/auth/logout
Content-Type: application/json

{
  "refresh_token": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Ответ (204 No Content)**

Удаляет refresh token из базы данных, делая его недействительным.

#### Использование Access Token

Для работы с защищенными endpoints (например, транзакции) добавляйте access token в заголовок:

```bash
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Примеры:**

```bash
# Создание транзакции с авторизацией
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "user_id": 1,
    "amount": 100.50,
    "category": "groceries",
    "type": "expense"
  }'
```

**Ошибки аутентификации:**

- `400 Bad Request` - Невалидный формат запроса
- `401 Unauthorized` - Неверные учетные данные или токен
- `409 Conflict` - Email уже зарегистрирован
- `500 Internal Server Error` - Внутренняя ошибка сервера

### Транзакции

#### Создать транзакцию
```bash
POST /api/v1/transactions
Content-Type: application/json

{
  "user_id": 1,
  "amount": 100.50,
  "category": "groceries",
  "description": "Еженедельные покупки",
  "type": "expense"
}
```

#### Получить транзакцию по ID
```bash
GET /api/v1/transactions/{id}
```

#### Список транзакций
```bash
GET /api/v1/transactions?user_id=1&type=expense&category=groceries&page=1&page_size=10
```

Параметры запроса:
- `user_id` (опционально) - Фильтр по ID пользователя
- `type` (опционально) - Фильтр по типу: `income` (доход) или `expense` (расход)
- `category` (опционально) - Фильтр по категории
- `page` (опционально, по умолчанию: 1) - Номер страницы
- `page_size` (опционально, по умолчанию: 10, макс: 100) - Количество элементов на странице

#### Обновить транзакцию
```bash
PUT /api/v1/transactions/{id}
Content-Type: application/json

{
  "amount": 150.75,
  "category": "food",
  "description": "Обновленное описание",
  "type": "expense"
}
```

#### Удалить транзакцию
```bash
DELETE /api/v1/transactions/{id}
```

## Docker команды

### Запустить сервисы
```bash
docker-compose up --build
```

### Запустить в фоновом режиме
```bash
docker-compose up -d --build
```

### Просмотр логов
```bash
# Все сервисы
docker-compose logs -f

# Конкретный сервис
docker-compose logs -f auth
docker-compose logs -f transactions
docker-compose logs -f ml-api
docker-compose logs -f postgres
```

### Остановить сервисы
```bash
docker-compose down
```

### Остановить и удалить volumes (сбросить базу данных)
```bash
docker-compose down -v
```

### Перезапустить конкретный сервис
```bash
docker-compose restart auth
docker-compose restart transactions
docker-compose restart ml-api
```

### Пересобрать только один сервис
```bash
docker-compose up -d --build auth
docker-compose up -d --build transactions
docker-compose up -d --build ml-api
```

## Структура проекта

```
FinWise-AI/
├── backend/
│   ├── auth/                     # Сервис аутентификации (порт 8082)
│   │   ├── cmd/app/main.go      # Точка входа
│   │   ├── internal/
│   │   │   ├── config/          # Конфигурация
│   │   │   ├── handlers/        # HTTP обработчики
│   │   │   ├── manager/         # JWT и auth менеджеры
│   │   │   ├── models/          # Модели данных
│   │   │   └── storage/         # Репозитории БД
│   │   ├── migrations/          # SQL миграции auth
│   │   ├── keys/                # RSA ключи для JWT
│   │   └── Dockerfile
│   ├── transactions/            # Сервис транзакций (порт 8080)
│   │   ├── cmd/app/main.go      # Точка входа
│   │   ├── internal/
│   │   │   ├── config/          # Конфигурация
│   │   │   ├── handlers/        # HTTP обработчики
│   │   │   ├── middleware/      # JWT валидация
│   │   │   ├── models/          # Модели данных
│   │   │   ├── service/         # Бизнес-логика
│   │   │   └── storage/         # Репозитории БД
│   │   ├── migrations/          # SQL миграции transactions
│   │   ├── keys/                # Публичный ключ для JWT
│   │   ├── docs/                # Swagger документация
│   │   └── Dockerfile
│   ├── ml-api/                  # ML API сервис (порт 8081)
│   │   ├── cmd/app/main.go      # Точка входа
│   │   ├── internal/
│   │   │   ├── config/          # Конфигурация
│   │   │   ├── handlers/        # HTTP обработчики
│   │   │   ├── models/          # Модели данных
│   │   │   ├── service/         # Бизнес-логика
│   │   │   └── ml-launcher/     # Запуск Python моделей
│   │   ├── python/              # ML модели (в разработке)
│   │   └── Dockerfile
│   ├── go.mod                   # Общие зависимости Go
│   └── go.sum
├── docker-compose.yml           # Конфигурация всех сервисов
└── README.md                    # Этот файл
```

## Архитектура

Проект построен как **микросервисная архитектура** с тремя независимыми сервисами:

### Сервисы

1. **Auth Service (8082)** - Управление пользователями и JWT токенами
   - Регистрация и вход пользователей
   - Генерация JWT токенов с RSA256 подписью
   - Refresh token механизм
   - Хранение refresh токенов в PostgreSQL

2. **Transactions Service (8080)** - Управление финансовыми транзакциями
   - CRUD операции с транзакциями
   - Фильтрация и пагинация
   - JWT авторизация через middleware
   - Swagger документация

3. **ML-API Service (8081)** - Машинное обучение и аналитика
   - Прогнозирование финансов
   - Финансовые советы
   - Интеграция с Python моделями (в разработке)

### Clean Architecture

Каждый сервис следует принципам **Clean Architecture**:

1. **Слой Handlers** (`internal/handlers/`) - Обработка HTTP запросов, валидация входных данных
2. **Слой Service/Manager** (`internal/service/`, `internal/manager/`) - Бизнес-логика, валидация данных
3. **Слой Storage** (`internal/storage/`) - Операции с базой данных, SQL запросы

### JWT Authentication Flow

```
1. Пользователь → POST /auth/register → Auth Service
   ↓
2. Auth Service создает пользователя в БД

3. Пользователь → POST /auth/login → Auth Service
   ↓
4. Auth Service генерирует:
   - Access Token (JWT, RSA256, 15 мин)
   - Refresh Token (UUID, 30 дней)
   ↓
5. Пользователь получает оба токена

6. Пользователь → GET /transactions (с Authorization: Bearer TOKEN) → Transactions Service
   ↓
7. Transactions Service валидирует JWT с публичным ключом
   ↓
8. Если токен валиден → обрабатывает запрос
   Если токен истек → 401 Unauthorized
   ↓
9. Пользователь → POST /auth/refresh → Auth Service (обновление токена)
```

### Ключевые особенности

- Микросервисная архитектура с независимыми сервисами
- JWT аутентификация с RSA256 (асимметричное шифрование)
- RESTful API на фреймворке Gin
- Структурированное логирование с `slog`
- Автоматические миграции базы данных
- Graceful shutdown (корректное завершение работы)
- Docker контейнеризация
- Health check эндпоинты
- Валидация запросов с `go-playground/validator`
- Паттерн Repository для абстракции базы данных

## Разработка

### Локальная разработка (без Docker)

Требования:
- Go 1.24+
- PostgreSQL 16+

1. Установите зависимости:
```bash
cd backend
go mod download
```

2. Настройте PostgreSQL и выполните миграции вручную

3. Запустите приложение:
```bash
cd backend
go run cmd/app/main.go
```

## Реализованные функции

- ✅ JWT аутентификация и авторизация (RSA256)
- ✅ Регистрация и вход пользователей
- ✅ Refresh token механизм
- ✅ Микросервисная архитектура (auth, transactions, ml-api)
- ✅ Docker контейнеризация
- ✅ Автоматические миграции БД

## Планируемые функции

- Foreign key ограничения между пользователями и транзакциями
- API документация со Swagger для всех сервисов
- Unit и интеграционные тесты
- CI/CD pipeline
- ML модели для прогнозирования и финансовых советов

## Лицензия

[Добавьте вашу лицензию здесь]

## Контрибуция

[Добавьте правила для контрибуторов здесь]
