# Tasks: Front-End Automation Ticket Purchase Flow

**Input**: `.specify/specs/001-pom-page-factory/plan.md` + `spec.md`
**Prerequisites**: constitution.md ✅ | spec.md ✅ | plan.md ✅

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Puede ejecutarse en paralelo
- **[US1]**: Pertenece a User Story 1 (inicio exitoso de compra)
- **[US2]**: Pertenece a User Story 2 (bloqueo por email requerido)

---

## Phase 1: Setup (Infraestructura base)

**Purpose**: Inicialización del proyecto Gradle con dependencias Serenity BDD y configuración declarativa

- [X] T001 Crear `build.gradle` con plugins y dependencias Serenity BDD según `plan.md`
- [X] T002 Crear `src/test/resources/serenity.conf` con `base.url = http://localhost:3000`, URLs API y datos base de prueba
- [X] T003 [P] Crear estructura de carpetas: `src/test/java/pages/`, `src/test/java/steps/`, `src/test/java/runners/`, `src/test/java/support/`, `src/test/resources/features/purchase/`

---

## Phase 2: Foundation (Prerequisito bloqueante)

**Purpose**: Runner de Cucumber y preparación determinística de datos para habilitar todos los escenarios

**⚠️ CRÍTICO**: Ninguna User Story puede implementarse sin este paso completo

- [X] T004 Crear `src/test/java/runners/CucumberTestRunner.java` con `@RunWith(CucumberWithSerenity.class)` y `@CucumberOptions` apuntando a `src/test/resources/features/` con glue `steps`
- [X] T005 Crear `src/test/java/support/TestDataSetup.java` para preparar por API un evento futuro con tickets disponibles antes de cada escenario

**Checkpoint**: Ejecutar `./gradlew test` — debe compilar con runner y helper de datos listos

---

## Phase 3: User Story 1 - Inicio exitoso de compra (Priority: P1) 🎯 MVP

**Goal**: Automatizar el flujo positivo de compra pública hasta el paso `Completar Pago`

**Independent Test**: Ejecutar solo el escenario positivo de `reserva-compra-tickets.feature` — debe pasar sin depender de US2

- [X] T006 [P] [US1] Crear `src/test/resources/features/purchase/reserva-compra-tickets.feature` con los 2 escenarios definidos en `plan.md`
- [X] T007 [P] [US1] Crear `src/test/java/pages/BuyPage.java` extendiendo `PageObject`, con `@FindBy` para el listado de eventos y la acción de abrir el detalle de compra
- [X] T008 [P] [US1] Crear `src/test/java/pages/BuyerEventPage.java` extendiendo `PageObject`, con `@FindBy` para: título del evento, cantidad, email, expiración, botón de compra y encabezado `Completar Pago`
- [X] T009 [US1] Crear `src/test/java/steps/TicketPurchaseSteps.java` con métodos `@Step` para: navegar a `/buy`, seleccionar el evento preparado, diligenciar cantidad válida, diligenciar email válido, diligenciar expiración válida, confirmar compra y verificar transición a `Completar Pago` (depende de T007, T008)
- [X] T010 [US1] Implementar step definitions en `src/test/java/steps/TicketPurchaseSteps.java` para el escenario positivo de `reserva-compra-tickets.feature` usando los datos preparados por `TestDataSetup` (depende de T005, T006, T009)

**Checkpoint**: Ejecutar `./gradlew test aggregate` filtrando el escenario positivo — debe pasar con PASS en el reporte Serenity

---

## Phase 4: User Story 2 - Bloqueo por email requerido (Priority: P2)

**Goal**: Automatizar la validación negativa cuando el usuario intenta comprar sin email

**Independent Test**: Ejecutar solo el escenario negativo de `reserva-compra-tickets.feature` — debe pasar sin depender de US1

- [X] T011 [US2] Agregar en `src/test/java/pages/BuyerEventPage.java` el `@FindBy` o verificación necesaria para identificar el mensaje `El email es requerido` y confirmar permanencia en el formulario (depende de T008)
- [X] T012 [US2] Agregar métodos `@Step` en `src/test/java/steps/TicketPurchaseSteps.java` para: intentar compra sin email, verificar mensaje `El email es requerido` y verificar que el usuario permanece en el formulario sin avanzar a `Completar Pago` (depende de T009)
- [X] T013 [US2] Implementar step definitions en `src/test/java/steps/TicketPurchaseSteps.java` para el escenario negativo de `reserva-compra-tickets.feature` (depende de T006, T012)

**Checkpoint**: Ejecutar `./gradlew test aggregate` — ambos escenarios US1 y US2 deben pasar con PASS

---

## Phase 5: Polish & Validación Final

**Purpose**: Verificación de cumplimiento de la Constitution antes de entregar

- [X] T014 [P] Verificar que ninguna clase contiene código comentado (Principio IV)
- [X] T015 [P] Verificar que todos los selectores usan `@FindBy` — ningún `driver.findElement` (Principio III)
- [X] T016 [P] Verificar que todos los métodos de acción usan anotación `@Step` (Principio III)
- [X] T017 [P] Verificar que cada escenario corre de forma aislada, sin compartir estado ni depender del orden de ejecución (Principio V)
- [X] T018 Ejecutar `./gradlew test aggregate` completo y confirmar que ambos escenarios pasan
- [X] T019 [P] Verificar que el reporte en `target/site/serenity/index.html` muestra los 2 escenarios con estado PASS

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: Sin dependencias — empieza inmediatamente
- **Phase 2 (Foundation)**: Depende de Phase 1 — bloquea US1 y US2
- **Phase 3 (US1)**: Depende de Phase 2 — puede iniciar cuando runner y preparación de datos estén listos
- **Phase 4 (US2)**: Depende de Phase 2 y reutiliza `BuyerEventPage.java` y `TicketPurchaseSteps.java` creados en US1
- **Phase 5 (Polish)**: Depende de Phases 3 y 4 completas

### Within Each User Story

- Feature file antes de Page Objects y Steps (Spec-First: el escenario define el contrato)
- Page Objects antes de Steps
- Helper de datos antes de la ejecución de escenarios
- Steps antes de Step Definitions

### Parallel Opportunities

- T003 puede ejecutarse en paralelo con T001 y T002
- T006, T007 y T008 pueden avanzar en paralelo una vez completa la Phase 2
- T014, T015, T016, T017 y T019 pueden ejecutarse en paralelo al cierre de implementación

---

## Implementation Strategy

### MVP (US1 solamente)

1. Phase 1: Setup
2. Phase 2: Foundation (runner + datos de prueba)
3. Phase 3: US1 (inicio exitoso de compra)
4. **VALIDAR**: `./gradlew test aggregate` → escenario positivo en PASS
5. Si pasa → continuar con Phase 4

### Entrega completa

1. Phases 1-2 → base lista
2. Phase 3 → US1 pasa ✅
3. Phase 4 → US2 pasa ✅
4. Phase 5 → verificación final
5. Reporte Serenity con 2 escenarios PASS → entrega al repositorio
