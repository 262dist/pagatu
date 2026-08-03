<!-- Transcripción fiel generada desde: dist2026-1.docx -->

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
| 06 | Horas de la asignatura | H. Te. Pract: 2 / H. Prc. Pres: 2 | 14 | Año y semestre académico | 2026-1 |
| 07 | Docente | Sullon Macalupu Abel Angel |  |  |  |
| 08 | Pre-requisito | Lenguaje de Programación II |  |  |  |

## II. Sumilla

La asignatura es de naturaleza teórico-práctica, pertenece al Área de Estudios Específicos y Especialidad y a la subárea de Ingeniería de Software. Su propósito es brindar al estudiante los conocimientos necesarios para desarrollar aplicaciones distribuidas, aplicando servicios REST e integración de sistemas backend. Aborda el diseño y configuración base de microservicios, la implementación de atributos de calidad como resiliencia, seguridad y consistencia, y la integración y despliegue de soluciones distribuidas en entornos reproducibles.

## III. Competencia del perfil de egreso en relación a la asignatura

| Tipo | Competencia | Nivel / dimensiones |
|---|---|---|
| Específica | **INGENIERÍA DE SOFTWARE:** Gestiona y desarrolla software de manera eficiente y efectiva, basándose en estándares internacionales de calidad a fin de lograr el control y aseguramiento de la calidad según el contexto de la organización. | N. 1.1: Programación. Desarrolla aplicaciones de escritorio, web y móvil. |
| General | **CARACTER Y APRENDIZAJE AUTONOMO:** Cultiva un carácter integro y autonomo, guíado por principios biblicos y adventistas, integrando un enfoque espiritual con la proactividad en el aprendizaje y el desarrollo personal. | N. 1.1: Firmeza de Proposito, Ejecucion, Dominio Propio, Mantener el Esfuerzo, Salud SocioEmocional. |

## IV. Resultado de aprendizaje de la asignatura

| Resultado de aprendizaje | Producto Académico |
|---|---|
| Diseña, implementa e integra una aplicación web distribuida basada en arquitectura de microservicios, aplicando atributos de calidad como resiliencia, seguridad y consistencia, validando su funcionamiento mediante pruebas técnicas y despliegue reproducible, sustentando las decisiones arquitectónicas adoptadas. | **Nombre:** Aplicación web basada en arquitectura de microservicios (arquitectura de software, seguridad, integración de backend con frontend y despliegue). |
|  | **Descripción:** Diseña e implementa una aplicación web distribuida basada en microservicios que incluya definición de servicios y contratos REST, configuración centralizada, API Gateway, registro y descubrimiento de servicios, seguridad mediante autenticación JWT y políticas de autorización, resiliencia con Circuit Breaker y balanceo de carga, patrón de consistencia distribuida para operaciones multi servicio, integración con cliente web y despliegue containerizado mediante Docker o Docker Compose, presentando documentación técnica, pruebas y evidencias de ejecución. |

## V. Unidades de aprendizaje

## Unidad 1: Fundamentos de microservicios

| Resultado de aprendizaje | Producto |
|---|---|
| Analiza el dominio de una problemática y diseña la arquitectura base de microservicios, implementando servicios REST y configurando mecanismos de configuración centralizada, API Gateway y comunicación básica entre servicios. | **Nombre:** Microservicios REST con arquitectura base configurada. |

| Criterios de evaluación del producto | Descripción del producto |
|---|---|
| Microservicios correctamente delimitados según el dominio. Endpoints REST funcionales y documentados. Configuración centralizada implementada. API Gateway configurado y operativo. Registro y descubrimiento funcionando. Comunicación entre microservicios validada. Documentación técnica clara y reproducible. | Modela el dominio de una problemática y define los microservicios y sus responsabilidades. Implementa microservicios REST funcionales, configura mecanismos de configuración centralizada y API Gateway, valida la comunicación entre servicios y documenta la arquitectura base implementada. |

### Sesiónes de aprendizaje

