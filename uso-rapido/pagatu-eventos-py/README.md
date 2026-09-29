# pagatu-eventos-py

Caso de uso rápido en Python para probar Kafka con un producer y un consumer
simples — verifica el flujo pub/sub y el contrato real de `orden.creada` sin
depender de Java ni de Spring Boot.

## Requisitos

- Kafka corriendo (`kafka/`, ver su propio README) — este contenedor se une a
  la red externa `pagatu-kafka-dev-net`, así que falla al levantarse si Kafka
  no está arriba primero.

## Uso

Desde esta carpeta:

```powershell
docker compose up -d --build
```

Contenedor esperado:

```powershell
docker compose ps
```

```text
pagatu-eventos-py
```

Ejecutar el consumer primero (queda escuchando, no termina solo — usa
`Ctrl+C` para salir):

```powershell
docker compose exec pagatu-eventos-py python /app/consumer_ordenes.py
```

En **otra terminal**, ejecutar el producer (publica un evento cada 2
segundos, en bucle infinito — también se detiene con `Ctrl+C`):

```powershell
docker compose exec pagatu-eventos-py python /app/producer_ordenes.py
```

## Qué esperar

El consumer imprime una línea JSON por cada evento recibido, con
`topic`/`partition`/`offset` reales, `latencyMs` (tiempo entre que el
producer publicó y el consumer procesó) y `status: "consumed"`. Si le llega
algo que no es JSON válido o le falta un campo del contrato
(`tipoEvento`/`ordenId`/`total`/`timestamp`), no se cae: lo marca
`status: "invalid"` y expone `rawPayload`/`decodeError` para diagnosticar —
pruébalo publicando texto plano desde Kafka UI (`http://localhost:18085`) al
topic `orden-eventos` mientras el consumer sigue corriendo.

## Configuración

| Variable de entorno | Valor por defecto | Aplica a |
|---|---|---|
| `KAFKA_BOOTSTRAP_SERVERS` | `kafka:9092` | consumer |
| `KAFKA_TOPIC_ORDENES` | `orden-eventos` | consumer |
| `KAFKA_GROUP_ID` | `pagatu-eventos-py-group` | consumer |

El producer no lee variables de entorno — el broker (`kafka:9092`) y el topic
(`orden-eventos`) están fijos en el propio script.

## Conexión

- broker interno: `kafka:9092`
- red Docker: `pagatu-kafka-dev-net`
