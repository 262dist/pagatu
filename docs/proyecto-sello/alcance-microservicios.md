# Alcance por microservicio y proyecto base de `pagatu-orden-ms` y `pagatu-cliente-ms`

Este documento delimita, por escrito, qué le corresponde a cada microservicio de `pagatu` a lo largo de **todo el curso** — no es el contenido de una sola sesión. Sirve para que, en cualquier momento del semestre, cualquier integrante del equipo pueda ubicar qué historia de usuario pertenece a qué microservicio y en qué sesión se construye, sin tener que revisar guía por guía.

No sustituye ninguna guía de sesión ya entregada (S1, S2): las historias marcadas como "ya entregado" o con una sesión concreta se implementan cuando esa sesión llega, siguiendo su propia guía — este documento solo ubica el mapa completo.

**Error frecuente al crear un proyecto nuevo copiando de otro:** al generar `pagatu-orden-ms`/`pagatu-cliente-ms` con Spring Initializr, es tentador copiar archivos ya hechos de `pagatu-catalogo-ms` (`.env`, `compose.yml`, `application-dev.yml`, `ResourceNotFoundException`, `CorrelationIdFilter`, etc.) para no escribirlos de cero — es una práctica válida, pero cada archivo copiado hay que revisarlo con cuidado: nombre de base de datos, puertos, nombre de contenedor y, sobre todo, la línea `package` de cada clase Java (debe decir `pe.edu.upeu.orden...` o `pe.edu.upeu.cliente...`, no `pe.edu.upeu.catalogo...`) — si el `package` no coincide con la carpeta real del archivo, el proyecto no compila.

## Alcance de `pagatu-catalogo-ms`, por sesión

**S1 (ya entregado) — CRUD básico**

1. Como cliente, quiero consultar el catálogo de categorías y productos disponibles, para elegir qué comprar.
2. Como administrador, quiero crear, actualizar y eliminar categorías y productos, para mantener el catálogo actualizado.

**S3 — descubrimiento de servicios**

3. Como sistema, quiero que `pagatu-catalogo-ms` se registre automáticamente en Eureka al arrancar, para que otros servicios (Gateway, `orden-ms`) lo encuentren sin conocer su dirección de antemano.

**S4 — punto único de acceso**

4. Como cliente, quiero acceder al catálogo a través de un único punto de entrada (Gateway), sin necesitar conocer la URL directa de `pagatu-catalogo-ms`.
5. Como sistema, quiero que el tráfico se reparta entre varias instancias de `pagatu-catalogo-ms`, para soportar más carga sin caerse.

**S6 — comunicación resiliente (consumido por `orden-ms`)**

6. Como sistema (`orden-ms`), quiero consultar un producto por id en `pagatu-catalogo-ms` de forma confiable, para validar que existe y su precio antes de crear una orden.

**S7 — seguridad**

7. Como administrador autenticado, quiero ser el único que puede crear, editar o eliminar categorías y productos, para que usuarios sin permiso no alteren el catálogo.
8. Como cliente, quiero poder consultar el catálogo sin restricciones, para explorar productos libremente.

**S9 — consistencia distribuida (pendiente de decidir)**

9. Como sistema, quiero descontar stock de un producto cuando se confirma una orden, para no vender más unidades de las disponibles. La columna `stock` ya se agregó a `productos` desde el `V1` (S1) — lo que falta construir en S9 es la operación de descuento segura entre `orden-ms` y `pagatu-catalogo-ms` (`UPDATE ... WHERE stock >= cantidad`, idempotencia, compensación si la orden se cancela).

**S10 — observabilidad**

10. Como equipo de operaciones, quiero ver métricas, logs y el estado de salud de `pagatu-catalogo-ms` en un panel, para detectar problemas antes de que afecten a los clientes.

**S11 — integración frontend**

11. Como cliente, quiero navegar el catálogo desde la aplicación web, para explorar y elegir productos antes de comprar.

## Alcance de `pagatu-orden-ms`, por sesión

**Pendiente de construir — CRUD básico**