| N. | Fecha | Contenido | HT | HP | Actividad práctica | Actividad autónoma |
|---|---|---|---|---|---|---|
| 1 | 19-03-2026 | **ARQUITECTURA BASE:** Monolito vs microservicios, desacoplamiento, escalabilidad, tecnologías para microservicios, mantenibilidad, despliegue continuo. Estructura de microservicio, controladores, DTO, validaciones, manejo de errores, documentación OpenAPI. | 2 | 2 | Prepara un ambiente de desarrollo para microservicios y crea un servicio web REST como arquitectura base. | Prepara el ambiente de desarrollo para microservicios en su computadora y crea un servicio web REST para otra tabla de base de datos. |
| 2 | 26-03-2026 | Configuración centralizada para microservicios. | 2 | 2 | Implementa la configuración centralizada de los microservicios. | Implementa la configuración centralizada para el proyecto integrador. |
| 3 | 02-04-2026 | Registro y descubrimiento de los microservicios. | 2 | 2 | Implementa el registro y descubrimiento de los microservicios. | Implementa el registro y descubrimiento de los microservicios para el proyecto integrador. |
| 4 | 09-04-2026 | API Gateway: enrutamiento, filtros, CORS, centralización de acceso, registro y descubrimiento de servicios. | 2 | 2 | Implementa el API Gateway con rutas funcionales hacia los microservicios. | Implementa el API Gateway con rutas funcionales hacia los microservicios para el proyecto integrador. |
| 5 | 16-04-2026 | Comunicación entre microservicios y fundamentos de seguridad en aplicaciones distribuidas. | 2 | 2 | Implementa la comunicación entre microservicios con conexión segura. | Implementa la comunicación segura entre microservicios para el proyecto integrador. |
| 6 | 23-04-2026 | Evaluación parcial 1: Arquitectura base de microservicios. | 2 | 2 | Demostracion técnica del sistema implementado, validación de endpoints REST, configuración centralizada, funcionamiento del API Gateway y comunicación entre servicios, aplicando rubrica de evaluación. | Incorpora retroalimentación recibida, corrige arquitectura base y actualiza documentación técnica. |

## Unidad 2: Atributos de calidad en microservicios: Resiliencia, Seguridad y Consistencia

| Resultado de aprendizaje | Producto |
|---|---|
| Implementa atributos de calidad en la arquitectura de microservicios incorporando mecanismos de resiliencia, balanceo de carga, seguridad basada en JWT y patrones de consistencia distribuida en la solución desarrollada. | **Nombre:** Microservicios robustos con mecanismos de resiliencia, seguridad y consistencia implementados. |

| Criterios de evaluación del producto | Descripción del producto |
|---|---|
| Circuit Breaker correctamente implementado y validado. Balanceo de carga funcional con múltiples instancias. Autenticacion JWT implementada correctamente. Políticas y filtros de seguridad aplicados adecuadamente. Patrón de consistencia distribuida implementado y documentado. Evidencia técnica reproducible y verificable. | Integra mecanismos de resiliencia mediante Circuit Breaker y tolerancia a fallos, configura balanceo de carga con múltiples instancias, implementa autenticación y autorización con JWT y políticas de acceso, y aplica un patrón de consistencia distribuida para operaciones multi servicio, validando su correcto funcionamiento mediante pruebas y evidencias técnicas. |

### Sesiónes de aprendizaje

