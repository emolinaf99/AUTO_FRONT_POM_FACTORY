# Feature Specification: Front-End Automation Ticket Purchase Flow

**Feature Branch**: `001-pom-page-factory`
**Created**: 2026-03-20
**Status**: Approved
**Input**: Sistema real Ticketing Project Week 1 — flujo público de compra de tickets
**Contexto de la app**: `.specify/memory/app-context.md`

## Entorno de Ejecución

| Dato | Valor | Fuente |
|---|---|---|
| URL base del frontend | `http://localhost:3000` | Frontend Next.js inspeccionado |
| Ruta raíz | `/` redirige a `/buy` | `frontend/app/page.tsx` |
| Página pública de compra | `/buy` | `frontend/app/buy/page.tsx` |
| Página detalle de compra | `/buy/{id}` | `frontend/app/buy/[id]/page.tsx` |
| Heading de listado | `Compra de Tickets` | Observable en la UI |
| Heading del paso exitoso | `Completar Pago` | Observable en la UI |
| Mensaje de validación | `El email es requerido` | Toast observable en la UI |
| Datos requeridos en backend | Mínimo 1 evento futuro con tickets disponibles | Precondición controlada por prueba |

---

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Iniciar compra de tickets con datos válidos (Priority: P1)

Como comprador de tickets,
quiero seleccionar un evento disponible e iniciar la compra con datos válidos,
para avanzar al paso de pago del sistema.

**Why this priority**: Es el flujo principal observable del sistema real.
Sin esta transición al paso de pago, la funcionalidad pública de compra no cumple su propósito.

**Independent Test**: Puede verificarse de forma aislada preparando por API un evento
futuro con tickets disponibles, navegando a `/buy`, seleccionando el evento preparado,
completando cantidad válida, email válido y tiempo de expiración mayor a 0, y confirmando
que la interfaz avanza a `Completar Pago`.

**Acceptance Scenarios**:

1. **Given** existe un evento futuro con tickets disponibles preparado para la prueba,
   **And** el usuario navega a la vista pública de compra,
   **When** selecciona el evento disponible e ingresa una cantidad válida, un email válido y un tiempo de expiración mayor a 0,
   **Then** el sistema avanza al paso `Completar Pago`.

---

### User Story 2 - Bloquear compra cuando falta el email (Priority: P2)

Como comprador de tickets,
quiero recibir una validación cuando intento comprar sin email,
para corregir el formulario antes de enviar la solicitud.

**Why this priority**: Es la validación negativa observable más directa en el flujo real de compra.
Complementa el flujo positivo y evita envíos inválidos del formulario.

**Independent Test**: Puede verificarse de forma aislada preparando por API un evento
futuro con tickets disponibles, navegando a `/buy`, entrando al detalle de compra del evento,
dejando el email vacío y confirmando que aparece el mensaje `El email es requerido`
sin avanzar al paso de pago.

**Acceptance Scenarios**:

1. **Given** existe un evento futuro con tickets disponibles preparado para la prueba,
   **And** el usuario se encuentra en el formulario de compra del evento,
   **When** intenta comprar tickets sin ingresar email,
   **Then** el sistema muestra el mensaje `El email es requerido`,
   **And** el usuario permanece en el formulario sin avanzar a `Completar Pago`.

---

### Edge Cases — Fuera de Alcance

Los siguientes casos quedan **explícitamente fuera del alcance** de este proyecto:

- Tiempo de expiración inválido (≤ 0).
- Reserva con email de formato inválido.
- Resultado asíncrono de reserva o pago posterior al paso `Completar Pago`.
- Flujos de autenticación o login, ya que no existen en la aplicación inspeccionada.

---

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema DEBE redirigir la ruta raíz `/` hacia la vista pública `/buy`.
- **FR-002**: El sistema DEBE listar eventos disponibles para compra en la vista pública `/buy`.
- **FR-003**: El sistema DEBE permitir abrir el detalle de un evento disponible desde la vista pública de compra.
- **FR-004**: El sistema DEBE permitir iniciar la compra cuando existe un evento con tickets disponibles y el formulario contiene cantidad válida, email válido y tiempo de expiración mayor a 0.
- **FR-005**: El sistema DEBE bloquear el envío cuando el email está vacío y mostrar el mensaje `El email es requerido`.
- **FR-006**: Los escenarios DEBEN ser completamente independientes entre sí.
- **FR-007**: Ambos escenarios DEBEN residir en un único archivo `.feature`.
- **FR-008**: Los Page Objects DEBEN usar exclusivamente `@FindBy` para los selectores.
- **FR-009**: Los Steps DEBEN llevar anotación `@Step` con descripción en lenguaje de negocio.
- **FR-010**: La configuración de URLs y propiedades de ejecución DEBE residir en `serenity.conf`, no en el código.

### Key Entities

- **BuyPage**: Representa la vista pública `/buy`. Contiene el listado de eventos y el acceso al detalle de compra.
- **BuyerEventPage**: Representa la vista `/buy/{id}`. Contiene el formulario de compra con cantidad, email, expiración, botón de acción y el paso `Completar Pago`.
- **TicketPurchaseSteps**: Orquesta la navegación, selección del evento, diligenciamiento del formulario y validaciones del flujo.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Los 2 escenarios Gherkin se ejecutan sin errores con `./gradlew test aggregate`.
- **SC-002**: El reporte Serenity en `target/site/serenity/` muestra ambos escenarios con estado PASS.
- **SC-003**: US1 confirma la transición visual al paso `Completar Pago`.
- **SC-004**: US2 confirma que el mensaje `El email es requerido` aparece al enviar el formulario sin email y que el usuario permanece en la vista de compra.
- **SC-005**: Ejecutar cada escenario de forma aislada produce PASS sin depender del otro.
- **SC-006**: El código no contiene comentarios ni nomenclatura no semántica.