1. Como cliente, quiero crear una orden con los productos que quiero comprar (cantidad y precio de cada uno), para registrar mi intención de compra.
2. Como cliente, quiero consultar el detalle de una orden por su id, para verificar qué pedí y cuánto voy a pagar.
3. Como cliente o administrador, quiero listar las órdenes existentes, para revisar el historial de pedidos.
4. Como administrador, quiero cambiar el estado de una orden (confirmar o cancelar), para reflejar su avance real.

**S6 — comunicación resiliente**

5. Como sistema, quiero validar que cada producto de la orden exista y tenga el precio vigente en `pagatu-catalogo-ms` antes de crearla, para no aceptar pedidos con productos inexistentes o precios desactualizados.
6. Como sistema, quiero responder con un mensaje claro (no un error genérico) si `pagatu-catalogo-ms` no responde, para no dejar al cliente esperando indefinidamente (Circuit Breaker).

**S7 — seguridad**

7. Como cliente autenticado, quiero que solo yo pueda ver mis propias órdenes, para que otros usuarios no accedan a mi información.
8. Como administrador, quiero ver y gestionar todas las órdenes del sistema, para operar el negocio.

`ordenes.id_cliente` ya nace como `BIGINT`, sin `REFERENCES` — apunta a `pagatu-cliente-ms` (un microservicio con su propia base de datos, ver [`docs/index.md`](../index.md)), igual que `id_producto` no referencia a `pagatu-catalogo-ms`. Mientras no exista login, se completa con un valor de prueba; desde S7 se poblará con el id que venga del JWT ya validado, en vez de confiar en lo que el cliente mande en el request. `pagatu-cliente-ms` guarda el perfil del cliente (DNI/RUC, nombre o razón social) y lo autocompleta consultando RENIEC o SUNAT según el tipo de documento — ver [`docs/index.md`](../index.md), tabla de U2 (fila S7) y diagrama C4 nivel 2.

**S8 — mensajería asíncrona**

9. Como sistema, quiero publicar un evento cuando se crea una orden, para que `pago-ms` se entere y procese el cobro sin que `orden-ms` tenga que llamarlo directamente.

**S9 — consistencia distribuida**

10. Como cliente, quiero que si envío la misma solicitud de compra dos veces por error (doble clic, reintento de red), no se creen dos órdenes duplicadas (idempotencia).
11. Como sistema, quiero poder revertir/cancelar una orden automáticamente si el pago asociado falla, para mantener consistencia entre orden y pago (compensación).

**S11 — integración frontend**

12. Como cliente, quiero ver mis órdenes y su estado desde la aplicación web, para hacer seguimiento de mis compras.

## Alcance de `pagatu-cliente-ms`, por sesión

**Pendiente de construir (trabajo autónomo, sin fecha fija de sesión) — CRUD y autocompletado con RENIEC/SUNAT**

1. Como cliente, quiero registrar mi documento de identidad (DNI o RUC), para que el sistema autocomplete mis datos sin que tenga que tipearlos.
2. Como sistema, quiero consultar RENIEC con el DNI de una persona natural, para obtener su nombre completo y autocompletar el perfil.
3. Como sistema, quiero consultar SUNAT con el RUC de una persona jurídica, para obtener su razón social y autocompletar el perfil.
4. Como cliente, quiero consultar mi propio perfil, para verificar que mis datos estén correctos.

**S10 — observabilidad**

5. Como equipo de operaciones, quiero ver métricas, logs y el estado de salud de `pagatu-cliente-ms` en un panel, para detectar problemas (incluidas fallas de RENIEC/SUNAT) antes de que afecten a los clientes.

**S11 — integración frontend**

6. Como cliente, quiero completar mi perfil desde la aplicación web con solo mi DNI o RUC, para no llenar el formulario a mano.

Las historias 1-4 de `pagatu-cliente-ms` no tienen una sesión de guía dedicada todavía — se construyen como trabajo autónomo, en el mismo momento que el equipo decida levantar `pagatu-orden-ms` (ambos microservicios nuevos se scaffoldan juntos, siguiendo la sección siguiente). Las de S10 y S11 quedan como delimitación de alcance para más adelante, sin implementarse todavía.

