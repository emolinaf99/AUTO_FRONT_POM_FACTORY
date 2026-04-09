# Tasks: POM + Page Factory — Registro de Usuario (HU1)

**Input**: Design documents from `/specs/001-pom-register-form/`  
**Prerequisites**: [plan.md](plan.md) · [spec.md](spec.md) · [research.md](research.md) · [data-model.md](data-model.md) · [contracts/](contracts/) · [quickstart.md](quickstart.md)

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Puede ejecutarse en paralelo (archivos distintos, sin dependencias pendientes)
- **[Story]**: Historia de usuario a la que pertenece la tarea ([HU1] = CP-HU1-01, [HU2] = CP-HU1-08)
- Cada tarea incluye la ruta exacta del archivo

---

## Phase 1: Setup (Infraestructura compartida)

**Propósito**: Configurar el runner de Cucumber para que registre los hooks del paquete `com.ticketing.hooks`, único cambio estructural previo a toda implementación.

- [X] T001 Extender el array `glue` de `@CucumberOptions` en `src/test/java/com/ticketing/runners/CucumberTestRunner.java` añadiendo `"com.ticketing.hooks"`, dejando `glue = {"com.ticketing.stepdefinitions", "com.ticketing.hooks"}`

**Checkpoint**: Runner configurado — Cucumber reconocerá hooks en `com.ticketing.hooks` sin necesidad de nuevas dependencias.

---

## Phase 2: Foundational (Prerequisitos bloqueantes)

**Propósito**: Artefactos compartidos entre ambas historias que DEBEN existir antes de implementar cualquier step definition.

**⚠️ CRÍTICO**: Ninguna historia puede implementarse hasta que esta fase esté completa.

- [X] T002 [P] Crear `src/test/resources/features/autenticacion.feature` copiando el contrato Gherkin de `specs/001-pom-register-form/contracts/autenticacion.feature` (incluye `# language: es`, tags `@autenticacion`, `@CP-HU1-01`, `@CP-HU1-08`)
- [X] T003 [P] Crear `src/test/java/com/ticketing/ui/RegisterPage.java` con `PageFactory.initElements(driver, this)` en el constructor y los 8 campos `@FindBy` en dos grupos: (1) **por `id`**: `@FindBy(id="reg-firstName")` → `campoNombre`, `@FindBy(id="reg-lastName")` → `campoApellido`, `@FindBy(id="reg-email")` → `campoEmail`, `@FindBy(id="reg-password")` → `campoPassword`, `@FindBy(id="reg-confirm")` → `campoConfirmacion`; (2) **por CSS**: `@FindBy(css="button[type='submit']")` → `botonEnviar`, `@FindBy(css="div.text-green-400")` → `mensajeConfirmacion`, `@FindBy(css="#reg-email + p")` → `errorEmail`; exponer métodos de acción semánticos (`completarNombre`, `completarApellido`, `completarEmail`, `completarPassword`, `completarConfirmacion`, `enviarFormulario`) y métodos de lectura con `WebDriverWait` de 5 s (`obtenerMensajeConfirmacion()`, `obtenerErrorEmail()`)

**Checkpoint**: Feature file y Page Object listos — las historias HU1 y HU2 pueden implementarse en paralelo.

---

## Phase 3: User Story 1 — CP-HU1-01: Registro exitoso (Priority: P1) 🎯 MVP

**Goal**: Automatizar el flujo de registro exitoso: completar el formulario con datos válidos, enviar, verificar mensaje de confirmación `div.text-green-400` y redirección a `/login`.

**Independent Test**: `./gradlew clean test -Dcucumber.filter.tags="@CP-HU1-01"` — pasa si el mensaje de confirmación es visible y la URL contiene `/login`.

### Implementación de HU1

- [X] T004 [HU1] Crear `src/test/java/com/ticketing/stepdefinitions/AutenticacionStepDefinitions.java` con los 5 step definitions de CP-HU1-01: `"que el usuario se encuentra en la página de registro"` (navega a `http://localhost:5173/register` e inicializa `RegisterPage` con `PageFactory`), `"completa el formulario con datos válidos"` (genera email UUID único, llama a los métodos `completar*` de `RegisterPage`), `"envía el formulario de registro"` (llama `RegisterPage.enviarFormulario()`), `"el sistema muestra el mensaje de confirmación de registro exitoso"` (llama `RegisterPage.obtenerMensajeConfirmacion()` y afirma que es `displayed`), `"el usuario es redirigido a la página de inicio de sesión"` (afirma que `driver.getCurrentUrl()` contiene `/login`)

**Checkpoint**: CP-HU1-01 funcional e independientemente verificable. MVP entregable.

---

## Phase 4: User Story 2 — CP-HU1-08: Email duplicado (Priority: P1)

**Goal**: Automatizar el escenario negativo: precondición vía API (`POST /api/auth/register`), envío del formulario con el email ya registrado, verificar mensaje `p.text-red-400` junto a `#reg-email` y ausencia de redirección.

**Independent Test**: `./gradlew clean test -Dcucumber.filter.tags="@CP-HU1-08"` — pasa si el error de email duplicado es visible y la URL no contiene `/login`.

