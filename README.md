# AUTO_FRONT_POM_FACTORY

Proyecto de automatización Front-End para el flujo público de compra de tickets del sistema Ticketing.

La solución fue construida con Java 21, Gradle, Serenity BDD, Selenium, Cucumber y JUnit 4, siguiendo patrón POM con Page Factory mediante `@FindBy`.

## Objetivo

Este proyecto valida el comportamiento visible del flujo público de compra de tickets en la aplicación web.

Actualmente cubre dos escenarios principales:

- Inicio exitoso de compra con datos válidos hasta el paso `Completar Pago`
- Bloqueo de compra cuando el usuario omite el email y el sistema muestra `El email es requerido`

## Alcance funcional automatizado

La automatización valida sobre la UI real:

- acceso al listado público de eventos en `/buy`
- navegación al detalle de compra `/buy/{id}`
- diligenciamiento del formulario de compra
- transición al paso `Completar Pago`
- validación obligatoria del email

## Stack técnico

- Java 21
- Gradle
- Serenity BDD
- Selenium WebDriver
- Cucumber
- JUnit 4
- WebDriverManager

## Estructura del proyecto

```text
src/
  test/
    java/
      pages/
      runners/
      steps/
      support/
    resources/
      features/
        purchase/
      serenity.conf
build.gradle
settings.gradle
README.md
```

## Escenarios automatizados

Archivo feature:

- [src/test/resources/features/purchase/reserva-compra-tickets.feature](src/test/resources/features/purchase/reserva-compra-tickets.feature)

Escenarios incluidos:

1. Compra válida de tickets
2. Bloqueo por email requerido

## Repositorios relacionados

Repositorios base relacionados con esta automatización.

### Repositorio principal de automatización

- Automatización UI: https://github.com/tu-organizacion/auto-front-pom-factory

### Servicios de los que depende

- Frontend Ticketing: https://github.com/emolinaf99/FrontendTicketing
- Backend Ticketing: https://github.com/Jomruizgo/ticketing_project_week1

> Nota: el repositorio backend agrupa los servicios requeridos por la automatización, incluyendo la API CRUD, Producer Service y servicios de soporte del flujo de tickets.

## Dependencias de ejecución

Para que la automatización funcione, el sistema bajo prueba debe estar disponible localmente.

### URLs esperadas

- Frontend: `http://localhost:3000`
- CRUD API: `http://localhost:8002`
- Producer API: `http://localhost:8001`

### Dependencias funcionales

El proyecto depende de que:

- exista un frontend accesible
- el CRUD Service permita preparar datos de prueba
- el Producer Service esté disponible
- la infraestructura de soporte del sistema Ticketing esté levantada

> Nota: la automatización prepara los datos necesarios por API antes de cada escenario para mantener independencia entre pruebas.

## Prerrequisitos

Instala o valida lo siguiente antes de ejecutar:

- Java 21
- Gradle Wrapper incluido en el repositorio
- Google Chrome instalado
- conectividad a los servicios locales requeridos

Verifica Java:

```bash
java -version
```

## Cómo clonar el proyecto

```bash
git clone https://github.com/tu-organizacion/auto-front-pom-factory.git
cd auto-front-pom-factory
```

## Cómo clonar también los servicios dependientes

Ejemplo de estructura local sugerida:

```text
workspace/
  auto-front-pom-factory/
  FrontendTicketing/
  ticketing_project_week1/
```

Ejemplo de clonado:

```bash
git clone https://github.com/tu-organizacion/auto-front-pom-factory.git
git clone https://github.com/emolinaf99/FrontendTicketing.git
git clone https://github.com/Jomruizgo/ticketing_project_week1.git
```

## Levantamiento del sistema bajo prueba

Debes iniciar primero los servicios del ecosistema Ticketing según la documentación de estos repositorios.

Orden recomendado:

1. Infraestructura base
2. CRUD Service
3. Reservation Service
4. Producer Service
5. Payment Service
6. Frontend

Repositorios a consultar para el levantamiento:

- Frontend: https://github.com/emolinaf99/FrontendTicketing
- Backend: https://github.com/Jomruizgo/ticketing_project_week1

Cuando todo esté listo, valida al menos:

- `http://localhost:3000/buy`
- `http://localhost:8002/api/events`
- `http://localhost:8001/health`

## Configuración del proyecto

La configuración principal de ejecución está en:

- [src/test/resources/serenity.conf](src/test/resources/serenity.conf)
- [build.gradle](build.gradle)

Ahí se definen, entre otros:

- URL base del frontend
- URLs de APIs de soporte
- datos base de prueba
- comportamiento del reporte Serenity

## Cómo ejecutar las pruebas

Desde la raíz del proyecto:

### Ejecutar toda la suite y generar un único informe

```bash
./gradlew clean test aggregate
```

### Ejecutar solo el escenario positivo

```bash
./gradlew clean test -Dcucumber.filter.tags='@happy-path'
```

### Ejecutar solo el escenario negativo

```bash
./gradlew clean test -Dcucumber.filter.tags='@error-path'
```

### Abrir el reporte generado

```bash
./gradlew openSerenityReport
```

## Reportes

La ejecución genera un reporte consolidado de Serenity en:

- [target/site/serenity/index.html](target/site/serenity/index.html)

Resumen rápido:

- [target/site/serenity/summary.txt](target/site/serenity/summary.txt)

## Arquitectura de automatización

### Componentes principales

- `BuyPage`: modela la pantalla pública de compra
- `BuyerEventPage`: modela el formulario de compra del evento
- `TicketPurchaseSteps`: concentra las acciones de negocio anotadas con `@Step`
- `PurchaseStepDefinitions`: enlaza Gherkin con la capa de steps
- `TestDataSetup`: prepara datos por API antes de cada escenario
- `CucumberTestRunner`: ejecuta la suite con Serenity

## Convenciones del proyecto

- un único archivo `.feature` para los dos escenarios definidos
- selectores centralizados en Page Objects con `@FindBy`
- steps orientados a negocio
- escenarios independientes entre sí
- datos de prueba preparados por API

## Solución de problemas

### El frontend no responde

Verifica que el servicio frontend esté levantado y responda en `http://localhost:3000/buy`.

### El CRUD API falla al preparar datos

Verifica que el servicio responda en `http://localhost:8002` y que la infraestructura asociada esté arriba.

### El reporte no se abre automáticamente

Puedes abrirlo manualmente desde:

- [target/site/serenity/index.html](target/site/serenity/index.html)

### Advertencias de Chrome DevTools

Puede aparecer una advertencia de compatibilidad CDP según la versión local de Chrome. Si las pruebas pasan, esta advertencia no bloquea la ejecución.

## Próximas mejoras sugeridas

- parametrización por ambientes
- integración con pipeline CI/CD
- ejecución headless configurable por perfil
- más validaciones funcionales del flujo de reserva y pago

## Autoría

Proyecto de automatización funcional UI para el ecosistema Ticketing.

Si más adelante quieres, se pueden agregar en este README los comandos exactos de despliegue para esos dos repositorios.