## Crear el proyecto base de `pagatu-orden-ms`

**Tabla 1. Configuración del proyecto `pagatu-orden-ms` en Spring Initializr**

| Campo | Valor |
|---|---|
| Project | Maven Project |
| Spring Boot | **4.0.7** |
| Language | Java |
| Group Id | `pe.edu.upeu` |
| Artifact Id | `pagatu-orden-ms` |
| Package name | `pe.edu.upeu.orden` |
| Packaging | Jar |
| Java | 21 |
| Dependencias | Las mismas de `pagatu-catalogo-ms` (S1, Tabla 4): Spring Web, Validation, Lombok, Spring Boot DevTools, SpringDoc OpenAPI WebMvc UI, Spring Boot Actuator, Spring Data JPA, PostgreSQL Driver, Flyway. **Además**, agrega MapStruct a mano en el `pom.xml` (S1, 3.5.20) — Spring Initializr no lo ofrece como opción, y sin él el proyecto no compila apenas escribas el primer `Mapper`. |
| Ubicación sugerente | `services/pagatu-orden-ms` |

El puerto de base de datos (`15434` DEV / `25434` PROD local) sigue la misma numeración ya reservada para `orden_db` en la arquitectura del proyecto ([`docs/index.md`](../index.md)), distinta de `pagatu_catalogo_db` (`15432`/`25432`) para que ambos puedan correr al mismo tiempo. El puerto de aplicación en DEV (`8082`, fijo) sigue el mismo criterio de S1 (puerto fijo, sin argumento) — distinto de `8080`, que ya usa `pagatu-catalogo-ms`.

**`services/pagatu-orden-ms/compose-dev.yml`**

```yaml
name: pagatu-orden-dev

services:
  postgres-orden-dev:
    image: postgres:16-alpine
    container_name: pagatu-postgres-orden-dev
    restart: unless-stopped
    environment:
      POSTGRES_DB: pagatu_orden_db
      POSTGRES_USER: pagatu
      POSTGRES_PASSWORD: pagatu
    ports:
      - "15434:5432"
    volumes:
      - pagatu_orden_dev_data:/var/lib/postgresql/data

volumes:
  pagatu_orden_dev_data:
```

**`services/pagatu-orden-ms/src/main/resources/application.yml`**

```yaml
spring:
  application:
    name: pagatu-orden-ms
  profiles:
    active: dev
```

**`services/pagatu-orden-ms/src/main/resources/application-dev.yml`**

