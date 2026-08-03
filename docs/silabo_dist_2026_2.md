<!-- Versión 2026-2 construida desde silabo_dist_2026_1.md y docs/index.md -->

Universidad Peruana Unión
Carret. Central km. 19.5 Ñaña. Telf. 01-6186300 Casilla 3564 Lima 1, Perú

# Sílabo: Desarrollo de Aplicaciónes Distribuidas

## I. Información General de Asignatura

| N. | Campo | Información | N. | Campo | Información |
|---|---|---|---|---|---|
| 01 | Facultad/EGP | Facultad de Ingeniería y Arquitectura | 09 | Año de plan de estudio | 2024-1 |
| 02 | Programa de estudio | EP Ingeniería de Sistemas | 10 | Ciclo de estudio | 5 |
| 03 | Tipo de estudio | Especialidad | 11 | Código de asignatura |  |
| 04 | Nombre de asignatura | Desarrollo de Aplicaciónes Distribuidas | 12 | Número de créditos | 3 |
| 05 | Duración |  | 13 | Nota mínima probatoria | 13 |
| 06 | Horas de la asignatura | H. Te. Pract: 2 / H. Prc. Pres: 2 | 14 | Año y semestre académico | 2026-2 |
| 07 | Docente | Sullon Macalupu Abel Angel |  |  |  |
| 08 | Pre-requisito | Lenguaje de Programación II |  |  |  |

## II. Sumilla

La asignatura es de naturaleza teórico-práctica, pertenece al Área de Estudios Específicos y Especialidad y a la subárea de Ingeniería de Software. Su propósito es brindar al estudiante los conocimientos necesarios para desarrollar aplicaciones distribuidas mediante microservicios, configuración centralizada, descubrimiento de servicios, API Gateway, seguridad, resiliencia, mensajería asíncrona, consistencia distribuida, observabilidad e integración frontend. El curso desarrolla progresivamente un sistema distribuido de comercio electronico ejecutable en entornos reproducibles con Docker y Spring Cloud.

## III. Competencia del perfil de egreso en relación a la asignatura

| Tipo | Competencia | Nivel / dimensiones |
|---|---|---|
| Específica | **INGENIERÍA DE SOFTWARE:** Gestiona y desarrolla software de manera eficiente y efectiva, basándose en estándares internacionales de calidad a fin de lograr el control y aseguramiento de la calidad según el contexto de la organización. | N. 1.1: Programación. Desarrolla aplicaciones de escritorio, web y móvil. |
| General | **CARACTER Y APRENDIZAJE AUTONOMO:** Cultiva un carácter integro y autonomo, guíado por principios biblicos y adventistas, integrando un enfoque espiritual con la proactividad en el aprendizaje y el desarrollo personal. | N. 1.1: Firmeza de Proposito, Ejecucion, Dominio Propio, Mantener el Esfuerzo, Salud SocioEmocional. |

## IV. Resultado de aprendizaje de la asignatura

| Resultado de aprendizaje | Producto Académico |
|---|---|
| Implementa, integra y sustenta un sistema distribuido basado en microservicios, aplicando configuración centralizada, descubrimiento de servicios, enrutamiento, escalado, seguridad, comunicación entre servicios, mensajería asíncrona, consistencia distribuida, observabilidad, persistencia e integración frontend, validando su funcionamiento mediante evidencias técnicas y defensa individual de aportes. | **Nombre:** Sistema distribuido de microservicios end-to-end, configurable, escalable, seguro, resiliente, consistente, observable, integrado con frontend y defendido técnicamente. |
|  | **Descripción:** Desarrolla un sistema distribuido de comercio electronico mediante laboratorios reproducibles. La solución integra infraestructura, microservicios, cliente frontend, mensajería, observabilidad y documentación técnica. El producto se presenta en equipo, pero cada estudiante evidencia y defiende su aporte individual. |

## V. Unidades de aprendizaje

## Unidad 1: Sistema distribuido base orientado a produccion

