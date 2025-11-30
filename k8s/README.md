# Kubernetes деплоймент для FinWise-AI

Эта директория содержит Kubernetes манифесты для деплоя backend сервисов FinWise-AI.

## Архитектура

Деплоймент включает:
- **PostgreSQL** (StatefulSet) - База данных с persistent storage
- **Auth Service** (Deployment) - JWT аутентификация и управление пользователями
- **Transactions Service** (Deployment) - API финансовых транзакций
- **ML-API Service** (Deployment) - Machine learning прогнозирование и советы
- **Prometheus** (Deployment) - Сбор метрик и мониторинг
- **Migration Jobs** - Инициализация схемы БД

## Предварительные требования

1. **Kubernetes кластер** (локальный или облачный)
   - Minikube: `minikube start`
   - Kind: `kind create cluster`
   - Облако: GKE, EKS, AKS, и т.д.

2. **kubectl** настроенный для доступа к кластеру
   ```bash
   kubectl cluster-info
   ```

3. **Docker образы** собраны и доступны
   ```bash
   # Сборка образов (из корня проекта)
   docker build -t finwise-auth:latest -f backend/auth/Dockerfile backend
   docker build -t finwise-transactions:latest -f backend/transactions/Dockerfile backend
   docker build -t finwise-ml-api:latest -f backend/ml-api/Dockerfile backend

   # Для Minikube загрузить образы в кластер
   minikube image load finwise-auth:latest
   minikube image load finwise-transactions:latest
   minikube image load finwise-ml-api:latest
   ```

4. **JWT ключи** - Сгенерировать и создать secret
   ```bash
   cd backend/auth/keys
   ./generate_keys.sh

   # Создать secret с JWT ключами
   kubectl create secret generic jwt-keys \
     --from-file=private.pem=backend/auth/keys/private.pem \
     --from-file=public.pem=backend/auth/keys/public.pem \
     -n finwise
   ```

## Шаги деплоймента

### Вариант 1: Использование Kustomize (Рекомендуется)

```bash
# Задеплоить всё сразу
kubectl apply -k k8s/

# Или использовать kustomize напрямую
kustomize build k8s/ | kubectl apply -f -
```

### Вариант 2: Ручной деплоймент

Деплой в порядке:

```bash
# 1. Namespace и Secrets
kubectl apply -f k8s/base/namespace.yaml
kubectl apply -f k8s/secrets/postgres-secret.yaml

# Создать secret с JWT ключами (см. Предварительные требования)
kubectl create secret generic jwt-keys \
  --from-file=private.pem=backend/auth/keys/private.pem \
  --from-file=public.pem=backend/auth/keys/public.pem \
  -n finwise

# 2. ConfigMaps и база данных
kubectl apply -f k8s/base/configmap.yaml
kubectl apply -f k8s/base/postgres.yaml

# Дождаться готовности PostgreSQL
kubectl wait --for=condition=ready pod -l app=postgres -n finwise --timeout=300s

# 3. Запустить миграции
kubectl apply -f k8s/jobs/migrate-auth.yaml
kubectl wait --for=condition=complete job/migrate-auth -n finwise --timeout=300s

kubectl apply -f k8s/jobs/migrate-transactions.yaml
kubectl wait --for=condition=complete job/migrate-transactions -n finwise --timeout=300s

# 4. Задеплоить сервисы
kubectl apply -f k8s/base/auth.yaml
kubectl apply -f k8s/base/transactions.yaml
kubectl apply -f k8s/base/ml-api.yaml

# 5. Задеплоить мониторинг
kubectl apply -f k8s/base/prometheus.yaml
```

### Вариант 3: Использование скрипта деплоя

```bash
./k8s/deploy.sh
```

## Доступ к сервисам

### Port Forwarding (для локальной разработки)

```bash
# Auth сервис
kubectl port-forward -n finwise svc/auth 8082:8082

# Transactions сервис
kubectl port-forward -n finwise svc/transactions 8080:8080

# ML-API сервис
kubectl port-forward -n finwise svc/ml-api 8081:8081

# Prometheus UI
kubectl port-forward -n finwise svc/prometheus 9090:9090
```

### Использование Ingress (для production)

Создайте Ingress ресурс для внешнего доступа к сервисам.

## Проверка деплоймента

```bash
# Проверить все ресурсы
kubectl get all -n finwise

# Проверить статус подов
kubectl get pods -n finwise

# Проверить сервисы
kubectl get svc -n finwise

# Проверить миграции
kubectl get jobs -n finwise

# Посмотреть логи
kubectl logs -n finwise -l app=auth
kubectl logs -n finwise -l app=transactions
kubectl logs -n finwise job/migrate-auth
```

## Health Check'и

