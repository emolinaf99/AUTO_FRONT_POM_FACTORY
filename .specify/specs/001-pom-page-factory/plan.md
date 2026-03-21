# Implementation Plan: Front-End Automation Ticket Purchase Flow

**Branch**: `001-pom-page-factory` | **Date**: 2026-03-20 | **Spec**: `.specify/specs/001-pom-page-factory/spec.md`
**App context**: `.specify/memory/app-context.md`

## Summary

Implementar un proyecto de automatización Front-End sobre el flujo público real de compra de tickets
usando Serenity BDD + Selenium con patrón POM y Page Factory (`@FindBy`).
La automatización cubrirá exactamente 2 escenarios Gherkin dentro de una sola feature:

- **US1 (positivo)**: iniciar una compra válida y avanzar a `Completar Pago`
- **US2 (negativo)**: intentar comprar sin email y recibir la validación `El email es requerido`

## Technical Context

**Language/Version**: Java 21
**Primary Dependencies**: Serenity BDD 4.2.9, Selenium, WebDriverManager 5.9.1, JUnit 4.13.2, Cucumber
**Storage**: N/A
**Testing**: JUnit 4 + Cucumber (`CucumberWithSerenity`) + Serenity aggregate reports
**Target Platform**: Chrome sobre `http://localhost:3000` (Frontend Next.js)
**Supporting APIs**: CRUD Service `http://localhost:8002`, Producer Service `http://localhost:8001`
**Project Type**: Test automation — Front-End UI
**Constraints**: 2 escenarios en 1 `.feature`, sin `driver.findElement`, sin código comentado, datos de prueba controlados por API
**Scale/Scope**: 2 Page Objects (`BuyPage`, `BuyerEventPage`), 1 Steps class, 1 Runner, 1 helper de datos

## Constitution Check

- ✅ I. Spec-First: `spec.md` aprobado y alineado al sistema real antes de crear código
- ✅ II. Java + Serenity BDD: stack confirmado
- ✅ III. POM + `@FindBy`: patrón confirmado, sin localizadores en Steps
- ✅ IV. Código Limpio: configuración declarativa y sin lógica de negocio en el runner
- ✅ V. Escenarios Independientes: 2 escenarios en una sola feature, ejecutables de forma aislada

## Test Data Strategy

El sistema real no garantiza datos permanentes de eventos futuros con tickets disponibles,
por lo que la automatización preparará sus precondiciones por API antes de cada escenario:

- Crear o asegurar un evento futuro mediante CRUD Service.
- Crear tickets disponibles para ese evento.
- Reutilizar únicamente los identificadores generados en el escenario actual.

Esto elimina fragilidad sobre datos manuales y asegura independencia entre escenarios.

## Project Structure

```text
src/
  test/
    java/
      runners/
        CucumberTestRunner.java
      steps/
        TicketPurchaseSteps.java
      pages/
        BuyPage.java
        BuyerEventPage.java
      support/
        TestDataSetup.java
    resources/
      features/
        purchase/
          reserva-compra-tickets.feature
      serenity.conf

build.gradle
```

## build.gradle

```groovy
plugins {
    id 'java'
    id 'net.serenity-bdd.serenity-gradle-plugin' version '4.2.9'
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'net.serenity-bdd:serenity-core:4.2.9'
    testImplementation 'net.serenity-bdd:serenity-junit:4.2.9'
    testImplementation 'net.serenity-bdd:serenity-cucumber:4.2.9'
    testImplementation 'io.github.bonigarcia:webdrivermanager:5.9.1'
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'org.assertj:assertj-core:3.25.3'
}

test {
    testLogging.showStandardStreams = true
    systemProperties System.getProperties()
}

gradle.startParameter.continueOnFailure = true
test.finalizedBy(aggregate)
```

## serenity.conf

```hocon
webdriver {
    driver = chrome
    autodownload = true
    base.url = "http://localhost:3000"
}

headless.mode = false

serenity {
    project.name = "AUTO_FRONT_POM_FACTORY"
    test.root = "runners"
}

environments {
    default {
        api {
            crud.url = "http://localhost:8002"
            producer.url = "http://localhost:8001"
        }
        testdata {
            buyer.email = "comprador@ejemplo.com"
            reservation.expiresInSeconds = 300
        }
    }
}
```

## Feature File

**Archivo**: `src/test/resources/features/purchase/reserva-compra-tickets.feature`

```gherkin
#language: es
Característica: Compra pública de tickets
  Como comprador de tickets
  Quiero iniciar o bloquear una compra según la validez del formulario
  Para avanzar correctamente al pago o recibir validaciones claras

  @happy-path @critico
  Escenario: El usuario inicia la compra con datos válidos
    Dado que existe un evento futuro con tickets disponibles preparado para la prueba
    Y el usuario navega a la vista pública de compra
    Cuando selecciona el evento disponible e ingresa una cantidad válida, un email válido y un tiempo de expiración mayor a 0
    Entonces el sistema avanza al paso Completar Pago

  @error-path
  Escenario: El sistema bloquea la compra cuando falta el email
    Dado que existe un evento futuro con tickets disponibles preparado para la prueba
    Y el usuario se encuentra en el formulario de compra del evento
    Cuando intenta comprar tickets sin ingresar email
    Entonces el sistema muestra el mensaje El email es requerido
    Y el usuario permanece en el formulario sin avanzar a Completar Pago
```

## Complexity Tracking

| Violación | Por qué se necesita | Alternativa rechazada |
|---|---|---|
| N/A | Sin violaciones constitucionales | N/A |