| Resultado de aprendizaje | Producto |
|---|---|
| Construye un primer servicio REST funcional, externaliza configuración por ambientes, registra servicios dinamicamente, accede al sistema mediante un punto unico de entrada y demuestra distribucion de trafico entre instancias. | **Nombre:** Sistema distribuido base funcional, configurable y preparado para múltiples instancias. |

| Criterios de evaluación del producto | Descripción del producto |
|---|---|
| Servicio REST funcional y persistente. Configuración externa por ambiente. Registro y descubrimiento de servicios operativo. Punto unico de acceso mediante Gateway. Distribucion de trafico entre instancias. Evidencias de ejecución reproducible y documentación técnica básica. | Implementa la base técnica del sistema distribuido: un servicio REST funcional, configuración centralizada, descubrimiento dinamico, acceso por Gateway y ejecución concurrente de instancias. |

### Sesiónes de aprendizaje

| N. | Fecha | Contenido | HT | HP | Actividad práctica | Actividad autónoma |
|---|---|---|---|---|---|---|
| 1 | 10/08/2026 - 15/08/2026 | Construcción de un servicio base para un sistema distribuido. | 2 | 2 | Implementa un servicio REST funcional, persistente, observable y preparado para ejecución reproducible. | Replica el patrón del servicio base en otro recurso del proyecto y documenta endpoints principales. |
| 2 | 16/08/2026 - 22/08/2026 | Gestión centralizada de configuración y ambientes. | 2 | 2 | Externaliza configuración por ambiente e incorpora evidencia inicial de observabilidad. | Configura variables y perfiles para otro microservicio del proyecto integrador. |
| 3 | 23/08/2026 - 29/08/2026 | Registro, descubrimiento y ejecución concurrente de servicios. | 2 | 2 | Registra servicios dinamicamente y ejecuta múltiples instancias operativas. | Valida el descubrimiento de servicios y documenta evidencias de instancias activas. |
| 4 | 30/08/2026 - 05/09/2026 | Punto unico de acceso y distribucion de trafico. | 2 | 2 | Configura rutas de acceso centralizado con Gateway y balanceo de carga. | Integra nuevas rutas del proyecto mediante Gateway y registra pruebas de acceso. |
| 5 | 06/09/2026 - 12/09/2026 | Integración del sistema distribuido base: servicios, configuración centralizada, descubrimiento, ejecución concurrente, Gateway y balanceo de carga. | 2 | 2 | **Evaluación de la Unidad I:**<br>1. Resolver la evaluación teórico-práctica de los temas de la Unidad I.<br>2. Presentar y sustentar el Sistema distribuido base funcional, configurable y preparado para múltiples instancias. | Corrige observaciones, estabiliza configuración y actualiza documentación técnica de U1. |

## Unidad 2: Sistema distribuido robusto

| Resultado de aprendizaje | Producto |
|---|---|
| Implementa comunicación sincronica resiliente, seguridad distribuida, mensajería asíncrona, consistencia eventual en procesos de negocio, observabilidad operacional e integración frontend mediante el punto unico de acceso. | **Nombre:** Sistema distribuido seguro, resiliente, consistente, observable e integrado con cliente frontend. |

| Criterios de evaluación del producto | Descripción del producto |
|---|---|
| Comunicación entre servicios con respuesta controlada ante fallos. Seguridad distribuida y proteccion de rutas. Mensajería asíncrona entre servicios desacoplados. Consistencia eventual, compensacion e idempotencia en procesos de negocio. Logs, health, métricas y paneles de diagnóstico. Cliente frontend integrado mediante Gateway. | Fortalece el sistema distribuido incorporando atributos de calidad, integración frontend y evidencias técnicas de operación real. |

### Sesiónes de aprendizaje

