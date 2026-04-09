# Research: POM + Page Factory — Registro de Usuario (HU1)

**Feature**: `001-pom-register-form`  
**Date**: 2026-04-08  
**Status**: Complete — sin NEEDS CLARIFICATION pendientes

---

## R-001: Cliente HTTP en hook `@Before` sin nuevas dependencias

**Unknowns resueltos**:
- ¿Cómo hacer una llamada HTTP a `POST /api/auth/register` desde Java 21 sin agregar dependencias al `build.gradle`?

**Decision**: Usar `java.net.http.HttpClient` (módulo `java.net.http`, disponible desde Java 11, estable en Java 21).

**Rationale**:
- Disponible en el JDK sin dependencia externa.
- API fluida: `HttpClient.newHttpClient().send(request, BodyHandlers.ofString())`.
- Suficiente para una llamada POST con cuerpo JSON y lectura del código de respuesta.
- No requiere modificación de `build.gradle` — cumple el gate de constitución.

**Alternatives considered**:
- RestAssured → requiere dependencia Gradle (`io.rest-assured:rest-assured`) — descartado.
- OkHttp → requiere dependencia Gradle — descartado.
- `HttpURLConnection` → disponible en Java 8+, pero API verbosa e impeditiva. Descartado en favor de `HttpClient`.

---

## R-002: Estrategia de email único por ejecución

**Unknowns resueltos**:
- ¿Cómo garantizar que CP-HU1-08 no colisione entre ejecuciones consecutivas?

**Decision**: Generar el email de prueba con `UUID.randomUUID()` como sufijo local: `"test." + UUID.randomUUID().toString().substring(0,8) + "@testmail.com"`.

**Rationale**:
- `java.util.UUID` disponible en JDK sin dependencias.
- Garantiza unicidad estadística entre ejecuciones paralelas y secuenciales.
- No requiere limpieza post-escenario — simplica el hook a solo `@Before`.
- El email generado en `@Before` se comparte al step definition vía campo de instancia del hook o a través de un objeto de contexto de prueba.

**Alternatives considered**:
- Timestamp (`System.currentTimeMillis()`) como sufijo → funciona pero menos legible en reportes; UUID preferido.
- Email hardcodeado en `test-data.properties` → no garantiza independencia entre ejecuciones paralelas — descartado.

---

## R-003: Compartir estado entre hook `@Before` y step definitions

**Unknowns resueltos**:
- ¿Cómo pasa `AutenticacionHooks.java` el email generado a `AutenticacionStepDefinitions.java`?

**Decision**: Usar un objeto de contexto compartido (POJO `TestContext`) inyectado por Cucumber-Java via PicoContainer (ya disponible en `serenity-cucumber`).

**Rationale**:
- `serenity-cucumber` incluye PicoContainer para inyección de dependencias entre pasos. No requiere dependencia adicional.
- El patrón es: `TestContext` tiene un campo `String emailDuplicado`; tanto el hook como el step definition lo reciben en su constructor.
- Alternativa equivalente: campo `static` en una clase utilitaria → desaconsejado (impide ejecución paralela futura).

**Alternatives considered**:
- Variable `static` en clase `TestContext` → funciona pero acopla escenarios si se ejecutan en paralelo. Descartado.
- Pasar el email como argumento del step Gherkin → expone un detalle técnico en el feature file, violando el principio declarativo. Descartado.

---

## R-004: Localizadores `@FindBy` concretos para `RegisterPage`

**Unknowns resueltos**:
- ¿Qué estrategia y valor usar en cada `@FindBy`?

**Decision**: Usar `@FindBy(id = "<valor>")` con los IDs reales del DOM de `RegisterForm.vue`:

| Campo | `@FindBy` |
|-------|-----------|
| Nombre | `@FindBy(id = "reg-firstName")` |
| Apellido | `@FindBy(id = "reg-lastName")` |
| Email | `@FindBy(id = "reg-email")` |
| Contraseña | `@FindBy(id = "reg-password")` |
| Confirmar contraseña | `@FindBy(id = "reg-confirm")` |
| Botón submit | `@FindBy(css = "button[type='submit']")` — no tiene id; selector CSS por tipo |
| Mensaje confirmación | `@FindBy(css = "div.text-green-400")` — clase Tailwind |
| Error email | `@FindBy(css = "#reg-email ~ p.text-red-400")` — adyacente al campo email |

**Rationale**:
- IDs confirmados por inspección de `RegisterForm.vue` (clarificación Pregunta 4).
- El botón submit es el único elemento sin `id`; `button[type='submit']` es suficientemente específico en este formulario.
- Los mensajes de feedback usan clases Tailwind (clarificación Pregunta 5); los selectores CSS son funcionales aunque frágiles ante cambios de paleta.

**Alternatives considered**:
- XPath para los mensajes → más frágil y verboso. Descartado.
- `data-testid` → requeriría modificar `RegisterForm.vue`, fuera del scope. Descartado.

---

## R-005: Compatibilidad del `CucumberTestRunner` con hooks en paquete separado

**Unknowns resueltos**:
- El `glue` actual es `"com.ticketing.stepdefinitions"`. ¿Hay que modificarlo para que Cucumber registre los hooks en `com.ticketing.hooks`?

**Decision**: Ampliar el array `glue` a `{"com.ticketing.stepdefinitions", "com.ticketing.hooks"}`.

**Rationale**:
- Cucumber-Java solo registra anotaciones `@Before`/`@After` en las clases incluidas en los paquetes del `glue`.
- El paquete `com.ticketing.hooks` ya existe en la estructura del proyecto (carpeta vacía); simplemente se añade al glue.
- Cambio mínimo, sin impacto en los escenarios existentes.

**Alternatives considered**:
- Mover `AutenticacionHooks` al paquete `stepdefinitions` → viola la separación de responsabilidades definida en la constitución. Descartado.
- Usar un paquete-padre como `glue = "com.ticketing"` → registraría todas las clases del árbol, incluyendo `util` y `ui`, lo que puede causar conflictos. Descartado en favor del enfoque explícito.

---

## R-006: Estrategia de espera explícita en Serenity BDD

**Unknowns resueltos**:
- ¿Cómo esperar a que el mensaje de confirmación o de error sea visible sin flakiness?

**Decision**: Usar `WebDriverWait` con `ExpectedConditions.visibilityOfElementLocated` en métodos del `RegisterPage`, envuelto en métodos de acción semánticos. Alternativa más simple: `@FindBy` inicializa el elemento perezosamente; Serenity aplica implicit wait configurado en `serenity.conf`. Para los mensajes de feedback, se agregará espera explícita de hasta 5 segundos dentro del método `obtenerMensajeConfirmacion()` y `obtenerMensajeErrorEmail()`.

**Rationale**:
- Los mensajes de feedback son renderizados condicionalmente por Vue 3 (v-if); pueden no estar en el DOM inmediatamente tras el submit.
- `WebDriverWait` es parte de Selenium (ya en el classpath); sin dependencia nueva.
- 5 segundos es conservador para red local; ajustable sin cambio de código si se externaliza el valor.

**Alternatives considered**:
- `Thread.sleep()` → antipatrón de automatización. Descartado.
- Solo implicit wait de Serenity → puede pasar por alto elementos que aparecen después del timeout implícito. Descartado para mensajes dinámicos Vue.