### Implementación de HU2

- [X] T005 [P] [HU2] Crear `src/test/java/com/ticketing/util/TestContext.java` como POJO con campo `public String emailDuplicado` para compartir el email generado en `@Before` hacia los step definitions vía inyección PicoContainer
- [X] T006 [P] [HU2] Crear `src/test/java/com/ticketing/util/ApiHelper.java` con método `public static void registrarUsuario(String email, String password)` que invoca `POST http://localhost:8003/api/auth/register` usando `java.net.http.HttpClient` (Java 21 built-in) con `Content-Type: application/json` y body `{"firstName":"Test","lastName":"User","email":"<email>","password":"<password>","roles":[]}`, lanzando `RuntimeException` si la respuesta no es `2xx`
- [X] T007 [HU2] Crear `src/test/java/com/ticketing/hooks/AutenticacionHooks.java` con hook `@Before(value = "@CP-HU1-08")` que: (1) genera `email = "test." + UUID.randomUUID().toString().substring(0, 8) + "@testmail.com"`, (2) invoca `ApiHelper.registrarUsuario(email, "Test1234!")`, (3) asigna el email a `TestContext.emailDuplicado`; recibe `TestContext` por inyección de constructor (PicoContainer de `serenity-cucumber`) — depende de T005 y T006
- [X] T008 [HU2] Añadir en `src/test/java/com/ticketing/stepdefinitions/AutenticacionStepDefinitions.java` los 4 step definitions de CP-HU1-08: `"existe un usuario registrado con el mismo email en el sistema"` (no-op: el hook `@Before` ya creó el usuario; el step solo documenta la precondición), `"completa el formulario usando el email ya registrado"` (lee `testContext.emailDuplicado` y llama a los métodos `completar*` de `RegisterPage`), `"el sistema muestra un mensaje de error junto al campo correo"` (llama `RegisterPage.obtenerErrorEmail()`, afirma `isDisplayed()` y que el texto contiene exactamente `"Este correo electrónico ya está en uso. ¿Deseas iniciar sesión?"`), `"el usuario no es redirigido a otra página"` (afirma que `driver.getCurrentUrl()` no contiene `/login`); añadir `TestContext` al constructor del step definition — depende de T005 y T007

**Checkpoint**: CP-HU1-08 funcional e independientemente verificable.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Propósito**: Validar integración completa de la suite, tiempo de ejecución y reporte Serenity.

- [X] T009 Ejecutar `./gradlew clean test -Dcucumber.filter.tags="@autenticacion"` y verificar que CP-HU1-01 y CP-HU1-08 pasan y el tiempo total es < 60 s; a continuación ejecutar `./gradlew aggregate` y verificar que el reporte Serenity single-page se genera correctamente en `build/site/serenity/index.html` (ambos comandos son parte del DoD constitucional)

---

## Dependency Graph

```
T001 (glue runner)
  └─► T002 [P] (feature file)    T003 [P] (RegisterPage)
        └─────────────────────────────────────┐
                                              ▼
                                          T004 (AutenticacionStepDefinitions HU1)
                                            ── MVP verificable ──►
                                          T005 [P] (TestContext)
                                          T006 [P] (ApiHelper)
                                              ├─► T007 (AutenticacionHooks)
                                              └─────────────────┐
                                                                ▼
                                                            T008 (step defs HU2)
                                                              ── HU2 verificable ──►
                                                        T009 (Polish / full run)
```

---

## Parallel Execution Examples

### Ejecución paralela recomendada (dos agentes simultáneos)

**Agente A** (después de T001):
```
T002 → T003 → T004   (HU1 completo — MVP)
```

**Agente B** (después de T001):
```
T005 + T006 (en paralelo) → T007 → T008   (HU2 completo)
```

> T009 (Polish) solo cuando ambos agentes hayan terminado.

---

## Implementation Strategy

1. **MVP mínimo**: Completar T001 → T002 + T003 → T004. En este punto CP-HU1-01 es funcional y demostrable.
2. **Incremento HU2**: Completar T005 + T006 → T007 → T008. En este punto CP-HU1-08 se añade sin riesgo de romper HU1.
3. **Validación final**: T009 confirma la suite completa.

**Scope MVP sugerido**: Solo HU1 (T001–T004) — 4 tareas, independientemente ejecutable y verificable.

---

## Summary

| Métrica | Valor |
|---------|-------|
| Total de tareas | 9 |
| Tareas HU1 (CP-HU1-01) | 3 (T002, T003, T004) + T001 compartida |
| Tareas HU2 (CP-HU1-08) | 4 (T005, T006, T007, T008) |
| Tareas de setup | 1 (T001) |
| Tareas de polish | 1 (T009) |
| Tareas paralelizables [P] | 4 (T002, T003, T005, T006) |
| Criterio de prueba independiente HU1 | `./gradlew test -Dcucumber.filter.tags="@CP-HU1-01"` |
| Criterio de prueba independiente HU2 | `./gradlew test -Dcucumber.filter.tags="@CP-HU1-08"` |
| Scope MVP sugerido | T001 → T002 + T003 → T004 (solo HU1) |
