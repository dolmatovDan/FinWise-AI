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
- **Сервер**: `localhost:8080`
- **База данных**: PostgreSQL на порту `5432`
- **Уровень логирования**: `info`

### 3. Запустите приложение

```bash
docker-compose up --build
```

Это действие:
1. Запустит контейнер PostgreSQL
2. Автоматически выполнит миграции базы данных
3. Запустит backend API сервис

API будет доступно по адресу `http://localhost:8080`

### 4. Проверьте работу сервиса

```bash
curl http://localhost:8080/health
```

Ожидаемый ответ:
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

# Только backend
docker-compose logs -f backend

# Только PostgreSQL
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
docker-compose restart backend
```

## Структура проекта

```
FinWise-AI/
├── backend/
│   ├── cmd/
│   │   └── app/
│   │       └── main.go           # Точка входа в приложение
│   ├── internal/
│   │   ├── config/               # Управление конфигурацией
│   │   ├── handlers/             # HTTP обработчики (контроллеры)
│   │   ├── models/               # Модели данных
│   │   ├── service/              # Слой бизнес-логики
│   │   └── storage/              # Слой базы данных
│   │       └── postgres/         # Реализация для PostgreSQL
│   ├── migrations/               # SQL миграции
│   ├── Dockerfile                # Docker образ backend
│   ├── go.mod                    # Зависимости Go
│   └── go.sum
├── docker-compose.yml            # Конфигурация Docker Compose
├── .env.example                  # Шаблон переменных окружения
└── README.md                     # Этот файл
```

## Архитектура

Сервис следует принципам **Clean Architecture**:

1. **Слой Handlers** (`internal/handlers/`) - Обработка HTTP запросов, валидация входных данных
2. **Слой Service** (`internal/service/`) - Бизнес-логика, валидация данных
3. **Слой Storage** (`internal/storage/`) - Операции с базой данных, SQL запросы

### Ключевые особенности

- RESTful API на фреймворке Gin
- Структурированное логирование с `slog`
- Автоматические миграции базы данных
- Graceful shutdown (корректное завершение работы)
- Docker контейнеризация
- Health check эндпоинт
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

## Планируемые функции

- JWT аутентификация и авторизация
- Регистрация и вход пользователей
- Foreign key ограничения между пользователями и транзакциями
- API документация со Swagger
- Unit и интеграционные тесты
- CI/CD pipeline

## Лицензия

[Добавьте вашу лицензию здесь]

## Контрибуция

[Добавьте правила для контрибуторов здесь]