| N. | Fecha | Contenido | HT | HP | Actividad práctica | Actividad autónoma |
|---|---|---|---|---|---|---|
| 1 | 30-04-2026 | Resiliencia en microservicios (Circuit Breaker). | 2 | 2 | Implementa resiliencia en microservicios basados en el patrón Circuit Breaker y tolerancia a fallos. | Implementa el patrón Circuit Breaker y tolerancia a fallos para el proyecto integrador. |
| 2 | 07-05-2026 | Balanceo de carga de los microservicios. | 2 | 2 | Implementa balanceo de carga de los microservicios y refina el registro y descubrimiento. | Implementa el patrón Load Balancer en microservicios para el proyecto integrador. |
| 3 | 14-05-2026 | Seguridad JWT en microservicio. | 2 | 2 | Implementa protocolos de seguridad en microservicios. | Implementa la seguridad de los microservicios mediante JSON Web Token (JWT) para el proyecto integrador. |
| 4 | 21-05-2026 | Políticas y filtros. | 2 | 2 | Implementa políticas y filtros de seguridad. | Implementa seguridad en microservicios, políticas y filtros de seguridad para el proyecto integrador. |
| 5 | 28-05-2026 | Patrónes de consistencia de datos. | 2 | 2 | Aplica patrones de consistencia de datos. | Implementa patrones de consistencia de datos para el proyecto integrador. |
| 6 | 04-06-2026 | Evaluación parcial 2: Implementación de atributos de calidad en microservicios, resiliencia, balanceo, seguridad y consistencia distribuida. | 2 | 2 | Demostracion técnica del sistema implementado, validación de Circuit Breaker, balanceo de carga con múltiples instancias, autenticación y autorización mediante JWT, aplicación de políticas y filtros de seguridad, y verificacion del patrón de consistencia distribuida, aplicando rubrica de evaluación. | Incorpora retroalimentación recibida, corrige fallos en resiliencia, seguridad o consistencia, optimiza la arquitectura y actualiza la documentación técnica para preparar la integración y despliegue en la Unidad 3. |

## Unidad 3: Integración y despliegue de aplicaciones basadas en microservicios

| Resultado de aprendizaje | Producto |
|---|---|
| Integra los microservicios con un cliente web y despliega la solución completa en contenedores, validando su funcionamiento integral y documentando la arquitectura final. | **Nombre:** Aplicación web distribuida basada en microservicios integrada y desplegada. |

| Criterios de evaluación del producto | Descripción del producto |
|---|---|
| Arquitectura de microservicios correctamente delimitada y coherente con la problemática, con responsabilidades claras y desacoplamiento adecuado. Implementación adecuada de resiliencia, balanceo y consistencia distribuida validada con pruebas funcionales y técnicas. Seguridad implementada correctamente mediante autenticación y autorización. Integración frontend y backend funcional y validada, con dockerización completa y despliegue reproducible sin errores. Documentación técnica completa, organizada y coherente con la arquitectura implementada. Defensa técnica coherente, fundamentada y alineada con las decisiones arquitectónicas adoptadas. | Integra el cliente web con los microservicios mediante API Gateway, gestiona autenticación en el frontend, dockeriza la solución completa y realiza el despliegue reproducible del sistema, presentando documentación técnica final, manual de ejecución, pruebas de funcionamiento y defensa técnica de la arquitectura implementada. |

### Sesiónes de aprendizaje