| N. | Fecha | Contenido | HT | HP | Actividad práctica | Actividad autónoma |
|---|---|---|---|---|---|---|
| 6 | 13/09/2026 - 19/09/2026 | Comunicación sincronica resiliente entre servicios. | 2 | 2 | Implementa comunicación entre microservicios con respuesta controlada ante fallos. | Agrega escenarios de fallo, fallback y recuperación al flujo principal del proyecto. |
| 7 | 20/09/2026 - 26/09/2026 | Seguridad distribuida y control de acceso. | 2 | 2 | Implementa autenticación, autorización y proteccion de rutas del sistema. | Protege endpoints del proyecto, documenta roles y valida accesos permitidos y denegados. |
| 8 | 27/09/2026 - 03/10/2026 | Mensajería asíncrona entre servicios. | 2 | 2 | Implementa comunicación por eventos entre servicios desacoplados. | Integra un evento de negocio adicional y registra evidencias de publicación y consumo. |
| 9 | 04/10/2026 - 10/10/2026 | Consistencia distribuida en procesos de negocio. | 2 | 2 | Implementa un proceso distribuido con consistencia eventual, compensacion e idempotencia. | Documenta el flujo de negocio, estados, compensaciones y pruebas de idempotencia. |
| 10 | 11/10/2026 - 17/10/2026 | Observabilidad y diagnóstico de sistemas distribuidos. | 2 | 2 | Incorpora logs, health, métricas y paneles de diagnóstico. | Registra evidencias de monitoreo, diagnóstico de fallos y trazabilidad operacional. |
| 11 | 18/10/2026 - 24/10/2026 | Integración con cliente frontend. | 2 | 2 | Integra el cliente web al sistema distribuido mediante Gateway. | Completa flujos frontend-backend con seguridad y consumo de APIs protegidas. |
| 12 | 25/10/2026 - 31/10/2026 | Integración del sistema distribuido robusto: comunicación resiliente, seguridad, mensajería, consistencia eventual, observabilidad e integración frontend. | 2 | 2 | **Evaluación de la Unidad II:**<br>1. Resolver la evaluación teórico-práctica de los temas de la Unidad II.<br>2. Presentar y sustentar el Sistema distribuido seguro, resiliente, consistente, observable e integrado con cliente frontend. | Corrige observaciones, consolida evidencias y prepara la validación end-to-end de U3. |

## Unidad 3: Validación y consolidación del producto del curso

| Resultado de aprendizaje | Producto |
|---|---|
| Integra los componentes desarrollados en las unidades anteriores, valida flujos completos, estabiliza documentación y despliegue local, prepara evidencias técnicas y sustenta el producto final. | **Nombre:** Sistema distribuido de microservicios end-to-end, validado, documentado, estabilizado y defendido técnicamente. |

| Criterios de evaluación del producto | Descripción del producto |
|---|---|
| Producto probado integralmente. Documentación técnica completa y reproducible. Evidencias de despliegue, seguridad, mensajería, consistencia y observabilidad. Defensa grupal coherente, aporte individual verificable y demostración de las competencias desarrolladas. | Consolida el producto final del curso, valida flujos completos, estabiliza la arquitectura y sustenta técnicamente las decisiones implementadas. |

### Sesiónes de aprendizaje

| N. | Fecha | Contenido | HT | HP | Actividad práctica | Actividad autónoma |
|---|---|---|---|---|---|---|
| 13 | 01/11/2026 - 07/11/2026 | Validación end-to-end del producto del curso. | 2 | 2 | Ejecuta pruebas integrales del producto del curso. | Documenta evidencias end-to-end y corrige fallos detectados en flujos principales. |
| 14 | 08/11/2026 - 14/11/2026 | Revisión técnica y estabilización del producto. | 2 | 2 | Estabiliza documentación, evidencias, configuración y despliegue local. | Refina el manual de ejecución y prepara el guion de demostración, el repositorio final, las evidencias y la explicación técnica de la arquitectura. |
| 15 | 15/11/2026 - 21/11/2026 | Integración y validación del sistema distribuido: arquitectura, seguridad, mensajería, consistencia, observabilidad, frontend y despliegue reproducible. | 2 | 2 | **Evaluación de la Unidad III:**<br>1. Resolver la evaluación teórico-práctica de los temas de la Unidad III.<br>2. Presentar y sustentar el Sistema distribuido de microservicios end-to-end, validado, documentado y estabilizado. | Registra las observaciones del docente y actualiza el repositorio, las evidencias y la documentación final. |
| 16 | 22/11/2026 - 28/11/2026 | Integración de sistemas distribuidos: servicios, configuración, comunicación, resiliencia, seguridad, consistencia, observabilidad, pruebas y despliegue. | 2 | 2 | **Continuación de la evaluación de la Unidad III:**<br>Realizar, según corresponda, la evaluación final individual o las presentaciones y sustentaciones pendientes mediante demostración, preguntas técnicas y revisión de evidencias. | Actualiza la entrega final y reflexiona sobre aprendizajes, fortalezas y oportunidades de mejora. |

