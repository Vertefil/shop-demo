# Shop Demo — Микросервисная архитектура

Демо-проект интернет-магазина для демонстрации микросервисной архитектуры
с использованием Java, Spring Boot и Apache Kafka.

## Стек технологий

- **Java 17**
- **Spring Boot 3.5.11**
- **Spring Cloud Gateway** — API Gateway
- **Apache Kafka** — асинхронная коммуникация между сервисами
- **PostgreSQL** — БД для order, inventory, payment сервисов
- **H2** — встроенная БД для user-service
- **Docker + Docker Compose** — инфраструктура
- **Lombok** — уменьшение boilerplate кода

## Архитектура
```
[Client / Postman]
        │
        ▼
[API Gateway :8080]
        │
   ┌────┼────┬──────────┐
   ▼    ▼    ▼          ▼
[User] [Order] [Inventory] [Payment]
:8081  :8082    :8083       :8084
                │
          Kafka Bus
                │
   ┌────────────┼────────────┐
   ▼            ▼            ▼
[Inventory] [Payment] [Notification]
 consumer    consumer    :8085
```

## Сервисы

| Сервис | Порт | БД | Описание |
|---|---|---|---|
| api-gateway | 8080 | — | Единая точка входа, роутинг запросов |
| user-service | 8081 | H2 | Регистрация и управление пользователями |
| order-service | 8082 | PostgreSQL | Создание и управление заказами |
| inventory-service | 8083 | PostgreSQL | Управление товарами и резервирование |
| payment-service | 8084 | PostgreSQL | Обработка платежей |
| notification-service | 8085 | — | Уведомления о событиях |
| shared-events | — | — | Общие Kafka события между сервисами |

## Kafka Topics

| Топик | Producer | Consumer | Описание |
|---|---|---|---|
| order.created | order-service | inventory-service, notification-service | Новый заказ создан |
| inventory.reserved | inventory-service | payment-service | Товар успешно зарезервирован |
| order.cancelled | inventory-service | order-service, notification-service | Заказ отменён (нет товара) |
| payment.processed | payment-service | order-service, notification-service | Результат обработки платежа |

## Flow событий

### Successful Path (успешный заказ)
```
POST /api/orders
      │
      ▼
order-service ──► order.created ──► inventory-service (резервирует товар)
                                          │
                                    inventory.reserved
                                          │
                                          ▼
                                    payment-service (80% success)
                                          │
                                    payment.processed (SUCCESS)
                                          │
                              ┌───────────┴───────────┐
                              ▼                       ▼
                        order-service           notification-service
                        (CONFIRMED)             (уведомление УСПЕШНО)
```

### Failure Path (нет товара)
```
POST /api/orders
      │
      ▼
order-service ──► order.created ──► inventory-service (нет товара)
                                          │
                                    order.cancelled
                                          │
                              ┌───────────┴───────────┐
                              ▼                       ▼
                        order-service           notification-service
                        (CANCELLED)             (уведомление ОТМЕНА)
```

### Failure Path (оплата отклонена)
```
inventory.reserved ──► payment-service (20% fail)
                              │
                        payment.processed (FAILED)
                              │
                  ┌───────────┴───────────┐
                  ▼                       ▼
            order-service           notification-service
            (CANCELLED)             (уведомление ОПЛАТА НЕ ПРОШЛА)
```

## Запуск проекта

### 1. Запустить инфраструктуру
```bash
docker compose up -d
```

### 2. Запустить сервисы в IDEA
Порядок запуска важен:
1. `inventory-service`
2. `order-service`
3. `payment-service`
4. `notification-service`
5. `user-service`
6. `api-gateway`

***Либо сделать файл конфигурации и запускать всё вместе***

### 3. Запросы предоставлены в терминале, можно использовать POSTMAN
```bash
# Kafka UI
http://localhost:8090

# Создать пользователя
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"ivan","email":"ivan@test.com","password":"123456"}'

# Создать товар
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Ноутбук","quantity":10,"price":999.99}'

# Создать заказ (запускает весь Kafka flow)
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"productId":1,"quantity":2,"totalPrice":1999.98}'

# Проверить статус заказа
curl http://localhost:8080/api/orders/1
```

## Плюсы микросервисной архитектуры

- **Независимый деплой** — каждый сервис деплоится отдельно
- **Масштабируемость** — можно запустить несколько инстансов inventory-service
- **Изоляция ошибок** — падение одного сервиса не роняет всю систему
- **Независимые БД** — каждый сервис владеет своими данными

## Роль Kafka

- **Асинхронность** — сервисы не ждут ответа друг от друга
- **Слабая связность** — сервисы знают только о топиках, не друг о друге
- **Надёжность** — сообщения хранятся в топике, не теряются при падении сервиса
- **Saga Pattern** — распределённые транзакции без единой БД
- **Масштабирование** — consumer groups автоматически балансируют нагрузку