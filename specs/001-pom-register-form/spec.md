# Feature Specification: POM + Page Factory — Registro de Usuario (HU1)

**Feature Branch**: `001-pom-register-form`  
**Created**: 2026-04-08  
**Status**: Draft  
**Input**: User description: "Automatizar con POM + Page Factory el formulario de registro de FrontendTicketing (Vue 3) en localhost:5173. HU1 — Escenario positivo CP-HU1-01: registro exitoso con datos válidos muestra mensaje de confirmación y redirige a /login. Escenario negativo CP-HU1-08: registro con email duplicado muestra mensaje de error 409 junto al campo correo."

## Clarifications

### Session 2026-04-08

- Q: ¿Cómo se garantiza que el email duplicado existe antes de ejecutar CP-HU1-08? → A: Llamada a la API del AuthService en un hook `@Before` del escenario que crea el usuario de prueba dinámicamente.
- Q: ¿Cuál es el endpoint del AuthService para registrar un usuario de prueba? → A: `POST /api/auth/register` (sin prefijo `/v1`).
- Q: ¿Cómo debe gestionarse la limpieza del usuario de prueba creado por el hook `@Before` de CP-HU1-08? → A: Usar un email único por ejecución (timestamp/UUID); no se requiere limpieza activa.
- Q: ¿Qué tipo de atributo utilizan los campos del formulario de registro en el DOM del frontend? → A: Atributo `id`; los IDs definidos en `RegisterForm.vue` son: `reg-firstName`, `reg-lastName`, `reg-email`, `reg-password`, `reg-confirm`. No se usará `data-testid` para no modificar el componente Vue existente.
- Q: ¿Con qué selectores se identifican el mensaje de confirmación y el mensaje de error de email duplicado? → A: Clases Tailwind CSS. Confirmación: `div.text-green-400`. Error email: `#reg-email ~ p.text-red-400` (o `#reg-email + p`). No tienen `id` ni `data-testid`; los selectores son funcionales pero frágiles ante cambios de diseño.

## User Scenarios & Testing *(mandatory)*

### User Story 1 — CP-HU1-01: Registro exitoso con datos válidos (Priority: P1)

Un usuario nuevo completa el formulario de registro de FrontendTicketing con datos válidos (firstName, lastName, email, password, confirmPassword) y envía el formulario. El sistema valida los datos, crea la cuenta y presenta un mensaje de confirmación, redirigiendo automáticamente a la página de inicio de sesión.

**Why this priority**: Es el flujo principal (happy path) de la funcionalidad de registro. Sin este flujo validado, no hay incorporación de usuarios al sistema y no existe valor entregable.

**Independent Test**: Puede probarse de forma independiente abriendo la página de registro, completando todos los campos con datos únicos válidos y verificando que aparece el mensaje de confirmación y la URL cambia a `/login`.

**Acceptance Scenarios**:

1. **Given** el usuario se encuentra en la página de registro, **When** completa firstName, lastName, un email único, password y confirmPassword coincidentes y presiona el botón de envío, **Then** el sistema muestra un mensaje de confirmación de registro exitoso visible en pantalla.
2. **Given** el registro fue completado exitosamente y se mostró el mensaje de confirmación, **When** el sistema redirige al usuario, **Then** la URL activa corresponde a `/login` y la página de inicio de sesión es visible.

---

### User Story 2 — CP-HU1-08: Registro rechazado por email duplicado (Priority: P1)

Un usuario intenta registrarse con un email que ya existe en el sistema. Al enviar el formulario, el sistema rechaza la solicitud con código 409 y muestra un mensaje de error posicionado junto al campo de correo electrónico, sin redirigir al usuario.

**Why this priority**: Escenario negativo crítico de integridad de datos. Si no se valida, podrían crearse cuentas duplicadas o el usuario quedaría sin retroalimentación adecuada sobre el error.

**Independent Test**: Puede probarse de forma independiente enviando el formulario con un email previamente registrado y verificando que el mensaje de error es visible junto al campo correo y que no ocurre redirección.

**Acceptance Scenarios**:

1. **Given** el usuario se encuentra en la página de registro y el email ingresado ya existe en el sistema, **When** completa todos los campos y presiona el botón de envío, **Then** el sistema no redirige al usuario y muestra un mensaje de error que indica que el correo ya está registrado.
2. **Given** el sistema muestra el mensaje de error por email duplicado, **When** el usuario observa el formulario, **Then** el mensaje de error aparece en la proximidad visual del campo email y los demás campos permanecen visibles y editables.

---

### Edge Cases

- ¿Qué ocurre si el formulario se envía con el campo email vacío o con formato inválido? → Fuera de alcance (HU1 v1)
- ¿Qué ocurre si password y confirmPassword no coinciden? → Fuera de alcance (HU1 v1)
- ¿Cómo responde el sistema si el AuthService no está disponible en el momento del envío? → Fuera de alcance (HU1 v1)
- ¿Qué sucede si el usuario intenta navegar directamente a `/login` sin haberse registrado? → Fuera de alcance (HU1 v1)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: La suite de pruebas DEBE cubrir el escenario CP-HU1-01 — registro exitoso con datos válidos.
- **FR-002**: La suite de pruebas DEBE cubrir el escenario CP-HU1-08 — registro rechazado con email ya registrado.
- **FR-003**: Las pruebas DEBEN interactuar con el formulario mediante un Page Object (`RegisterPage`) que encapsule los siguientes campos y sus IDs de DOM: `firstName` (`id="reg-firstName"`), `lastName` (`id="reg-lastName"`), `email` (`id="reg-email"`), `password` (`id="reg-password"`), `confirmPassword` (`id="reg-confirm"`) y el botón de envío.
- **FR-004**: El Page Object DEBE declarar los elementos del formulario con `@FindBy` (Page Factory) en dos grupos:
  - **Por `id`** (campos del formulario): `@FindBy(id = "reg-firstName")`, `@FindBy(id = "reg-lastName")`, `@FindBy(id = "reg-email")`, `@FindBy(id = "reg-password")`, `@FindBy(id = "reg-confirm")`.
  - **Por selector CSS** (elementos de feedback/acción sin `id`): botón submit → `@FindBy(css = "button[type='submit']")`, mensaje de confirmación → `@FindBy(css = "div.text-green-400")`, error de email → `@FindBy(css = "#reg-email + p")`.
  No se añaden atributos `data-testid` al componente Vue, pues está fuera del alcance de esta HU.
