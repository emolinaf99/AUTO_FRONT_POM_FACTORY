# Implementation Plan: POM + Page Factory — Registro de Usuario (HU1)

**Branch**: `001-pom-register-form` | **Date**: 2026-04-08 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/001-pom-register-form/spec.md`

## Summary

Automatizar dos casos de prueba de la página de registro de FrontendTicketing (Vue 3 en `localhost:5173`) usando el patrón POM + Page Factory con Serenity BDD + Cucumber sobre el proyecto Java 21 existente. **CP-HU1-01** valida el registro exitoso (mensaje de confirmación `div.text-green-400` + redirección a `/login`). **CP-HU1-08** valida el rechazo por email duplicado (mensaje `p.text-red-400` junto a `#reg-email`, sin redirección). La precondición de CP-HU1-08 se crea dinámicamente vía `POST /api/auth/register` en un hook `@Before` usando `java.net.http.HttpClient` (Java 21 built-in, sin dependencias nuevas). Email único por ejecución (UUID) garantiza independencia entre escenarios.

## Technical Context

**Language/Version**: Java 21  
**Primary Dependencies**: Serenity BDD 4.2.0 · Serenity Cucumber 4.2.0 · Cucumber Java 7.18.0 · JUnit 4.13.2 · Selenium WebDriver (Chrome, gestionado por Serenity)  
**Storage**: N/A — suite de pruebas sin persistencia propia  
**Testing**: Cucumber 7.18.0 + JUnit 4.13.2 + Serenity BDD 4.2.0  
**Target Platform**: Linux desktop · Chrome browser (modo no headless, configurable vía `serenity.conf`)  
**Project Type**: Suite de automatización UI (test-only, Gradle)  
**Performance Goals**: Tiempo total de ejecución de CP-HU1-01 y CP-HU1-08 < 60 segundos por carrera  
**Constraints**: No modificar el frontend Vue 3 · No añadir nuevas dependencias Gradle · No alterar `gestion_eventos.feature` ni `GestionEventosStepDefinitions.java` · Campos del formulario localizados por `id`; bot\u00f3n submit y mensajes de feedback localizados por selector CSS (clases Tailwind)  
**Scale/Scope**: 2 casos de prueba obligatorios (CP-HU1-01, CP-HU1-08) dentro de 1 feature file nuevo (`autenticacion.feature`)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Gate | Regla (Constitución) | Estado | Evidencia |
|------|----------------------|--------|-----------|
| Stack Java 21 | Lenguaje Java 21 | ✅ PASA | `build.gradle` → `JavaLanguageVersion.of(21)` |
| Serenity BDD 4.2.0 | Framework de automatización | ✅ PASA | `build.gradle` → `serenityVersion = '4.2.0'` |
| Cucumber 7.18.0 + JUnit 4.13.2 | Runner de pruebas | ✅ PASA | `cucumberVersion = '7.18.0'`, `junit:junit:4.13.2` |
| POM + @FindBy | Patrón UI obligatorio | ✅ PASA | `RegisterPage` usará `@FindBy(id=...)` + `PageFactory.initElements` |
| Sin patrón Screenplay | Prohibido en este repo | ✅ PASA | No se usan `@Step`, `Actor` ni tasks |
| Sin nuevas dependencias | Sin nuevas dependencias Gradle | ✅ PASA | Hook `@Before` usa `java.net.http.HttpClient` (Java 21 built-in) |
| Arquitectura de carpetas | `ui/`, `stepdefinitions/`, `hooks/`, `util/` | ✅ PASA | Nuevas clases van a los paquetes correctos |
| Sin aserciones en Page Objects | Solo interacciones encapsuladas | ✅ PASA | `RegisterPage` solo expone acciones; aserciones en `AutenticacionStepDefinitions` |
| Sin selectores en step defs | `@FindBy` obligatorio | ✅ PASA | Selectores declarados únicamente en `RegisterPage` |
| Feature existente intacta | `gestion_eventos.feature` no se toca | ✅ PASA | Nuevo archivo `autenticacion.feature` es independiente |
| Gherkin declarativo en español | Escenarios de negocio, no pasos técnicos | ✅ PASA | Los escenarios describen comportamiento, no clicks |
| Independencia de escenarios | Sin dependencias entre casos | ✅ PASA | Email único por ejecución (UUID) en CP-HU1-08 |

**Resultado**: Todos los gates pasan. Sin violaciones. No se requiere tabla de Complexity Tracking.

**Re-check post-diseño (Phase 1)**: Los contratos Gherkin y el data model confirman el cumplimiento. Veredicto sin cambios.

## Project Structure

### Documentation (this feature)

```text
specs/001-pom-register-form/
├── plan.md              ← este archivo
├── research.md          ← Phase 0 (generado)
├── data-model.md        ← Phase 1 (generado)
├── quickstart.md        ← Phase 1 (generado)
├── contracts/
│   ├── autenticacion.feature   ← contrato Gherkin
│   └── auth-api-schema.md      ← esquema POST /api/auth/register
└── tasks.md             ← Phase 2 (/speckit.tasks — NO creado aún)
```

### Source Code (repository root)

```text
src/test/
├── java/com/ticketing/
│   ├── ui/
│   │   └── RegisterPage.java                     ← NUEVO
│   ├── stepdefinitions/
│   │   ├── GestionEventosStepDefinitions.java     ← NO TOCAR
│   │   └── AutenticacionStepDefinitions.java      ← NUEVO
│   ├── runners/
│   │   └── CucumberTestRunner.java               ← MODIFICAR: añadir "com.ticketing.hooks" al glue
│   ├── hooks/
│   │   └── AutenticacionHooks.java               ← NUEVO
│   └── util/
│       └── ApiHelper.java                        ← NUEVO
└── resources/
    ├── features/
    │   ├── gestion_eventos.feature               ← NO TOCAR
    │   └── autenticacion.feature                 ← NUEVO
    ├── serenity.conf                              ← NO TOCAR
    └── logback-test.xml                          ← NO TOCAR
```

**Structure Decision**: Proyecto único existente. Los nuevos archivos se insertan en los paquetes definidos por la constitución. El `glue` del `CucumberTestRunner` se extiende de `"com.ticketing.stepdefinitions"` a `{"com.ticketing.stepdefinitions", "com.ticketing.hooks"}` para que Cucumber registre los hooks `@Before`.

## Complexity Tracking

> Sin violaciones de constitución — tabla no requerida.
