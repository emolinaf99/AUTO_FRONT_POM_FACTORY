# Quickstart: POM + Page Factory — Registro de Usuario (HU1)

**Feature**: `001-pom-register-form` | **Branch**: `001-pom-register-form`

---

## Requisitos previos

| Requisito | Verificación |
|-----------|-------------|
| Java 21 instalado | `java -version` → `openjdk 21.x` |
| Chrome instalado | Serenity gestiona ChromeDriver automáticamente vía WebDriverManager |
| FrontendTicketing (Vue 3) corriendo | `curl -s -o /dev/null -w "%{http_code}" http://localhost:5173` → `200` |
| AuthService corriendo | `curl -s -o /dev/null -w "%{http_code}" http://localhost:8003/api/auth/register -X POST` → `400` o `409` (no `000`) |

---

## Archivos nuevos a crear

### 1. `src/test/resources/features/autenticacion.feature`

Copiar el contrato Gherkin desde [`contracts/autenticacion.feature`](contracts/autenticacion.feature).

> El `# language: es` en la primera línea activa las palabras clave en español en Cucumber.

---

### 2. `src/test/java/com/ticketing/ui/RegisterPage.java`

Page Object con `@FindBy` y `PageFactory.initElements`. Campos requeridos:

| Campo Java | `@FindBy` |
|------------|-----------|
| `campoNombre` | `@FindBy(id = "reg-firstName")` |
| `campoApellido` | `@FindBy(id = "reg-lastName")` |
| `campoEmail` | `@FindBy(id = "reg-email")` |
| `campoPassword` | `@FindBy(id = "reg-password")` |
| `campoConfirmacion` | `@FindBy(id = "reg-confirm")` |
| `botonEnviar` | `@FindBy(css = "button[type='submit']")` |
| `mensajeConfirmacion` | `@FindBy(css = "div.text-green-400")` |
| `errorEmail` | `@FindBy(css = "#reg-email ~ p.text-red-400")` |

Constructor: `PageFactory.initElements(driver, this)`.

---

### 3. `src/test/java/com/ticketing/util/ApiHelper.java`

Utilitario estático. Método:
```
static void registrarUsuario(String email, String password)
```
Llama a `POST http://localhost:8003/api/auth/register`.  
Ver implementación de referencia en [`contracts/auth-api-schema.md`](contracts/auth-api-schema.md).

---

### 4. `src/test/java/com/ticketing/hooks/AutenticacionHooks.java`

Hook `@Before(value = "@CP-HU1-08")`:
1. Genera `email = "test." + UUID.randomUUID().toString().substring(0, 8) + "@testmail.com"`.
2. Llama `ApiHelper.registrarUsuario(email, "Test1234!")`.
3. Guarda `email` en `TestContext.emailDuplicado`.

Recibe `TestContext` por inyección de constructor (PicoContainer).

---

### 5. `src/test/java/com/ticketing/stepdefinitions/AutenticacionStepDefinitions.java`

Step definitions que mapean los pasos de `autenticacion.feature`. Recibe `TestContext` por inyección de constructor.

---

### 6. Modificar `src/test/java/com/ticketing/runners/CucumberTestRunner.java`

Añadir `"com.ticketing.hooks"` al array `glue` de la anotación `@CucumberOptions`:

```java
// Antes
glue = {"com.ticketing.stepdefinitions"}

// Después
glue = {"com.ticketing.stepdefinitions", "com.ticketing.hooks"}
```

---

## Ejecución

### Ejecutar solo los escenarios de HU1

```bash
./gradlew clean test -Dcucumber.filter.tags="@autenticacion"
```

### Ejecutar solo CP-HU1-01

```bash
./gradlew clean test -Dcucumber.filter.tags="@CP-HU1-01"
```

### Ejecutar solo CP-HU1-08

```bash
./gradlew clean test -Dcucumber.filter.tags="@CP-HU1-08"
```

### Ejecutar toda la suite

```bash
./gradlew clean test
```

---

## Ver el reporte Serenity

```bash
./gradlew reports
# El reporte HTML se genera en:
# build/site/serenity/index.html
```

---

## Estructura final de archivos nuevos y modificados

```text
src/test/
├── java/com/ticketing/
│   ├── ui/
│   │   └── RegisterPage.java                     ← NUEVO
│   ├── stepdefinitions/
│   │   └── AutenticacionStepDefinitions.java      ← NUEVO
│   ├── runners/
│   │   └── CucumberTestRunner.java               ← MODIFICAR (glue)
│   ├── hooks/
│   │   └── AutenticacionHooks.java               ← NUEVO
│   └── util/
│       └── ApiHelper.java                        ← NUEVO
└── resources/
    └── features/
        └── autenticacion.feature                 ← NUEVO
```

---

## Troubleshooting rápido

| Síntoma | Causa probable | Solución |
|---------|---------------|----------|
| `SessionNotCreatedException` al iniciar Chrome | Versión ChromeDriver incompatible | Serenity/WebDriverManager lo gestiona; verificar que Chrome esté actualizado |
| Hook `@Before` falla con `RuntimeException: HTTP 000` | AuthService no disponible en `localhost:8003` | Iniciar el AuthService antes de correr las pruebas |
| `NoSuchElementException` en `div.text-green-400` | El mensaje no aparece o tarda más de 5 s | Verificar que el frontend responde correctamente; revisar timeout en `RegisterPage` |
| Los steps no son reconocidos por Cucumber | Hook no está en el `glue` | Verificar que `CucumberTestRunner.glue` incluye `"com.ticketing.hooks"` |
| CP-HU1-08 falla con `409` en el `@Before` | Email colisionó (raro con UUID) | Verificar generación UUID; raro pero posible si la BD no limpia datos de pruebas muy antiguas |
