# AUTO_FRONT_POM_FACTORY Constitution

## I. Stack y Herramientas (NO NEGOCIABLE)

| Componente | Tecnología |
|------------|------------|
| Lenguaje | Java 21 |
| Framework de Automatización | Serenity BDD 4.2.0 |
| Runner de Pruebas | Cucumber 7.18.0 + JUnit 4.13.2 |
| Gestión de Dependencias | Gradle |
| Patrón UI | POM (Page Object Model) + Page Factory (`@FindBy`) |
| Driver | Selenium WebDriver — Chrome |
| Reporte | Serenity single-page HTML |

No se agrega ninguna dependencia nueva sin justificación explícita. No se usa el patrón Screenplay en este repositorio — ese patrón pertenece a AUTO_FRONT_SCREENPLAY.

---

## II. Patrón de Automatización (NO NEGOCIABLE)

**Page Object Model con Page Factory:**
- Cada página de la aplicación tiene una clase Page Object dedicada.
- Los elementos se declaran con `@FindBy` — nunca se usan `By.id()` o `By.xpath()` directamente en los step definitions.
- `PageFactory.initElements(driver, this)` se invoca en el constructor de cada Page Object.
- Los Page Objects NO contienen aserciones — solo encapsulan interacciones con la UI.
- Las aserciones viven en los Step Definitions.

```
Estructura del patrón:
Page Object (@FindBy) → Step Definitions → Feature (Gherkin)
```

---

## III. Arquitectura de Carpetas (NO NEGOCIABLE)

```
src/test/
├── java/com/ticketing/
│   ├── ui/                  ← Page Objects con @FindBy
│   │   ├── RegisterPage.java
│   │   ├── LoginPage.java
│   │   └── [ExistingPages].java
│   ├── stepdefinitions/     ← Step Definitions por feature
│   │   ├── AutenticacionStepDefinitions.java
│   │   └── GestionEventosStepDefinitions.java
│   ├── runners/             ← CucumberTestRunner
│   │   └── CucumberTestRunner.java
│   ├── hooks/               ← @Before / @After (setup/teardown del driver)
│   ├── util/                ← Helpers reutilizables (esperas, datos de prueba)
│   └── questions/           ← No aplica en POM — carpeta reservada, no usar
└── resources/
    ├── features/
    │   ├── gestion_eventos.feature        ← existente, NO modificar
    │   └── autenticacion.feature          ← nueva — feature de auth
    ├── serenity.conf
    └── logback-test.xml
```

---

## IV. Convenciones de Código

**Page Objects:**
- Nombre: `NombrePagina` + sufijo `Page` → `RegisterPage`, `LoginPage`
- Un archivo por página de la aplicación
- Constructor siempre llama `PageFactory.initElements(driver, this)`
- Métodos describen acciones de negocio, no interacciones técnicas:
  - ✅ `void completarFormularioDeRegistro(String nombre, String apellido, String email, String password, String confirmPassword)`
  - ❌ `void clickBotonSubmit()`

**Step Definitions:**
- Nombre: `NombreFeature` + sufijo `StepDefinitions` → `AutenticacionStepDefinitions`
- Un archivo por feature
- Sin lógica de negocio — solo orquesta Page Objects y aserciones

**Gherkin:**
- Escenarios en español
- Declarativos: describen comportamiento de negocio, no pasos técnicos
- Sin antipatrones: no usar "hago clic en el botón X" sino "envía el formulario de registro"
- Cada escenario es independiente — ninguno depende del estado del anterior

---

## V. Aplicación Bajo Prueba

| Servicio | URL |
|----------|-----|
| FrontendTicketing (Vue 3) | `http://localhost:5173` |
| AuthService | `http://localhost:8003` |

Configuración del driver en `serenity.conf`:
```
webdriver.driver = chrome
headless.mode = false
```

---

## VI. Scope de Esta Feature — Autenticación (HU1)

**Dentro del alcance de este repositorio:**

| CP | Descripción | Tipo | Prioridad |
|----|-------------|------|-----------|
| CP-HU1-01 | Registro exitoso → mensaje de confirmación + redirección a `/login` | Positivo | Obligatorio |
| CP-HU1-08 | Registro con email duplicado → mensaje "Este correo electrónico ya está en uso" | Negativo | Obligatorio |
| CP-HU1-02 | Campos vacíos → error por campo obligatorio | Negativo | Recomendado |
| CP-HU1-03 | Formato de correo inválido → mensaje de error junto al campo | Negativo | Recomendado |
| CP-HU1-04 | Contraseñas no coinciden → mensaje de error | Negativo | Recomendado |
| CP-HU1-05 | Contraseña < 8 caracteres → mensaje de política (valor límite) | Negativo | Recomendado |

**Fuera del alcance de este repositorio:**
- Escenarios de login, logout y cuenta bloqueada → pertenecen a AUTO_FRONT_SCREENPLAY
- Pruebas de API REST → pertenecen a AUTO_API_SCREENPLAY
- CP-HU1-09 (bcrypt) → prueba unitaria del backend, no automatizable en UI

---

## VII. Reglas de Calidad (NO NEGOCIABLE)

- **Independencia de escenarios**: cada escenario arranca con estado limpio (driver fresco o datos independientes). Ningún escenario puede depender del resultado de otro.
- **Sin código comentado**: ausencia total de código comentado dentro de las clases. El historial de Git es la fuente de verdad.
- **Nomenclatura semántica**: nombres de variables, métodos y clases claros y descriptivos. Sin abreviaciones crípticas.
- **@FindBy obligatorio**: todos los elementos de la UI se declaran con `@FindBy`. Prohibido localizar elementos directamente en los step definitions.
- **Sin lógica en Page Objects**: los Page Objects no tienen `if`, `for` ni aserciones. Solo interacciones encapsuladas.

---

## VIII. Definition of Done

Una tarea está completa cuando:

- El escenario Gherkin compila y ejecuta sin errores
- El Page Object declara todos sus elementos con `@FindBy`
- Los step definitions no contienen selectores CSS/XPath directos
- El escenario es independiente (puede ejecutarse solo o en cualquier orden)
- El reporte Serenity HTML se genera correctamente con el resultado del escenario
- Sin código comentado en ninguna clase
- `./gradlew clean test aggregate` pasa sin errores de compilación

---

## Governance

Esta constitución extiende el proyecto existente sin contradecirlo. El repositorio ya cubre la feature de gestión de eventos (`gestion_eventos.feature`) — esa feature se conserva intacta. La feature de autenticación se agrega como extensión independiente.

**Version**: 1.0.0 | **Ratified**: 2026-04-08 | **Last Amended**: 2026-04-08