```yaml
server:
  port: 8082

spring:
  datasource:
    url: jdbc:postgresql://localhost:15434/pagatu_orden_db
    username: pagatu
    password: pagatu
    driver-class-name: org.postgresql.Driver
  flyway:
    enabled: true
    locations: classpath:db/migration
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  devtools:
    restart:
      enabled: true
    livereload:
      enabled: true

springdoc:
  swagger-ui:
    path: /swagger-ui.html

logging:
  level:
    pe.edu.upeu.orden: DEBUG

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

**`services/pagatu-orden-ms/src/main/resources/db/migration/V1__create_orden_tables.sql`**

```sql
CREATE TABLE IF NOT EXISTS ordenes (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    id_cliente BIGINT NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT now(),
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    tipo_comprobante VARCHAR(20) NOT NULL DEFAULT 'BOLETA_SIMPLE',
    metodo_pago VARCHAR(20) NOT NULL,
    momento_pago VARCHAR(20) NOT NULL DEFAULT 'ADELANTADO',
    total NUMERIC(10,2) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS orden_detalles (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    id_orden BIGINT NOT NULL REFERENCES ordenes(id),
    id_producto BIGINT NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario NUMERIC(10,2) NOT NULL,
    PRIMARY KEY (id)
);
```

`id_orden` sí es una llave foránea normal (`REFERENCES ordenes(id)`), porque `ordenes` y `orden_detalles` viven en la misma base de datos de `pagatu-orden-ms`. `id_producto` e `id_cliente`, en cambio, **no** llevan `REFERENCES` — el producto vive en la base de datos de `pagatu-catalogo-ms` y el cliente en la de `pagatu-cliente-ms` (otro microservicio, con su propia base de datos), cada uno un microservicio distinto; validar que existan es responsabilidad del código (una consulta HTTP al microservicio correspondiente), no de una llave foránea entre bases de datos separadas. `id_cliente` se completa con el id de un cliente ya registrado en `pagatu-cliente-ms` (probado a mano, sin login todavía); recién desde S7 ese valor se derivará automáticamente del JWT en vez de venir en el request.

## Crear el proyecto base de `pagatu-cliente-ms`

**Tabla 2. Configuración del proyecto `pagatu-cliente-ms` en Spring Initializr**

| Campo | Valor |
|---|---|
| Project | Maven Project |
| Spring Boot | **4.0.7** |
| Language | Java |
| Group Id | `pe.edu.upeu` |
| Artifact Id | `pagatu-cliente-ms` |
| Package name | `pe.edu.upeu.cliente` |
| Packaging | Jar |
| Java | 21 |
| Dependencias | Las mismas de `pagatu-catalogo-ms` (S1, Tabla 4): Spring Web, Validation, Lombok, Spring Boot DevTools, SpringDoc OpenAPI WebMvc UI, Spring Boot Actuator, Spring Data JPA, PostgreSQL Driver, Flyway. **Además**, agrega MapStruct a mano en el `pom.xml` (S1, 3.5.20) — Spring Initializr no lo ofrece como opción, y sin él el proyecto no compila apenas escribas el primer `Mapper`. |
| Ubicación sugerente | `services/pagatu-cliente-ms` |

El puerto de base de datos (`15433` DEV / `25433` PROD local) es el que ya estaba reservado para `cliente_db` en la arquitectura del proyecto ([`docs/index.md`](../index.md)) — el hueco entre `auth_db` (`15431`) y `orden_db` (`15434`). El puerto de aplicación en DEV es `8084`, fijo, distinto de `8080` (`pagatu-catalogo-ms`) y `8082` (`pagatu-orden-ms`); se deja `8081` y `8083` sin usar, por si se necesitan para segundas instancias o para `auth-ms`.

**`services/pagatu-cliente-ms/compose-dev.yml`**

```yaml
name: pagatu-cliente-dev

services:
  postgres-cliente-dev:
    image: postgres:16-alpine
    container_name: pagatu-postgres-cliente-dev
    restart: unless-stopped
    environment:
      POSTGRES_DB: pagatu_cliente_db
      POSTGRES_USER: pagatu
      POSTGRES_PASSWORD: pagatu
    ports:
      - "15433:5432"
    volumes:
      - pagatu_cliente_dev_data:/var/lib/postgresql/data

volumes:
  pagatu_cliente_dev_data:
```

**`services/pagatu-cliente-ms/src/main/resources/application.yml`**

```yaml
spring:
  application:
    name: pagatu-cliente-ms
  profiles:
    active: dev
```

**`services/pagatu-cliente-ms/src/main/resources/application-dev.yml`**

```yaml
server:
  port: 8084

spring:
  datasource:
    url: jdbc:postgresql://localhost:15433/pagatu_cliente_db
    username: pagatu
    password: pagatu
    driver-class-name: org.postgresql.Driver
  flyway:
    enabled: true
    locations: classpath:db/migration
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  devtools:
    restart:
      enabled: true
    livereload:
      enabled: true

springdoc:
  swagger-ui:
    path: /swagger-ui.html

logging:
  level:
    pe.edu.upeu.cliente: DEBUG

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

**`services/pagatu-cliente-ms/src/main/resources/db/migration/V1__create_cliente_tables.sql`**

```sql
CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    tipo_persona VARCHAR(10) NOT NULL,
    dni VARCHAR(8),
    ruc VARCHAR(11),
    nombre_completo VARCHAR(150),
    razon_social VARCHAR(150),
    direccion VARCHAR(255),
    email VARCHAR(150),
    whatsapp VARCHAR(20),
    PRIMARY KEY (id),
    UNIQUE (dni),
    UNIQUE (ruc)
);
```

Una persona natural puede tener **solo DNI**, o **DNI y RUC a la vez** (un negocio unipersonal, por ejemplo un independiente que emite factura a su propio nombre) — por eso `dni` y `ruc` son dos columnas separadas y ambas opcionales, en vez del `tipo_documento`/`numero_documento` mutuamente excluyentes de una versión anterior. Una persona jurídica (empresa) solo tiene `ruc` y `razon_social`; no tiene `dni` propio (el DNI de su representante legal es otro dato, fuera de este alcance).

**Tabla 3. Reglas de `tipo_persona` en `clientes`**

| `tipo_persona` | Documentos | Autocompletado | Campo de nombre usado |
|---|---|---|---|
| `NATURAL` | `dni` obligatorio; `ruc` opcional | RENIEC (por `dni`); SUNAT (por `ruc`, si lo tiene) | `nombre_completo` |
| `JURIDICA` | `ruc` obligatorio; `dni` queda `NULL` | SUNAT (por `ruc`) | `razon_social` |

`whatsapp` (opcional, no lo completa RENIEC/SUNAT — lo escribe el cliente) es el número al que se envía el comprobante de cada orden una vez confirmada; su uso real llega recién con la mensajería de S8, cuando `orden-ms` publique el evento correspondiente. `ordenes.id_cliente` apunta al `id` de esta tabla, sin `REFERENCES` entre bases de datos.

**`services/pagatu-cliente-ms/src/main/resources/db/migration/V2__seed_clientes.sql`** (opcional, datos de prueba)

Flyway también migra datos, no solo estructura — un archivo versionado más, con `INSERT` en vez de `CREATE TABLE`. Sirve para no estar registrando clientes a mano cada vez que reinicias la base de datos:

```sql
INSERT INTO clientes (tipo_persona, dni, ruc, nombre_completo, razon_social, direccion, email, whatsapp)
VALUES ('NATURAL', '87654321', NULL, 'Maria Torres Quispe', NULL, 'Av. Los Olivos 123, Lima', 'maria.torres@example.com', '999888777');

INSERT INTO clientes (tipo_persona, dni, ruc, nombre_completo, razon_social, direccion, email, whatsapp)
VALUES ('JURIDICA', NULL, '20123456789', NULL, 'Comercial Andina S.A.C.', 'Jr. Comercio 456, Lima', 'contacto@comercialandina.pe', '988777666');

INSERT INTO clientes (tipo_persona, dni, ruc, nombre_completo, razon_social, direccion, email, whatsapp)
VALUES ('NATURAL', '45678912', '10456789123', 'Jose Ramirez Lopez', NULL, 'Calle Las Flores 789, Arequipa', 'jose.ramirez@example.com', '977666555');

INSERT INTO clientes (tipo_persona, dni, ruc, nombre_completo, razon_social, direccion, email, whatsapp)
VALUES ('JURIDICA', NULL, '20567891234', NULL, 'Distribuidora del Sur E.I.R.L.', 'Av. Ejercito 321, Arequipa', 'ventas@distribuidorasur.pe', '966555444');
```

Este cuarto cliente (`NATURAL` con `dni` **y** `ruc`) es justo el caso de una persona natural con negocio propio, que puede pedir boleta con su DNI o factura con su RUC según la orden.

Igual que `V1`, una vez aplicado no se edita — si necesitas ajustar estos datos, agrega un `V3` nuevo. `pagatu-orden-ms` también tiene su propio `V2__seed_ordenes.sql`, con órdenes de ejemplo referenciando estos `id` de `clientes` y los `id` de `productos` sembrados en `pagatu-catalogo-ms` (S1) — sin llave foránea entre microservicios, así que ese `V2` de `orden-ms` solo funciona si los seeds de `cliente-ms` y `catalogo-ms` ya se aplicaron antes.

## Tipo de comprobante por orden: boleta simple, boleta con DNI o factura

Elegir el comprobante es una decisión **de cada orden**, no del perfil del cliente — la misma persona puede pedir boleta simple una vez y factura la siguiente. Por eso este campo vive en `ordenes` (`pagatu-orden-ms`), no en `clientes`.

**Tabla 4. Tipos de comprobante y qué exigen del cliente**

| `tipo_comprobante` | Requiere | Uso típico |
|---|---|---|
| `BOLETA_SIMPLE` | Nada — ni DNI ni RUC | Compra rápida, sin identificar al cliente. |
| `BOLETA_CON_DNI` | El cliente debe tener `dni` registrado en `pagatu-cliente-ms` | El cliente quiere su DNI impreso en la boleta (garantía, seguimiento). |
| `FACTURA` | El cliente debe tener `ruc` registrado en `pagatu-cliente-ms` | El cliente (persona natural con RUC, o persona jurídica) necesita crédito fiscal / gasto deducible. |

La columna `tipo_comprobante` ya está en `ordenes` (ver `V1__create_orden_tables.sql`, arriba en este mismo documento). Validar que el cliente realmente tenga `dni` (para `BOLETA_CON_DNI`) o `ruc` (para `FACTURA`) antes de crear la orden es responsabilidad del código — una consulta a `pagatu-cliente-ms` — no de una restricción SQL entre bases de datos distintas.

## Método de pago por orden

`pagatu` es comercio electrónico, así que la orden también registra con qué método el cliente eligió pagar — el procesamiento real de ese pago (confirmar, rechazar, reintentar) es trabajo de `pago-ms`, recién en S8; hoy solo se deja registrado el método elegido en `ordenes.metodo_pago`.

"Contra entrega" no es un método de pago — es **cuándo** se paga. El repartidor puede cobrar en efectivo, por Yape/Plin (QR) o incluso por transferencia al momento de la entrega; el medio sigue siendo uno de los mismos de siempre. Por eso `pagatu` separa dos columnas: **con qué** se paga (`metodo_pago`) y **cuándo** se paga (`momento_pago`).

**Tabla 5. Métodos de pago considerados para `pagatu`**

| `metodo_pago` | Descripción | Nota |
|---|---|---|
| `TARJETA` | Tarjeta de crédito o débito (Visa, Mastercard), vía pasarela de pagos. | Es el nodo "Pasarela de pagos externa" que ya está en el C4 nivel 1 y 2 ([`docs/index.md`](../index.md)); en la práctica solo aplica cuando `momento_pago = ADELANTADO`. |
| `YAPE_PLIN` | Billetera digital (Yape o Plin) por QR. | Funciona tanto adelantado como al momento de la entrega. |
| `TRANSFERENCIA` | Transferencia bancaria directa. | El cliente paga fuera del sistema y sube o registra un comprobante; también puede hacerse al recibir el pedido. |
| `PAGO_EFECTIVO` | Efectivo — en un agente/punto físico afiliado si es adelantado, o directo al repartidor si es contra entrega. | Útil para clientes sin tarjeta ni banca digital. |

**Tabla 6. `momento_pago`: cuándo se paga**

| `momento_pago` | Descripción |
|---|---|
| `ADELANTADO` | El cliente paga al crear la orden, antes del despacho — `pago-ms` procesa el pago de inmediato (S8-S9). |
| `CONTRA_ENTREGA` | El cliente paga cuando recibe el pedido, con cualquiera de los métodos de la Tabla 5 (excepto `TARJETA`, en la práctica — no suele haber POS físico con el repartidor). |

`metodo_pago` y `momento_pago` son obligatorios desde el diseño (`NOT NULL`) porque, a diferencia del comprobante, en comercio electrónico real casi nunca se permite crear una orden sin saber cómo y cuándo se va a pagar. Validar reglas como "`TARJETA` no aplica a `CONTRA_ENTREGA`" es trabajo del código en `pago-ms` (S8), no una restricción SQL de este documento.
