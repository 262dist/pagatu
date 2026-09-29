# Kafka

Broker de mensajes de `pagatu` (S8) — el primer componente que hay que
levantar antes de tocar `services/pagatu-orden-ms`, `services/pagatu-pago-ms`
o `uso-rapido/pagatu-eventos-py`, porque los tres dependen de la red Docker
que este `compose-dev.yml` crea.

## Requisitos

- Docker Desktop corriendo.

## Servicios y versiones

| Servicio | Imagen | URL/Puerto (DEV) | URL/Puerto (PROD local) |
|---|---|---|---|
| Kafka broker | `apache/kafka:4.3.1` | interno `kafka:9092`, desde el host `localhost:19092` | interno `pagatu-kafka:9092`, desde el host `localhost:29092` |
| Kafka UI | `ghcr.io/kafbat/kafka-ui:v1.5.0` | `http://localhost:18085` | `http://localhost:28085` |

Las imágenes van con versión fija, no `latest` — quien levante este
`compose-dev.yml` meses después obtiene exactamente lo mismo que se probó al
escribir la guía de S8. `kafka-ui` usa `ghcr.io/kafbat/kafka-ui`, no
`provectuslabs/kafka-ui`: el proyecto original (Provectus) no saca una
versión real hace más de dos años — el mismo equipo continúa el desarrollo
activo bajo Kafbat.

## Uso (DEV)

Desde esta carpeta:

```powershell
docker compose -f compose-dev.yml up -d
```

Desde la raíz del repositorio:

```powershell
docker compose -f kafka/compose-dev.yml up -d
```

Contenedores esperados:

```powershell
docker compose -f compose-dev.yml ps
```

```text
pagatu-kafka-dev
pagatu-kafka-ui-dev
```

## Verificar que está arriba

Abre Kafka UI y confirma que el clúster `pagatu-dev` aparece **conectado**
(punto verde), sin ningún topic todavía — es normal, nadie ha publicado nada
todavía:

```text
http://localhost:18085
```

## Dos direcciones de escucha

El broker anuncia dos direcciones (`KAFKA_ADVERTISED_LISTENERS`): `INTERNAL`
(`kafka:9092`), la que usan otros contenedores de la misma red Docker (Kafka
UI, `uso-rapido/pagatu-eventos-py`); y `EXTERNAL` (`localhost:19092`), la que
usan los servicios Spring Boot que corren en el host con `mvnw`
(`pagatu-orden-ms`, `pagatu-pago-ms`). Detalle completo en la guía de S8
(3.2).

## Topics

`orden-eventos` y `pago-eventos`, ambos con 3 particiones. `KAFKA_AUTO_CREATE_TOPICS_ENABLE`
está en `"false"` a propósito: cada topic se crea explícito, por consola
(guía de S8, 3.3) o por código (`KafkaTopicsConfig` de cada microservicio,
3.14) — nunca aparece solo la primera vez que alguien publica en un nombre
nuevo, para no ocultar un typo detrás de un topic "fantasma" con una sola
partición por defecto.

## Red compartida

```text
pagatu-kafka-dev-net
```

`services/pagatu-orden-ms`, `services/pagatu-pago-ms` (por `bootstrap-servers:
localhost:19092`, la dirección `EXTERNAL`) y `uso-rapido/pagatu-eventos-py`
(como red *externa*, `networks: - pagatu-kafka-dev-net` con `external: true`)
dependen de este `compose-dev.yml`. Por eso Kafka **siempre tiene que estar
arriba primero**: si intentas levantar `uso-rapido/pagatu-eventos-py` sin
haber corrido este archivo antes, Docker Compose falla con un error de red
no encontrada.

## Producción local

`compose.yml` (mismo criterio de puertos `2xxxx` que el resto del proyecto)
se une a `pagatu-prod-net`, creada por `infra/compose.yml`, y anuncia el
broker por el nombre del contenedor, `pagatu-kafka:9092`. Hoy no se levanta:
queda lista para cuando el proyecto despliegue en PROD local.