| N. | Fecha | Contenido | HT | HP | Actividad práctica | Actividad autónoma |
|---|---|---|---|---|---|---|
| 1 | 11-06-2026 | Integración de los microservicios con el cliente web. | 2 | 2 | Integra microservicios con el cliente web contemplando la seguridad del frontend. | Integra microservicios con el cliente web contemplando la seguridad del frontend para el proyecto integrador. |
| 2 | 18-06-2026 | Despliegue de la solución. | 2 | 2 | Despliega los microservicios. | Despliega la solución para el proyecto integrador. |
| 3 | 25-06-2026 | Sustentación del producto del curso. | 2 | 2 | Presenta el proyecto integrador demostrando el funcionamiento integral del sistema, explica la arquitectura implementada, justifica decisiones técnicas relacionadas con desacoplamiento, resiliencia, seguridad y consistencia, evidencia integración frontend y backend y despliegue reproducible, respondiendo preguntas técnicas del docente. | Implementa mejoras derivadas de la retroalimentación recibida en la sustentación, optimiza documentación técnica y consolida versión final del repositorio del proyecto. |
| 4 | 02-07-2026 | Evaluación final: Evaluación integral del logro de la competencia del curso y análisis de resultados obtenidos en la implementación de la arquitectura de microservicios. | 2 | 2 | Realiza la evaluación final individual que puede incluir análisis técnico del proyecto desarrollado, preguntas conceptuales sobre arquitectura de microservicios, atributos de calidad, patrones implementados y despliegue de la solución. | Reflexiona sobre los aprendizajes alcanzados, identifica fortalezas y oportunidades de mejora en el desarrollo del proyecto y documenta conclusiones técnicas finales. |

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
| 23/04/2026 | Unidad 1: Fundamentos de microservicios | Microservicios REST con arquitectura base configurada. | Evaluación de sesiones | 5% |
| 23/04/2026 | Unidad 1: Fundamentos de microservicios | Microservicios REST con arquitectura base configurada. | Evaluación del producto | 15% |
| 04/06/2026 | Unidad 2: Atributos de calidad en microservicios: Resiliencia, Seguridad y Consistencia | Microservicios robustos con mecanismos de resiliencia, seguridad y consistencia implementados. | Evaluación de sesiones | 5% |
| 04/06/2026 | Unidad 2: Atributos de calidad en microservicios: Resiliencia, Seguridad y Consistencia | Microservicios robustos con mecanismos de resiliencia, seguridad y consistencia implementados. | Evaluación del producto | 30% |
| 02/07/2026 | Unidad 3: Integración y despliegue de aplicaciones basadas en microservicios | Aplicación web distribuida basada en microservicios integrada y desplegada. | Evaluación de sesiones | 10% |
| 02/07/2026 | Unidad 3: Integración y despliegue de aplicaciones basadas en microservicios | Aplicación web distribuida basada en microservicios integrada y desplegada. | Evaluación del producto | 25% |
| 02/07/2026 | Competencia General | CARACTER Y APRENDIZAJE AUTONOMO: Cultiva un carácter integro y autonomo, guíado por principios biblicos y adventistas, integrando un enfoque espiritual con la proactividad en el aprendizaje y el desarrollo personal. | Competencia General | 10% |

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

- Philippe J. 2018. *Docker Primeros pasos y puesta en práctica de una arquitectura basada en micro-servicios*. Ediciones ENI.
- Mathur, A. (2021). *Traefik API Gateway for Microservices*. Gurgaon, Haryana, India. ISBN-13 (electronic): 978-1-4842-6376-1. doi: <https://doi.org/10.1007/978-1-4842-6376-1>.
- Singh, N. (2021). *Building Microservices with Micronaut*. ISBN 978-1-80056-423-7.
- Carnell J. 2017. *Spring Microservices in Action*. Shelter Island. Manning Publications Co.
- Lamouchi, N. (2021). *Pro Java Microservices with Quarkus and Kubernetes: A Hands-on Guide*. París, France. ISBN-13 (electronic): 978-1-4842-7170-4.
- Patni, S. (2023). *Pro RESTful APIs with Micronaut: Build Java-Based Microservices with*. Santa Clara, CA, USA. ISBN-13 (electronic): 978-1-4842-9200-6.
- Arango E. C., Loaiza O. L. (2021). SCRUM Framework Extended with Clean Architecture Practices for Software Maintainability. In Silhavy R. (eds), *Software Engineering and Algorithms*. CSOC 2021. Lecture Notes in Networks and Systems, vol. 230. Springer, Cham. <https://doi.org/10.1007/978-3-030-77442-4_56>.
- Christudas, B. (2019). *Práctical Microservices Architectural Patterns*. Trivandrum, Kerala, India. ISBN-13 (electronic): 978-1-4842-4501-9.
- De La Torre C., Wagner B., Rousos M. 2022. *.NET Microservices: Architecture for Containerized .NET Applications*. Washington. Microsoft Corporation.
- Martinez D., Valderes P., Torres V. 2018. *Microservicios: un enfoque integrado*. Madrid: RA-MA Editorial.
- Berenguel J. 2016. *Desarrollo de aplicaciones web distribuidas UF1824*. España: Ediciones Paraninfo S.A.
- Schenker G. 2018. *Containerize your Apps with Docker and Kubernetes*. Packt Publishing Ltd.
- Boada M., Gomez J. 2018. *El Gran libro de Angular*. México: Alfaomega Grupo Editor.
- Palacio R., Moran A. 2015. *Inicios de colaboración en el desarrollo distribuido de software*.
- Gonzalez M., Perez A. 2021. *Criptografia esencial*. Madrid: RA-MA Editorial.

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