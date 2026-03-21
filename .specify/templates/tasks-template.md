---

description: "Task list template for Java + Serenity BDD automation features"
---

# Tasks: [AUTOMATION FEATURE NAME]

**Input**: Design documents from `/specs/[###-feature-name]/`
**Prerequisites**: plan.md (required), spec.md (required)

**Organization**: Las tareas se agrupan por User Story para mantener trazabilidad y permitir
validación independiente de los 2 escenarios obligatorios.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Puede ejecutarse en paralelo (archivos distintos, sin dependencia directa)
- **[Story]**: US1 para flujo positivo, US2 para flujo negativo
- Cada tarea MUST incluir la ruta exacta del archivo impactado

## Path Conventions

- `src/test/java/pages/` para Page Objects
- `src/test/java/steps/` para Steps y step definitions
- `src/test/java/runners/` para runners
- `src/test/resources/features/` para archivos `.feature`
- `src/test/resources/serenity.conf` para configuración

<!--
  IMPORTANT:
  - Esta plantilla asume exactamente 2 escenarios por feature: 1 positivo y 1 negativo.
  - Las tareas de validación constitucional NO son opcionales.
  - Sustituye todos los ejemplos por tareas concretas del feature actual.
-->

## Phase 1: Setup (Infraestructura base)

**Purpose**: Dejar lista la base técnica del proyecto de automatización

- [ ] T001 Crear o ajustar `build.gradle` con dependencias compatibles con Serenity BDD
- [ ] T002 Crear o ajustar `src/test/resources/serenity.conf` con configuración declarativa
- [ ] T003 [P] Crear la estructura `pages/`, `steps/`, `runners/` y `features/` según `plan.md`

---

## Phase 2: Foundation (Prerequisito bloqueante)

**Purpose**: Habilitar la ejecución centralizada de Cucumber + Serenity

**⚠️ CRÍTICO**: Ninguna User Story inicia hasta completar esta fase

- [ ] T004 Crear o ajustar `src/test/java/runners/CucumberTestRunner.java` con `CucumberWithSerenity`
- [ ] T005 Validar que el runner no contiene lógica de negocio ni configuración hardcodeada

**Checkpoint**: La base compila y el runner apunta correctamente a `features/` y `steps/`

---

## Phase 3: User Story 1 - [Flujo positivo] (Priority: P1) 🎯 MVP

**Goal**: Automatizar el comportamiento exitoso principal

**Independent Test**: Ejecutar únicamente el escenario positivo y observar el resultado esperado

- [ ] T006 [P] [US1] Crear `src/test/resources/features/[feature-folder]/[positive-flow].feature`
- [ ] T007 [P] [US1] Crear o ajustar `src/test/java/pages/[PrimaryPage].java` con `@FindBy`
- [ ] T008 [P] [US1] Crear o ajustar `src/test/java/pages/[SecondaryPage].java` si aplica
- [ ] T009 [US1] Crear o ajustar `src/test/java/steps/[FeatureSteps].java` con métodos `@Step` para el flujo positivo
- [ ] T010 [US1] Implementar step definitions del escenario positivo sin duplicar localizadores

**Checkpoint**: El escenario US1 pasa por separado

---

## Phase 4: User Story 2 - [Flujo negativo] (Priority: P2)

**Goal**: Automatizar la validación o rechazo del caso inválido

**Independent Test**: Ejecutar únicamente el escenario negativo y observar el rechazo esperado

- [ ] T011 [P] [US2] Crear `src/test/resources/features/[feature-folder]/[negative-flow].feature`
- [ ] T012 [US2] Ajustar Page Objects existentes o crear uno nuevo para el estado de error o rechazo
- [ ] T013 [US2] Agregar métodos `@Step` y validaciones de negocio para el flujo negativo
- [ ] T014 [US2] Implementar step definitions del escenario negativo manteniendo independencia respecto a US1

**Checkpoint**: El escenario US2 pasa por separado y no depende del estado de US1

---

## Phase 5: Polish & Constitution Validation

**Purpose**: Validar cumplimiento integral antes de entrega

- [ ] T015 [P] Verificar que todos los localizadores usan `@FindBy` y no existe `driver.findElement`
- [ ] T016 [P] Verificar que todos los métodos de negocio usan `@Step`
- [ ] T017 [P] Verificar ausencia de código comentado y nombres ambiguos
- [ ] T018 [P] Verificar que `serenity.conf` concentra la configuración y no hay valores hardcodeados en clases
- [ ] T019 Ejecutar `./gradlew test aggregate`
- [ ] T020 [P] Confirmar en el reporte Serenity que US1 y US2 aparecen en PASS

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1**: Sin dependencias
- **Phase 2**: Depende de Phase 1 y bloquea el resto
- **Phase 3 (US1)**: Depende de Phase 2
- **Phase 4 (US2)**: Depende de Phase 2 y puede reutilizar Page Objects sin romper independencia
- **Phase 5**: Depende de Phase 3 y Phase 4 completas

### Within Each User Story

- Feature file antes de Step Definitions
- Page Objects antes de Steps
- Steps antes de validación completa
- Cada historia debe quedar ejecutable por separado antes de continuar

### Parallel Opportunities

- T003, T006, T007, T008 pueden ejecutarse en paralelo
- T015, T016, T017, T018, T020 pueden ejecutarse en paralelo

---

## Implementation Strategy

### MVP First

1. Completar Phase 1
2. Completar Phase 2
3. Completar Phase 3 (US1)
4. Validar US1 en aislamiento

### Full Delivery

1. Base lista
2. US1 en PASS
3. US2 en PASS
4. Validación constitucional y Serenity report completos

---

## Notes

- Cada tarea debe mapearse a una User Story o a un gate constitucional.
- Evitar tareas vagas, múltiples archivos sin ruta o dependencias cruzadas innecesarias.
- No agregar una tercera User Story sin una enmienda explícita de la Constitution.
