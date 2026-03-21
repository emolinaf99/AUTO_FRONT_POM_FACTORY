# Implementation Plan: [AUTOMATION FEATURE]

**Branch**: `[###-feature-name]` | **Date**: [DATE] | **Spec**: [link]
**Input**: Feature specification from `/specs/[###-feature-name]/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command para proyectos de
automatización UI en Java + Serenity BDD.

## Summary

[Resume el flujo a automatizar, el objetivo del usuario y el enfoque técnico POM + Page Factory]

## Technical Context

**Language/Version**: Java 21  
**Primary Dependencies**: Serenity BDD 4.x, Selenium, WebDriverManager, JUnit 4, Cucumber  
**Storage**: N/A  
**Testing**: `./gradlew test aggregate` + reportes Serenity  
**Target Platform**: Navegador web configurado en `serenity.conf`  
**Project Type**: Proyecto de automatización Front-End  
**Performance Goals**: Ejecución estable de 2 escenarios independientes  
**Constraints**: `@FindBy` obligatorio, `@Step` obligatorio, sin código comentado, sin configuración hardcodeada  
**Scale/Scope**: 2 escenarios Gherkin (1 positivo + 1 negativo), Page Objects y Steps trazables a la spec

## Constitution Check

*GATE: Debe aprobarse antes del diseño detallado y revalidarse antes de implementar.*

- [ ] **I. Spec-First y Trazabilidad**: `spec.md`, `plan.md` y `tasks.md` están alineados y la implementación propuesta se puede rastrear a User Stories y FRs.
- [ ] **II. Java + Serenity BDD**: El stack propuesto usa Java 21, Gradle Groovy DSL, Serenity BDD, Selenium, JUnit 4 y Cucumber con `CucumberWithSerenity`.
- [ ] **III. POM + Page Factory**: El diseño define Page Objects por página/componente, localizadores con `@FindBy` y Steps con `@Step`.
- [ ] **IV. Código Limpio y Configuración Declarativa**: La configuración vive en `serenity.conf`; no se propone lógica de negocio en el runner ni valores hardcodeados en clases.
- [ ] **V. Escenarios Independientes y Cobertura Mínima**: El alcance mantiene exactamente 2 escenarios aislados: 1 positivo (P1) y 1 negativo (P2).

## Project Structure

### Documentation (this feature)

```text
.specify/specs/[###-feature-name]/
├── spec.md
├── plan.md
└── tasks.md
```

### Source Code (repository root)

```text
src/
└── test/
    ├── java/
    │   ├── pages/
    │   ├── runners/
    │   └── steps/
    └── resources/
        ├── features/
        │   └── [feature-folder]/
        └── serenity.conf

build.gradle
```

**Structure Decision**: [Confirma la ubicación final de features, pages, steps, runner y `serenity.conf`.]

## Automation Design

### Feature Files

- **US1 / Flujo positivo**: [nombre del archivo `.feature`, tags y criterio observable]
- **US2 / Flujo negativo**: [nombre del archivo `.feature`, tags y criterio observable]

### Page Objects

- **[PageObject1]**: [responsabilidad de la página o componente]
- **[PageObject2]**: [responsabilidad de verificación o navegación]

### Step Strategy

- **Business Steps**: [acciones del usuario y validaciones con `@Step`]
- **Step Definitions**: [mapeo Gherkin -> métodos de Steps sin duplicar localizadores]

## Quality Gates

- [ ] `build.gradle` declara dependencias compatibles con Serenity.
- [ ] `serenity.conf` centraliza navegador, URL y propiedades de ejecución.
- [ ] No se requiere `driver.findElement` ni configuración embebida en clases.
- [ ] `./gradlew test aggregate` es suficiente para validar la entrega.
- [ ] El reporte Serenity esperado evidencia PASS para ambos escenarios.

## Complexity Tracking

> **Completar solo si existe una desviación aprobada respecto a la Constitution**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., escenario adicional] | [razón excepcional] | [por qué no cumple la cobertura mínima] |
| [e.g., configuración externa adicional] | [necesidad técnica] | [por qué `serenity.conf` solo no basta] |