- **FR-005**: La prueba CP-HU1-01 DEBE verificar que el elemento `div.text-green-400` (mensaje de confirmación) es visible en pantalla tras el envío exitoso del formulario.
- **FR-006**: La prueba CP-HU1-01 DEBE verificar que la URL activa corresponde a `/login` después del registro exitoso.
- **FR-007**: La prueba CP-HU1-08 DEBE verificar que el elemento `p` adyacente al campo `#reg-email` (selector: `#reg-email + p`) es visible en pantalla Y su contenido textual contiene exactamente: `"Este correo electrónico ya está en uso. ¿Deseas iniciar sesión?"`.
- **FR-008**: La prueba CP-HU1-08 DEBE verificar que el usuario NO es redirigido cuando ocurre el error de email duplicado.
- **FR-009**: Los casos de prueba DEBEN poder ejecutarse de forma independiente entre sí, sin dependencias de estado compartido.
- **FR-010**: El entorno de ejecución DEBE apuntar al frontend disponible en `localhost:5173`, conectado al servicio de autenticación en `localhost:8003`.
- **FR-011**: La precondición de CP-HU1-08 (usuario con email duplicado) DEBE crearse dinámicamente mediante una llamada `POST /api/auth/register` al AuthService (`localhost:8003`) en un hook `@Before` del escenario. El email de prueba DEBE ser único por ejecución (generado con timestamp o UUID) para evitar colisiones entre ejecuciones; no se requiere un hook `@After` de limpieza.

### Key Entities *(include if feature involves data)*

- **RegisterPage (Page Object)**: Representa la página de registro. Encapsula los campos del formulario mediante `@FindBy(id = ...)` con los IDs: `reg-firstName`, `reg-lastName`, `reg-email`, `reg-password`, `reg-confirm`, más el botón de envío. Expone acciones: completar campo, enviar formulario, leer mensaje de confirmación, leer mensaje de error junto al campo email.
- **Usuario de Prueba**: Conjunto de datos de entrada por escenario. CP-HU1-01 requiere un email único no registrado; CP-HU1-08 requiere un email previamente existente en el sistema.
- **Mensaje de Confirmación**: `<div class="text-green-400">` renderizado en `RegisterForm.vue` cuando `showSuccess = true`. Selector CSS: `div.text-green-400`. No tiene `id` ni `data-testid`. El selector es funcional pero frágil ante cambios de paleta Tailwind; se recomienda añadir `data-testid="success-message"` en una iteración futura.
- **Mensaje de Error (campo email)**: `<p class="text-red-400">` adyacente al input `#reg-email`, renderizado cuando `emailError` tiene contenido. Selector CSS: `#reg-email ~ p.text-red-400`. No tiene `id` ni `data-testid`. Mismo riesgo de fragilidad; se recomienda `data-testid="email-error"` en iteración futura.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El caso CP-HU1-01 pasa de forma estable en al menos 3 ejecuciones consecutivas sin intervención manual.
- **SC-002**: El caso CP-HU1-08 pasa de forma estable en al menos 3 ejecuciones consecutivas sin intervención manual.
- **SC-003**: Ambos casos de prueba se completan en un tiempo total inferior a 60 segundos por ejecución en condiciones normales de red local.
- **SC-004**: El 100% de los criterios de aceptación definidos para CP-HU1-01 y CP-HU1-08 están cubiertos por aserciones ejecutables dentro de la suite.
- **SC-005**: Cada ejecución de CP-HU1-08 utiliza un email único generado con timestamp o UUID; la independencia entre ejecuciones está garantizada sin necesidad de limpieza activa post-escenario.

## Assumptions

- El frontend FrontendTicketing (Vue 3) está disponible y ejecutándose en `localhost:5173` durante la ejecución de las pruebas.
- El servicio de autenticación (AuthService) está disponible en `localhost:8003` y devuelve código 409 ante intentos de registro con email duplicado.
- La precondición de CP-HU1-08 se establece dinámicamente: un hook `@Before` invoca `POST /api/auth/register` en `localhost:8003` para crear el usuario de prueba con un email único por ejecución (generado con timestamp o UUID). No se requiere hook `@After` de limpieza.
- El proyecto de automatización usa Selenium WebDriver con soporte de Page Factory (`PageFactory.initElements`) integrado en el runner Cucumber + Serenity BDD existente.
- Los campos del formulario usan atributo `id`; los mensajes de feedback usan selectores CSS de clase Tailwind (frágiles ante cambios de paleta de colores).
- CP-HU1-02 a CP-HU1-05 quedan fuera del alcance de esta iteración.
- Los selectores CSS de los mensajes de feedback (`div.text-green-400` y `#reg-email + p`) son clases utilitarias de Tailwind; son funcionales en la implementación actual pero frágiles ante cambios de paleta de colores. Se recomienda añadir `data-testid="success-message"` y `data-testid="email-error"` al componente `RegisterForm.vue` como mejora de mantenibilidad fuera del alcance de esta HU.
- El navegador de prueba y el entorno local tienen acceso de red a ambos servicios sin restricciones de firewall o proxy.