```bash
# Auth сервис
curl http://localhost:8082/health

# Transactions сервис
curl http://localhost:8080/health

# ML-API сервис
curl http://localhost:8081/health

# Prometheus
curl http://localhost:9090/-/healthy
```

## Метрики

Доступ к Prometheus UI: http://localhost:9090 (после port-forward)

Ключевые метрики:
- `auth_service_http_requests_total`
- `transactions_service_http_requests_total`
- `auth_service_http_request_duration_seconds`
- `transactions_service_http_request_duration_seconds`

## Конфигурация

### Переменные окружения

Отредактируйте `k8s/base/configmap.yaml` для изменения:
- Настроек базы данных
- JWT token TTL
- Уровней логирования

### Секреты

Отредактируйте или пересоздайте секреты:
```bash
# PostgreSQL учетные данные
kubectl create secret generic postgres-secret \
  --from-literal=DB_USER=postgres \
  --from-literal=DB_PASSWORD=newpassword \
  --dry-run=client -o yaml | kubectl apply -f -

# JWT ключи (см. Предварительные требования)
```

### Масштабирование

```bash
# Масштабировать auth сервис
kubectl scale deployment auth -n finwise --replicas=3

# Масштабировать transactions сервис
kubectl scale deployment transactions -n finwise --replicas=3
```

## Устранение неполадок

### Поды не запускаются
```bash
kubectl describe pod -n finwise <pod-name>
kubectl logs -n finwise <pod-name>
```

### Проблемы с подключением к БД
```bash
# Проверить под postgres
kubectl logs -n finwise postgres-0

# Проверить подключение из пода
kubectl exec -n finwise -it <pod-name> -- sh
# Внутри пода:
apk add postgresql-client
psql -h postgres -U postgres -d finwise
```

### Ошибки миграций
```bash
# Проверить логи job'ов миграций
kubectl logs -n finwise job/migrate-auth
kubectl logs -n finwise job/migrate-transactions

# Перезапустить миграции
kubectl delete job -n finwise migrate-auth migrate-transactions
kubectl apply -f k8s/jobs/
```

### Ошибки загрузки образов
```bash
# Для Minikube убедиться что образы загружены
minikube image ls | grep finwise

# Перезагрузить если отсутствуют
minikube image load finwise-auth:latest
```

## Очистка

```bash
# Удалить всё
kubectl delete namespace finwise

# Или использовать kustomize
kubectl delete -k k8s/

# Удалить PVC вручную если нужно
kubectl delete pvc -n finwise --all
```

## Рекомендации для production

Перед деплоем в production:

1. **Обновить секреты**
   - Использовать сильные пароли для PostgreSQL
   - Сгенерировать production JWT ключи
   - Хранить секреты в системе управления секретами (Vault, AWS Secrets Manager, и т.д.)

2. **Лимиты ресурсов**
   - Добавить resource requests и limits для всех контейнеров
   - Настроить HPA (Horizontal Pod Autoscaler)

3. **Persistent Storage**
   - Использовать подходящий StorageClass для вашего окружения
   - Настроить стратегию бэкапов для PostgreSQL

4. **Сеть**
   - Настроить Ingress с TLS
   - Настроить NetworkPolicies
   - Использовать правильные типы сервисов (ClusterIP, LoadBalancer, и т.д.)

5. **Мониторинг**
   - Настроить дашборды Grafana
   - Настроить правила алертинга
   - Включить persistent storage для Prometheus

6. **Безопасность**
   - Запускать контейнеры от non-root пользователя
   - Использовать Pod Security Standards
   - Включить RBAC
   - Сканировать образы на уязвимости

7. **Высокая доступность**
   - Запустить PostgreSQL в HA режиме (StatefulSet с репликацией)
   - Использовать несколько реплик для сервисов
   - Настроить pod disruption budgets

## Структура директории

```
k8s/
├── README.md                          # Этот файл
├── kustomization.yaml                 # Kustomize конфигурация
├── deploy.sh                          # Скрипт автоматического деплоя
├── base/                              # Базовые ресурсы
│   ├── namespace.yaml                 # Определение namespace
│   ├── configmap.yaml                 # Общая конфигурация
│   ├── postgres.yaml                  # PostgreSQL StatefulSet
│   ├── auth.yaml                      # Auth сервис
│   ├── transactions.yaml              # Transactions сервис
│   ├── ml-api.yaml                    # ML-API сервис
│   └── prometheus.yaml                # Prometheus мониторинг
├── secrets/                           # Секреты (НЕ коммитить реальные значения)
│   ├── postgres-secret.yaml           # Учетные данные БД
│   └── jwt-keys-secret.yaml           # JWT ключи (только template)
└── jobs/                              # Одноразовые задачи
    ├── migrate-auth.yaml              # Auth миграции БД
    └── migrate-transactions.yaml      # Transactions миграции БД
```