## VI. Estrategias metodológicas

| N. | Estrategias de enseñanza y de aprendizaje que se aplicaran en la asignatura |
|---|---|
| 1.1 | Flipped Classroom (Clase Invertida): En esta metodología, los estudiantes revisan el material teorico fuera del aula, generalmente en línea, y utilizan el tiempo en clase para actividades prácticas y discusiones. |
| 1.2 | Aprendizaje Basado en Problemas: Centra el aprendizaje en la resolución de problemas reales, una habilidad esencial en casi todas las profesiones. Estimula el pensamiento crítico y la colaboración, elementos clave en el desarrollo de competencias. |
| 1.3 | Proyectos: Fomentan habilidades de investigación, gestión del tiempo y trabajo en equipo, todas cruciales en el mundo profesional. |

## VII. Evaluación

La evaluación de los estudiantes se rige por el Reglamento de Estudios, disponible en: <https://upeu.edu.pe/reglamentos/evaluación/>.

La estructura evaluativa comprende componentes formativos y/o de procesos, de producto y genéricos, reflejando un enfoque integral.

### Componentes de evaluación y ponderacion

- **Evaluación de Sesiónes (ES):** Es el promedio de las evaluaciónes aplicadas a los estudiantes para verificar su proceso de aprendizaje durante las sesiones de las unidades. Su contribucion a la nota final es de hasta el 20%.
- **Evaluación de Productos (EP):** Es el promedio ponderado de las evaluaciónes de los productos entregados en cada unidad. Este componente representa un mínimo del 70% de la nota final.
- **Evaluación de Competencias Generales (ECG):** Esta evaluación aporta hasta un 10% al cálculo de la nota final.

### Programación de evaluaciónes

| Fecha | Unidad | Producto | Evaluación de proceso y de resultado | Pesos |
|---|---|---|---|---|
| 18/09/2026 | Unidad 1: Sistema distribuido base orientado a produccion | Sistema distribuido base funcional, configurable y preparado para múltiples instancias. | Evaluación de sesiones | 5% |
| 18/09/2026 | Unidad 1: Sistema distribuido base orientado a produccion | Sistema distribuido base funcional, configurable y preparado para múltiples instancias. | Evaluación del producto | 15% |
| 30/10/2026 | Unidad 2: Sistema distribuido robusto | Sistema distribuido seguro, resiliente, consistente, observable e integrado con cliente frontend. | Evaluación de sesiones | 5% |
| 30/10/2026 | Unidad 2: Sistema distribuido robusto | Sistema distribuido seguro, resiliente, consistente, observable e integrado con cliente frontend. | Evaluación del producto | 30% |
| 20/11/2026 | Unidad 3: Validación y consolidación del producto del curso | Sistema distribuido de microservicios end-to-end, validado, documentado, estabilizado y defendido técnicamente. | Evaluación de sesiones | 10% |
| 20/11/2026 | Unidad 3: Validación y consolidación del producto del curso | Sistema distribuido de microservicios end-to-end, validado, documentado, estabilizado y defendido técnicamente. | Evaluación del producto | 25% |
| 20/11/2026 | Competencia General | CARACTER Y APRENDIZAJE AUTONOMO: Cultiva un carácter integro y autonomo, guíado por principios biblicos y adventistas, integrando un enfoque espiritual con la proactividad en el aprendizaje y el desarrollo personal. | Competencia General | 10% |

| Componente | Peso |
|---|---|
| Evaluación de sesiones | 20% |
| Evaluación del producto | 70% |
| Evaluación de competencia generica | 10% |
| **Total** | **100%** |

