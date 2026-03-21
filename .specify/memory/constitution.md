<!--
Sync Impact Report
Version change: 1.0.0 -> 1.1.0
Modified principles:
- I. Spec-First (NON-NEGOTIABLE) -> I. Spec-First y Trazabilidad (NON-NEGOTIABLE)
- IV. Código Limpio -> IV. Código Limpio y Configuración Declarativa
- V. Escenarios Independientes -> V. Escenarios Independientes y Cobertura Mínima
Added sections:
- Calidad y Estándares Técnicos
- Workflow y Quality Gates
Removed sections:
- Ninguna
Templates requiring updates:
- ✅ .specify/templates/plan-template.md
- ✅ .specify/templates/spec-template.md
- ✅ .specify/templates/tasks-template.md
Follow-up TODOs:
- Ninguno
-->

# AUTO_FRONT_POM_FACTORY Constitution

## Core Principles

### I. Spec-First y Trazabilidad (NON-NEGOTIABLE)
Toda implementación MUST iniciar con `spec.md`, `plan.md` y `tasks.md` coherentes entre sí.
Ningún archivo de automatización, feature, Page Object, Step o configuración se crea o modifica
sin una especificación aprobada que lo justifique. Los escenarios Gherkin definidos en `spec.md`
MUST ser la fuente de verdad del comportamiento esperado y cada tarea de implementación MUST poder
trazarse a una User Story o a un requisito funcional.

**Rationale**: La trazabilidad evita automatizaciones huérfanas, reduce retrabajo y mantiene el
repositorio alineado con el objetivo funcional del proyecto.

### II. Java + Serenity BDD
Toda automatización del repositorio MUST usar exclusivamente este stack:
- Lenguaje: Java 21
- Build: Gradle con Groovy DSL
- Framework: Serenity BDD 4.x + Selenium
- Runner: JUnit 4 + Cucumber con `CucumberWithSerenity`
- Reportes: Serenity aggregate mediante `./gradlew test aggregate`

No se permite introducir otro framework de automatización UI, otro runner principal ni otra
convención de build dentro de este repositorio.

**Rationale**: Un stack único simplifica mantenimiento, onboarding, soporte docente y lectura de reportes.

### III. Patrón POM + Page Factory (NON-NEGOTIABLE)
Cada página o componente relevante MUST tener exactamente un Page Object responsable de su interacción.
Los Page Objects MUST extender `PageObject` de Serenity. Los selectores MUST declararse únicamente con
`@FindBy`; el uso de `driver.findElement`, `By` embebidos en Steps o localizadores duplicados está
prohibido. Los Steps MUST usar `@Step` con descripciones en lenguaje de negocio y MUST orquestar la
interacción entre features y páginas sin mezclar lógica de localización.

**Rationale**: POM + Page Factory mejora mantenibilidad, legibilidad y reutilización de componentes.

### IV. Código Limpio y Configuración Declarativa
El código MUST permanecer libre de comentarios muertos, bloques deshabilitados y nombres ambiguos.
El `CucumberTestRunner` MUST limitarse a configuración de ejecución; no puede contener lógica de negocio.
Toda configuración de driver, navegador, URLs, timeouts o propiedades de ejecución MUST residir en
`serenity.conf` o en propiedades externas compatibles con Serenity; no puede quedar hardcodeada en clases.

**Rationale**: La automatización limpia reduce fragilidad, facilita revisión y evita dependencias ocultas.

### V. Escenarios Independientes y Cobertura Mínima
Cada feature MUST producir exactamente 2 escenarios Gherkin ejecutables de forma aislada: 1 flujo positivo
prioritario y 1 flujo negativo complementario. Ningún escenario puede depender del estado dejado por otro,
compartir datos mutables sin reinicialización o asumir orden de ejecución. Cada escenario MUST poder correr
por separado y expresar claramente su criterio de aceptación observable.

**Rationale**: La independencia garantiza confiabilidad, mientras la cobertura mínima positiva/negativa
demuestra el comportamiento esperado y la validación defensiva del sistema.

## Calidad y Estándares Técnicos

- **Lenguaje**: Java 21.
- **Build**: Gradle con Groovy DSL.
- **Framework base**: Serenity BDD 4.x con Selenium y WebDriverManager.
- **Estructura mínima**: `src/test/java/pages`, `src/test/java/steps`, `src/test/java/runners`,
  `src/test/resources/features`, `src/test/resources/serenity.conf`.
- **Features**: Los archivos `.feature` MUST redactarse en lenguaje de negocio, con nombres semánticos y
  ubicación coherente por flujo.
- **Page Objects**: MUST encapsular solo comportamiento de UI y verificaciones asociadas a su página o componente.
- **Steps**: MUST representar acciones y validaciones de negocio; no deben duplicar localizadores ni configuración.
- **Configuración**: `serenity.conf` MUST centralizar navegador, propiedades de ejecución y parámetros ambientales.
- **Evidencia de aceptación**: `./gradlew test aggregate` MUST ser el comando de validación final antes de entrega.

## Workflow y Quality Gates

1. El flujo obligatorio del repositorio es `spec.md` -> `plan.md` -> `tasks.md` -> implementación.
2. Todo `plan.md` MUST incluir una sección de Constitution Check que valide explícitamente los principios I a V.
3. Todo `tasks.md` MUST incluir tareas de verificación para `@FindBy`, `@Step`, ausencia de código comentado,
	independencia de escenarios y generación del reporte Serenity.
4. Una implementación no está lista para revisión si no existe evidencia de ejecución satisfactoria de
	`./gradlew test aggregate` o una justificación documentada y aprobada de la excepción.
5. Toda desviación deliberada de la constitución MUST documentarse en `plan.md` bajo una sección de complejidad
	o excepción antes de escribir código.

## Governance

Esta constitución prevalece sobre prácticas locales, notas temporales o preferencias individuales dentro de
AUTO_FRONT_POM_FACTORY. Toda enmienda MUST registrarse en este archivo, incluir su impacto sobre plantillas y
seguir versionado semántico:

- **MAJOR**: elimina o redefine principios de forma incompatible.
- **MINOR**: agrega principios, secciones o quality gates obligatorios.
- **PATCH**: aclara redacción sin cambiar el significado normativo.

Los revisores y autores de PR MUST verificar cumplimiento explícito de los principios I a V, la presencia de
artefactos Speckit actualizados y la evidencia de Serenity antes de aprobar cambios. Si una regla necesita
cambiarse, la constitución MUST actualizarse primero; no se permite reinterpretarla silenciosamente en un plan
o en una implementación puntual.

**Version**: 1.1.0 | **Ratified**: 2026-03-20 | **Last Amended**: 2026-03-20