## VIII. Recursos, medios y materiales

| N. | Recursos, medios y materiales |
|---|---|
| 1 | Guías y/o tutoriales |
| 2 | Laboratorios |
| 3 | Internet - Wifi |
| 4 | Proyector y/o TV Smart |

## IX. Referencias

### Básica (Fuentes primarias)

- Larsson, M. (2025). *Microservices with Spring Boot and Spring Cloud* (4.ª ed.). Packt Publishing. ISBN 978-1-80580-127-6.
- Mathur, A. (2021). *Traefik API Gateway for Microservices*. Gurgaon, Haryana, India. ISBN-13 (electronic): 978-1-4842-6376-1. doi: <https://doi.org/10.1007/978-1-4842-6376-1>.
- Singh, N. (2021). *Building Microservices with Micronaut*. ISBN 978-1-80056-423-7.
- Lamouchi, N. (2021). *Pro Java Microservices with Quarkus and Kubernetes: A Hands-on Guide*. París, France. ISBN-13 (electronic): 978-1-4842-7170-4.
- Patni, S. (2023). *Pro RESTful APIs with Micronaut: Build Java-Based Microservices with*. Santa Clara, CA, USA. ISBN-13 (electronic): 978-1-4842-9200-6.
- Arango E. C., Loaiza O. L. (2021). SCRUM Framework Extended with Clean Architecture Practices for Software Maintainability. In Silhavy R. (eds), *Software Engineering and Algorithms*. CSOC 2021. Lecture Notes in Networks and Systems, vol. 230. Springer, Cham. <https://doi.org/10.1007/978-3-030-77442-4_56>.
- Gonzalez M., Perez A. 2021. *Criptografia esencial*. Madrid: RA-MA Editorial.
- Álvarez Caules, C. (2024). *Arquitectura Java sólida y patrones*. Arquitectura Java. [PDF](https://www.arquitecturajava.com/wp-content/uploads/LibroArquitecturaJavaSolidayPatronesV2.pdf).

### Complementaria (Fuentes secundarias)

- Philippe J. 2018. *Docker Primeros pasos y puesta en práctica de una arquitectura basada en micro-servicios*. Ediciones ENI.
- Carnell J. 2017. *Spring Microservices in Action*. Shelter Island. Manning Publications Co.
- Christudas, B. (2019). *Práctical Microservices Architectural Patterns*. Trivandrum, Kerala, India. ISBN-13 (electronic): 978-1-4842-4501-9.
- De La Torre C., Wagner B., Rousos M. 2022. *.NET Microservices: Architecture for Containerized .NET Applications*. Washington. Microsoft Corporation.
- Martinez D., Valderes P., Torres V. 2018. *Microservicios: un enfoque integrado*. Madrid: RA-MA Editorial.
- Berenguel J. 2016. *Desarrollo de aplicaciones web distribuidas UF1824*. España: Ediciones Paraninfo S.A.
- Schenker G. 2018. *Containerize your Apps with Docker and Kubernetes*. Packt Publishing Ltd.
- Boada M., Gomez J. 2018. *El Gran libro de Angular*. México: Alfaomega Grupo Editor.
- Palacio R., Moran A. 2015. *Inicios de colaboración en el desarrollo distribuido de software*.

### Enlaces de internet

- <http://www.ucuenca.edu.ec/ojs/index.php/maskana/article/view/695>
- <http://asiermarques.com/2013/conceptos-sobre-apis-rest/>
- <https://hub.docker.com/>
- <http://tesis.ipn.mx/handle/123456789/19869>
- <https://www.youtube.com/watch?v=OJuNzSwsZaY>
- <https://angular.io/>
- <http://sedici.unlp.edu.ar/handle/10915/56635>
- <https://aws.amazon.com/es/microservices/>
- <https://dialnet.unirioja.es/servlet/articulo?código=5123616>
- <https://es.reactjs.org/>
- <https://material.angular.io/>
- <https://www.docker.com/>
- <https://bsoftgroup.com/educate/>
- <https://codearti.com/>
